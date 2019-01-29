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

package com.liferay.ide.theme.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.theme.core.operation.ThemeDescriptorHelper;
import com.liferay.ide.theme.core.util.BuildHelper;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings("rawtypes")
public class ThemeCSSBuilder extends IncrementalProjectBuilder {

	public static final String ID = "com.liferay.ide.eclipse.theme.core.cssBuilder";

	public static IStatus compileTheme(IProject project) throws CoreException {
		SDK sdk = SDKUtil.getSDK(project);

		if (sdk == null) {
			throw new CoreException(
				ThemeCore.createErrorStatus("No SDK for project configured. Could not build theme."));
		}

		IStatus status = sdk.compileThemePlugin(project, null);

		if (!status.isOK()) {
			throw new CoreException(status);
		}

		ensureLookAndFeelFileExists(project);

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		catch (Exception e) {
			ThemeCore.logError(e);
		}

		return status;
	}

	public static void ensureLookAndFeelFileExists(IProject project) throws CoreException {

		// IDE-110 IDE-648

		IWebProject lrProject = LiferayCore.create(IWebProject.class, project);

		if (lrProject == null) {
			return;
		}

		IFile lookAndFeelFile = null;

		IResource res = lrProject.findDocrootResource(
			new Path("WEB-INF/" + ILiferayConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE));

		if (res instanceof IFile && res.exists()) {
			lookAndFeelFile = (IFile)res;
		}

		if (lookAndFeelFile == null) {

			// need to generate a new lnf file in deafult docroot

			String projectName = project.getName();

			String id = projectName.replaceAll(ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX, StringPool.EMPTY);

			IResource propertiesFileRes = lrProject.findDocrootResource(
				new Path("WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE));

			String name = id;

			if (propertiesFileRes instanceof IFile && propertiesFileRes.exists()) {
				Properties props = new Properties();
				IFile propsFile = (IFile)propertiesFileRes;

				try (InputStream contents = propsFile.getContents()) {
					props.load(contents);

					String nameValue = props.getProperty("name");

					if (!CoreUtil.isNullOrEmpty(nameValue)) {
						name = nameValue;
					}

					ThemeDescriptorHelper themeDescriptorHelper = new ThemeDescriptorHelper(project);

					ILiferayProject lProject = lrProject;

					ILiferayPortal portal = lProject.adapt(ILiferayPortal.class);

					String version = "6.2.0";

					if (portal != null) {
						version = portal.getVersion();
					}

					String themeType = lProject.getProperty("theme.type", "vm");

					IFolder folder = lrProject.getDefaultDocrootFolder();

					themeDescriptorHelper.createDefaultFile(folder.getFolder("WEB-INF"), version, id, name, themeType);
				}
				catch (IOException ioe) {
					ThemeCore.logError("Unable to load plugin package properties.", ioe);
				}
			}
		}
	}

	public ThemeCSSBuilder() {
		_buildHelper = new BuildHelper();
	}

	protected void applyDiffsDeltaToDocroot(IResourceDelta delta, IContainer docroot, IProgressMonitor monitor) {
		int deltaKind = delta.getKind();

		switch (deltaKind) {
			case IResourceDelta.REMOVED_PHANTOM:

				break;
		}

		IPath path = FileUtil.getResourceLocation(docroot);

		ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, getProject());

		String themeParent = liferayProject.getProperty("theme.parent", "_styled");

		ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

		if (portal != null) {
			IPath appServerDir = portal.getAppServerPortalDir();

			IPath themesPath = appServerDir.append("html/themes");

			List<IPath> restorePaths = new ArrayList<>();

			for (int i = 0; i < IPluginProjectDataModelProperties.THEME_PARENTS.length; i++) {
				if (IPluginProjectDataModelProperties.THEME_PARENTS[i].equals(themeParent)) {
					restorePaths.add(themesPath.append(IPluginProjectDataModelProperties.THEME_PARENTS[i]));
				}
				else {
					if (ListUtil.isNotEmpty(restorePaths)) {
						restorePaths.add(themesPath.append(IPluginProjectDataModelProperties.THEME_PARENTS[i]));
					}
				}
			}

			new Job("publish theme delta") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					_buildHelper.publishDelta(delta, path, restorePaths.toArray(new IPath[0]), monitor);

					try {
						docroot.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					}
					catch (Exception e) {
						ThemeCore.logError(e);
					}

					return Status.OK_STATUS;
				}

			}.schedule();
		}
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) {
		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			fullBuild(args, monitor);
		}
		else {
			IResourceDelta delta = getDelta(getProject());

			if (delta == null) {
				fullBuild(args, monitor);
			}
			else {
				incrementalBuild(delta, monitor);
			}
		}

		return null;
	}

	protected void fullBuild(Map args, IProgressMonitor monitor) {
		try {
			if (shouldFullBuild(args)) {
				compileTheme(getProject(args));
			}
		}
		catch (Exception e) {
			ThemeCore.logError("Full build failed for Theme CSS Builder", e);
		}
	}

	protected IProject getProject(Map args) {
		return getProject();
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
		int deltaKind = delta.getKind();

		if (deltaKind == IResourceDelta.REMOVED) {
			return;
		}

		try {
			delta.accept(
				new IResourceDeltaVisitor() {

					public boolean visit(IResourceDelta delta) {
						IResource resource = delta.getResource();

						IPath fullResourcePath = resource.getFullPath();

						for (String segment : fullResourcePath.segments()) {
							if ("_diffs".equals(segment)) {

								// IDE-110 IDE-648

								IWebProject webproject = LiferayCore.create(IWebProject.class, getProject());

								if ((webproject != null) && (webproject.getDefaultDocrootFolder() != null)) {
									IFolder webappRoot = webproject.getDefaultDocrootFolder();

									if (webappRoot != null) {
										IFolder diffs = webappRoot.getFolder(new Path("_diffs"));

										if (FileUtil.exists(diffs)) {
											IPath fullPath = diffs.getFullPath();

											if (fullPath.isPrefixOf(fullResourcePath)) {
												applyDiffsDeltaToDocroot(delta, diffs.getParent(), monitor);

												return false;
											}
										}
									}
								}
							}
							else if ("build.xml".equals(segment)) {
								IPath relPath = resource.getProjectRelativePath();

								if ((relPath != null) && (relPath.segmentCount() == 1)) {
									try {
										compileTheme(resource.getProject());
									}
									catch (CoreException ce) {
										ThemeCore.logError("Error compiling theme.", ce);
									}
								}
							}
						}

						return true; // visit children too
					}

				});
		}
		catch (CoreException ce) {
			ThemeCore.logError(ce);
		}
	}

	protected boolean shouldFullBuild(Map args) throws CoreException {
		if (args != null) {
			Object force = args.get("force");

			if ((force != null) && force.equals("true")) {
				return true;
			}
		}

		// check to see if there is any files in the _diffs folder
		// IDE-110 IDE-648

		IWebProject lrproject = LiferayCore.create(IWebProject.class, getProject());

		if ((lrproject != null) && (lrproject.getDefaultDocrootFolder() != null)) {
			IFolder webappRoot = lrproject.getDefaultDocrootFolder();

			if (webappRoot != null) {
				IFolder diffs = webappRoot.getFolder(new Path("_diffs"));

				if ((diffs != null) && diffs.exists()) {
					IResource[] diffMembers = diffs.members();

					if (ListUtil.isNotEmpty(diffMembers)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private BuildHelper _buildHelper;

}