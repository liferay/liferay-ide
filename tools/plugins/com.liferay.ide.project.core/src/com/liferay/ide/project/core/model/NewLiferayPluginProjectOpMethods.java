/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.project.core.model;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.descriptor.RemoveSampleElementsOperation;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Tao Tao
 * @author Kuo Zhang
 * @author Terry Jia
 */
public class NewLiferayPluginProjectOpMethods {

	public static boolean canUseCustomLocation(NewLiferayPluginProjectOp op) {
		boolean retval = false;

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> projectProvider = op.getProjectProvider().content(true);

		if (projectProvider.getShortName().equals("maven")) {
			retval = true;
		}

		return retval;
	}

	public static final Status execute(NewLiferayPluginProjectOp op, ProgressMonitor pm) {
		IProgressMonitor monitor = ProgressMonitorBridge.create(pm);

		monitor.beginTask("Creating Liferay plugin project (this process may take several minutes)", 100);

		Status retval = null;

		try {
			NewLiferayProjectProvider<NewLiferayPluginProjectOp> projectProvider =
				op.getProjectProvider().content(true);

			// IDE-1306 If the user types too quickly all the model changes may not have propagated

			Path projectLocation = op.getLocation().content();

			updateLocation(op, projectLocation);

			IStatus status = projectProvider.createNewProject(op, monitor);

			if (status.isOK()) {
				_updateProjectPrefs(op);

				_removeSampleCodeAndFiles(op);

				op.setImportProjectStatus(true);
			}

			retval = StatusBridge.create(status);
		}
		catch (Exception e) {
			String msg = "Error creating Liferay plugin project.";

			ProjectCore.logError(msg, e);

			return Status.createErrorStatus(msg + " Please see Eclipse error log for more details.", e);
		}

		return retval;
	}

	public static String getFrameworkName(NewLiferayPluginProjectOp op) {
		IPortletFramework portletFramework = op.getPortletFramework().content();

		String frameworkName = portletFramework.getShortName();

		if (portletFramework.isRequiresAdvanced()) {
			IPortletFramework framework = op.getPortletFrameworkAdvanced().content();

			frameworkName = framework.getShortName();
		}

		return frameworkName;
	}

	public static String getMavenParentPomGroupId(NewLiferayPluginProjectOp op, String projectName, IPath path) {
		String retval = null;

		File parentProjectDir = path.toFile();

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = op.getProjectProvider().content(true);

		IStatus locationStatus = provider.validateProjectLocation(projectName, path);

		if (locationStatus.isOK() && FileUtil.hasChildren(parentProjectDir)) {
			List<String> groupId = provider.getData("parentGroupId", String.class, parentProjectDir);

			if (ListUtil.isNotEmpty(groupId)) {
				retval = groupId.get(0);
			}
		}

		return retval;
	}

	public static String getMavenParentPomVersion(NewLiferayPluginProjectOp op, String projectName, IPath path) {
		String retval = null;

		File parentProjectDir = path.toFile();

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = op.getProjectProvider().content(true);

		IStatus locationStatus = provider.validateProjectLocation(projectName, path);

		if (locationStatus.isOK() && FileUtil.hasChildren(parentProjectDir)) {
			List<String> version = provider.getData("parentVersion", String.class, parentProjectDir);

			if (ListUtil.isNotEmpty(version)) {
				retval = version.get(0);
			}
		}

		return retval;
	}

	public static String getPluginTypeSuffix(PluginType pluginType) {
		String suffix = null;

		switch (pluginType) {
			case portlet:
			case servicebuilder:
				suffix = "-portlet";

				break;
			case ext:
				suffix = "-ext";

				break;
			case hook:
				suffix = "-hook";

				break;
			case layouttpl:
				suffix = "-layouttpl";

				break;
			case theme:
				suffix = "-theme";

				break;
			case web:
				suffix = "-web";

				break;
		}

		return suffix;
	}

	public static Set<String> getPossibleProfileIds(NewLiferayPluginProjectOp op, boolean includeNewProfiles) {
		String activeProfilesValue = op.getActiveProfilesValue().content();

		Path currentLocation = op.getLocation().content();

		File param = currentLocation != null ? currentLocation.toFile() : null;

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = op.getProjectProvider().content(true);

		List<String> systemProfileIds = provider.getData("profileIds", String.class, param);

		ElementList<NewLiferayProfile> newLiferayProfiles = op.getNewLiferayProfiles();

		Set<String> possibleProfileIds = new HashSet<>();

		if (!CoreUtil.isNullOrEmpty(activeProfilesValue)) {
			String[] vals = activeProfilesValue.split(",");

			if (ListUtil.isNotEmpty(vals)) {
				for (String val : vals) {
					if (!possibleProfileIds.contains(val) && !val.contains(StringPool.SPACE)) {
						possibleProfileIds.add(val);
					}
				}
			}
		}

		if (ListUtil.isNotEmpty(systemProfileIds)) {
			for (Object systemProfileId : systemProfileIds) {
				if (systemProfileId != null) {
					String val = systemProfileId.toString();

					if (!possibleProfileIds.contains(val) && !val.contains(StringPool.SPACE)) {
						possibleProfileIds.add(val);
					}
				}
			}
		}

		if (includeNewProfiles) {
			for (NewLiferayProfile newLiferayProfile : newLiferayProfiles) {
				String newId = newLiferayProfile.getId().content();

				if (!CoreUtil.isNullOrEmpty(newId) && !possibleProfileIds.contains(newId) &&
					!newId.contains(StringPool.SPACE)) {

					possibleProfileIds.add(newId);
				}
			}
		}

		return possibleProfileIds;
	}

	public static String getProjectNameWithSuffix(NewLiferayPluginProjectOp op) {
		String projectName = op.getProjectName().content();

		String suffix = null;

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = op.getProjectProvider().content(true);

		if ((projectName != null) && "ant".equals(provider.getShortName())) {
			suffix = getPluginTypeSuffix(op.getPluginType().content(true));

			if (suffix != null) {

				// check if project name already contains suffix

				if (projectName.endsWith(suffix)) {
					suffix = null;
				}
			}
		}

		return (projectName == null ? StringPool.EMPTY : projectName) + (suffix == null ? StringPool.EMPTY : suffix);
	}

	public static boolean supportsTypePlugin(NewLiferayPluginProjectOp op, String type) {
		boolean retval = false;

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = op.getProjectProvider().content(true);

		if (provider.getShortName().equals("maven") && (type.equals("web") || type.equals("theme"))) {
			return true;
		}
		else {
			SDK sdk = null;

			try {
				sdk = SDKUtil.getWorkspaceSDK();
			}
			catch (CoreException ce) {
			}

			if (sdk == null) {
				Path sdkLocation = op.getSdkLocation().content();

				if (sdkLocation != null) {
					sdk = SDKUtil.createSDKFromLocation(PathBridge.create(sdkLocation));
				}
			}

			if (sdk == null) {
				return true;
			}

			Version version = new Version(sdk.getVersion());

			boolean greaterThan700 = false;

			if (CoreUtil.compareVersions(version, ILiferayConstants.V700) >= 0) {
				greaterThan700 = true;
			}

			if ((greaterThan700 && "web".equals(type)) || "theme".equals(type)) {
				retval = true;
			}

			if (greaterThan700 && "ext".equals(type)) {
				IPath extFolder = sdk.getLocation().append("ext");

				File buildXml = extFolder.append("build.xml").toFile();

				if (FileUtil.exists(extFolder) && FileUtil.exists(buildXml)) {
					return true;
				}
			}
			else if (!greaterThan700 && "ext".equals(type)) {
				return true;
			}
		}

		return retval;
	}

	public static void updateActiveProfilesValue(NewLiferayPluginProjectOp op, ElementList<Profile> profiles) {
		StringBuilder sb = new StringBuilder();

		if (ListUtil.isNotEmpty(profiles)) {
			for (Profile profile : profiles) {
				if (!profile.getId().empty()) {
					sb.append(profile.getId().content());
					sb.append(',');
				}
			}
		}

		// remove trailing ','

		op.setActiveProfilesValue(sb.toString().replaceAll("(.*),$", "$1"));
	}

	public static void updateLocation(NewLiferayPluginProjectOp op) {
		String currentProjectName = op.getProjectName().content();

		if (currentProjectName == null) {
			return;
		}

		boolean useDefaultLocation = op.getUseDefaultLocation().content(true);

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = op.getProjectProvider().content(true);

		String providerShortName = provider.getShortName();

		if (useDefaultLocation) {
			Path newLocationBase = null;

			if (providerShortName.equals("ant")) {
				SDK sdk = null;

				try {
					sdk = SDKUtil.getWorkspaceSDK();

					if (sdk != null) {
						IStatus sdkStatus = sdk.validate();

						if (!sdkStatus.isOK()) {
							sdk = null;
						}
					}
				}
				catch (CoreException ce) {
				}

				if (sdk == null) {
					if (op.getSdkLocation() != null) {
						Path sdkPath = op.getSdkLocation().content();

						if (sdkPath != null) {
							IPath sdkLocation = PathBridge.create(sdkPath);

							sdk = SDKUtil.createSDKFromLocation(sdkLocation);
						}
					}
				}

				if (sdk != null) {
					Path sdkLocation = PathBridge.create(sdk.getLocation());

					switch (op.getPluginType().content(true)) {
						case portlet:
						case servicebuilder:
							newLocationBase = sdkLocation.append("portlets");

							break;
						case ext:
							newLocationBase = sdkLocation.append("ext");

							break;
						case hook:
							newLocationBase = sdkLocation.append("hooks");

							break;
						case layouttpl:
							newLocationBase = sdkLocation.append("layouttpl");

							break;
						case theme:
							newLocationBase = sdkLocation.append("themes");

							break;
						case web:
							newLocationBase = sdkLocation.append("webs");

							break;
					}
				}
				else {
					return;
				}
			}
			else {
				newLocationBase = PathBridge.create(CoreUtil.getWorkspaceRoot().getLocation());
			}

			if (newLocationBase != null) {
				updateLocation(op, newLocationBase);
			}
		}
	}

	public static void updateLocation(NewLiferayPluginProjectOp op, Path baseLocation) {
		String projectName = getProjectNameWithSuffix(op);

		if (baseLocation == null) {
			return;
		}

		Path newLocation = baseLocation.append(projectName);

		op.setLocation(newLocation);
	}

	private static IStatus _removeSampleCodeAndFiles(NewLiferayPluginProjectOp op) {
		IStatus status = org.eclipse.core.runtime.Status.OK_STATUS;

		boolean includeSampleCode = op.getIncludeSampleCode().content();

		if (includeSampleCode) {
			return status;
		}

		IProject project = CoreUtil.getLiferayProject(op.getFinalProjectName().content());

		if (FileUtil.exists(project)) {
			ProjectCore.operate(project, RemoveSampleElementsOperation.class);

			// delete sample files: view.jsp, main.css, main.js

			try {
				IWebProject webproject = LiferayCore.create(IWebProject.class, project);

				if (webproject == null) {
					return status;
				}

				IFolder docroot = webproject.getDefaultDocrootFolder();

				IFile[] sampleFiles =
					{docroot.getFile("view.jsp"), docroot.getFile("css/main.css"), docroot.getFile("js/main.js")};

				for (IFile file : sampleFiles) {
					if (FileUtil.exists(file)) {
						file.delete(true, new NullProgressMonitor());

						if (file.getParent().members().length == 0) {
							CoreUtil.deleteResource(file.getParent());
						}
					}
				}
			}
			catch (CoreException ce) {
				ProjectCore.logError("Error deleting sample files.", ce);
			}
		}

		return status;
	}

	private static void _updateProjectPrefs(NewLiferayPluginProjectOp op) {
		try {
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID);

			prefs.put(ProjectCore.PREF_DEFAULT_PLUGIN_PROJECT_BUILD_TYPE_OPTION, op.getProjectProvider().text());
			prefs.putBoolean(ProjectCore.PREF_INCLUDE_SAMPLE_CODE, op.getIncludeSampleCode().content());
			prefs.putBoolean(ProjectCore.PREF_CREATE_NEW_PORLET, op.getCreateNewPortlet().content());

			if ("maven".equalsIgnoreCase(op.getProjectProvider().text())) {
				prefs.put(ProjectCore.PREF_DEFAULT_PLUGIN_PROJECT_MAVEN_GROUPID, op.getGroupId().content());
			}

			prefs.flush();
		}
		catch (Exception e) {
			String msg = "Error updating default project build type.";

			ProjectCore.logError(msg, e);
		}
	}

}