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

package com.liferay.ide.project.core;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.util.ClasspathUtil;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.WizardUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.server.core.IRuntime;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public class PluginsSDKProjectProvider
	extends AbstractLiferayProjectProvider implements NewLiferayProjectProvider<NewLiferayPluginProjectOp> {

	public PluginsSDKProjectProvider() {
		super(new Class<?>[] {IProject.class, IRuntime.class});
	}

	@Override
	public IStatus createNewProject(NewLiferayPluginProjectOp op, IProgressMonitor monitor) throws CoreException {
		ElementList<ProjectName> projectNames = op.getProjectNames();

		PluginType pluginType = op.getPluginType().content(true);

		String originalProjectName = op.getProjectName().content();

		String pluginTypeSuffix = NewLiferayPluginProjectOpMethods.getPluginTypeSuffix(pluginType);

		String fixedProjectName = originalProjectName;

		if (originalProjectName.endsWith(pluginTypeSuffix)) {
			fixedProjectName = originalProjectName.substring(
				0, originalProjectName.length() - pluginTypeSuffix.length());
		}

		String projectName = fixedProjectName;

		String displayName = op.getDisplayName().content(true);

		boolean separateJRE = true;

		SDK sdk = _getSDK(op);

		if (sdk == null) {
			return ProjectCore.createErrorStatus(
				"Can not get correct SDK for " + fixedProjectName + ", Please check SDK configuration setting.");
		}

		// workingDir should always be the directory of the type of plugin /sdk/portlets/ for a portlet, etc

		String workingDir = null;

		ArrayList<String> arguments = new ArrayList<>();

		arguments.add(projectName);
		arguments.add(displayName);

		boolean hasGradleTools = SDKUtil.hasGradleTools(sdk.getLocation());

		IPath newSDKProjectPath = null;

		IPath path = null;

		switch (pluginType) {
			case servicebuilder:
				op.setPortletFramework("mvc");
			case portlet:
				path = sdk.getLocation().append(ISDKConstants.PORTLET_PLUGIN_PROJECT_FOLDER);

				String frameworkName = NewLiferayPluginProjectOpMethods.getFrameworkName(op);

				workingDir = path.toOSString();

				if (hasGradleTools) {
					arguments.add(frameworkName);

					sdk.createNewProject(projectName, arguments, "portlet", workingDir, monitor);
				}
				else {
					newSDKProjectPath = sdk.createNewPortletProject(
						projectName, displayName, frameworkName, separateJRE, workingDir, null, monitor);
				}

				break;

			case hook:
				path = sdk.getLocation().append(ISDKConstants.HOOK_PLUGIN_PROJECT_FOLDER);

				workingDir = path.toOSString();

				if (hasGradleTools) {
					sdk.createNewProject(projectName, arguments, "hook", workingDir, monitor);
				}
				else {
					newSDKProjectPath = sdk.createNewHookProject(
						projectName, displayName, separateJRE, workingDir, null, monitor);
				}

				break;

			case ext:
				path = sdk.getLocation().append(ISDKConstants.EXT_PLUGIN_PROJECT_FOLDER);

				workingDir = path.toOSString();

				if (hasGradleTools) {
					sdk.createNewProject(projectName, arguments, "ext", workingDir, monitor);
				}
				else {
					newSDKProjectPath = sdk.createNewExtProject(
						projectName, displayName, separateJRE, workingDir, null, monitor);
				}

				break;

			case layouttpl:
				path = sdk.getLocation().append(ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_FOLDER);

				workingDir = path.toOSString();

				if (hasGradleTools) {
					sdk.createNewProject(projectName, arguments, "layouttpl", workingDir, monitor);
				}
				else {
					newSDKProjectPath = sdk.createNewLayoutTplProject(
						projectName, displayName, separateJRE, workingDir, null, monitor);
				}

				IProject layoutProject = ProjectUtil.getProject(projectName);

				if (!LiferayDescriptorHelper.getDescriptorVersion(layoutProject).equals("6.2.0")) {
					IPath projectPath = newSDKProjectPath.append(projectName + pluginTypeSuffix);

					IPath fileWap = projectPath.append("docroot").append("blank_columns.wap.tpl");

					if (FileUtil.exists(fileWap)) {
						fileWap.toFile().delete();
					}
				}

				break;

			case theme:
				path = sdk.getLocation().append(ISDKConstants.THEME_PLUGIN_PROJECT_FOLDER);

				workingDir = path.toOSString();

				if (hasGradleTools) {
					sdk.createNewProject(projectName, arguments, "theme", workingDir, monitor);
				}
				else {
					newSDKProjectPath = sdk.createNewThemeProject(
						projectName, displayName, separateJRE, workingDir, null, monitor);
				}

				break;

			case web:
				path = sdk.getLocation().append(ISDKConstants.WEB_PLUGIN_PROJECT_FOLDER);

				workingDir = path.toOSString();

				if (hasGradleTools) {
					sdk.createNewProject(projectName, arguments, "web", workingDir, monitor);
				}
				else {
					newSDKProjectPath = sdk.createNewWebProject(
						projectName, displayName, separateJRE, workingDir, null, monitor);
				}

				break;
		}

		NewLiferayPluginProjectOpMethods.updateLocation(op);

		Path projectLocation = op.getLocation().content();

		if (!hasGradleTools) {
			File projectDir = projectLocation.toFile();

			File projectParent = projectDir.getParentFile();

			projectParent.mkdirs();

			if (FileUtil.notExists(newSDKProjectPath)) {
				return ProjectCore.createErrorStatus(
					"Error create project due to '" + newSDKProjectPath + "' does not exist.");
			}

			File newSDKProjectDir = newSDKProjectPath.toFile();

			try {
				FileUtils.copyDirectory(newSDKProjectDir, projectParent);

				FileUtils.deleteDirectory(newSDKProjectDir);
			}
			catch (IOException ioe) {
				throw new CoreException(ProjectCore.createErrorStatus(ioe));
			}
		}

		ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir(projectLocation.toOSString());

		IProject newProject = ProjectImportUtil.importProject(projectRecord.getProjectLocation(), monitor, op);

		newProject.open(monitor);

		// need to update project name incase the suffix was not correct

		op.setFinalProjectName(newProject.getName());

		projectNames.insert().setName(op.getFinalProjectName().content());

		_projectCreated(newProject);

		switch (op.getPluginType().content()) {
			case portlet:

				_portletProjectCreated(op, newProject, monitor);

				break;

			case servicebuilder:

				PortalBundle bundle = ServerUtil.getPortalBundle(newProject);

				_serviceBuilderProjectCreated(bundle.getVersion(), newProject, monitor);

				break;
			case theme:

				_themeProjectCreated(newProject);

				break;
			default:

				break;
		}

		return Status.OK_STATUS;
	}

	@Override
	public ILiferayProject provide(Object type) {
		if (type instanceof IProject) {
			IProject project = (IProject)type;

			if (!SDKUtil.isSDKProject(project)) {
				return null;
			}

			try {
				IJavaProject javaProject = JavaCore.create(project);

				boolean hasNewSdk = ClasspathUtil.hasNewLiferaySDKContainer(javaProject.getRawClasspath());

				if (hasNewSdk) {
					PortalBundle portalBundle = ServerUtil.getPortalBundle(project);

					if (portalBundle != null) {
						return new PluginsSDKBundleProject(project, portalBundle);
					}
				}
				else {
					ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(project);

					if (liferayRuntime != null) {
						return new PluginsSDKRuntimeProject(project, liferayRuntime);
					}
				}
			}
			catch (CoreException ce) {
			}
		}
		else if (type instanceof IRuntime) {
			try {
				IRuntime runtime = (IRuntime)type;

				ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(runtime);

				if (liferayRuntime != null) {
					return new PluginsSDKRuntimeProject(null, liferayRuntime);
				}
			}
			catch (Exception e) {
			}
		}

		return null;
	}

	@Override
	public IStatus validateProjectLocation(String name, IPath path) {
		IStatus retval = Status.OK_STATUS;

		File file = path.append(".project").toFile();

		if (file.exists()) {
			retval = ProjectCore.createErrorStatus(
				"\"" + path + "\" is not valid because a project already exists at that location.");
		}
		else {
			File pathFile = path.toFile();

			if (pathFile.exists() && pathFile.isDirectory() && (pathFile.listFiles().length > 0)) {
				retval = ProjectCore.createErrorStatus(
					"\"" + path + "\" is not valid because it already contains files.");
			}
		}

		return retval;
	}

	private static SDK _getSDK(NewLiferayPluginProjectOp op) {
		SDK sdk = null;

		try {
			boolean sdkValid = false;

			sdk = SDKUtil.getWorkspaceSDK();

			if (sdk != null) {
				IStatus sdkStatus = sdk.validate();

				if (sdkStatus.isOK()) {
					sdkValid = true;
				}
			}

			if (sdkValid == false) {
				Path sdkLocation = op.getSdkLocation().content(true);

				if (sdkLocation != null) {
					sdk = SDKUtil.createSDKFromLocation(PathBridge.create(sdkLocation));

					if (sdk != null) {
						IStatus sdkStatus = sdk.validate();

						if (sdkStatus.isOK()) {
							sdkValid = true;
						}
					}
				}
			}

			if (!sdkValid) {
				return null;
			}
		}
		catch (CoreException ce) {
			return null;
		}

		return sdk;
	}

	private void _portletProjectCreated(NewLiferayPluginProjectOp op, IProject newProject, IProgressMonitor monitor)
		throws CoreException {

		IPortletFramework portletFramework = op.getPortletFramework().content();

		String portletName = op.getPortletName().content(false);

		String frameworkName = NewLiferayPluginProjectOpMethods.getFrameworkName(op);

		IStatus status = portletFramework.postProjectCreated(newProject, frameworkName, portletName, monitor);

		if (!status.isOK()) {
			throw new CoreException(status);
		}
	}

	private void _projectCreated(IProject project) {
		IFile ivyFile = project.getFile("ivy.xml");

		if (ivyFile.exists()) {
			try {
				String contents = CoreUtil.readStreamToString(ivyFile.getContents());

				contents = contents.replace("${sdk.dir}/ivy.xml", "../../ivy.xml");

				ivyFile.setContents(
					new ByteArrayInputStream(contents.getBytes("UTF-8")), IResource.FORCE, new NullProgressMonitor());
			}
			catch (Exception e) {
				ProjectCore.logError(e);
			}
		}
	}

	private void _serviceBuilderProjectCreated(String version, IProject newProject, IProgressMonitor monitor)
		throws CoreException {

		// create a default service.xml file in the project

		IFile serviceXmlFile = newProject.getFile(ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/service.xml");

		String descriptorVersion = null;

		try {
			Version portalVersion = new Version(version);

			descriptorVersion = portalVersion.getMajor() + "." + portalVersion.getMinor() + ".0";
		}
		catch (Exception e) {
			ProjectCore.logError("Could not determine liferay runtime version", e);

			descriptorVersion = "6.0.0";
		}

		WizardUtil.createDefaultServiceBuilderFile(
			serviceXmlFile, descriptorVersion, true, "com.liferay.sample", "SAMPLE", System.getProperty("user.name"),
			monitor);
	}

	private void _themeProjectCreated(IProject newProject) throws CoreException {
		Map<String, String> args = new HashMap<>();

		args.put("force", "true");

		newProject.build(
			IncrementalProjectBuilder.FULL_BUILD, "com.liferay.ide.eclipse.theme.core.cssBuilder", args, null);
	}

}