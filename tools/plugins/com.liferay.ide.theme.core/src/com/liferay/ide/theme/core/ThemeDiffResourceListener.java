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
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.theme.core.operation.ThemeDescriptorHelper;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class ThemeDiffResourceListener implements IResourceChangeListener {

	public void resourceChanged(IResourceChangeEvent event) {
		if (event == null) {
			return;
		}

		if (shouldProcessResourceChangedEvent(event)) {
			IResourceDelta delta = event.getDelta();

			try {
				delta.accept(
					new IResourceDeltaVisitor() {

						public boolean visit(IResourceDelta delta) throws CoreException {
							if (shouldProcessResourceDelta(delta)) {
								processResourceChanged(delta);

								return false;
							}

							return true;
						}

					});
			}
			catch (CoreException ce) {
			}
		}
	}

	protected boolean isLiferayPluginProject(IPath deltaPath) {
		IFile pluginPackagePropertiesFile = _getWorkspaceFile(deltaPath);

		if ((pluginPackagePropertiesFile != null) && pluginPackagePropertiesFile.exists()) {
			return ProjectUtil.isThemeProject(pluginPackagePropertiesFile.getProject());
		}

		return false;
	}

	protected void processResourceChanged(IResourceDelta delta) throws CoreException {
		new WorkspaceJob(Msgs.compilingTheme) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				IProject project = delta.getResource().getProject();

				SDK sdk = SDKUtil.getSDK(project);

				if (sdk == null) {
					throw new CoreException(
						ThemeCore.createErrorStatus("No SDK for project configured. Could not deploy theme module"));
				}

				IStatus status = sdk.compileThemePlugin(project, null);

				if (!status.isOK()) {
					throw new CoreException(status);
				}

				IWebProject webproject = LiferayCore.create(IWebProject.class, project);

				if (webproject == null) {
					return status;
				}

				// IDE-110 IDE-648

				IResource res = webproject.findDocrootResource(
					new Path("WEB-INF/" + ILiferayConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE));

				IFile lookAndFeelFile = null;

				if ((res != null) && res.exists()) {
					lookAndFeelFile = (IFile)res;
				}

				if (lookAndFeelFile == null) {
					String id = project.getName().replaceAll(
						ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX, StringPool.EMPTY);

					IResource propsRes = webproject.findDocrootResource(
						new Path("WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE));

					String name = id;

					if (propsRes instanceof IFile && propsRes.exists()) {
						Properties props = new Properties();

						try (InputStream inputStream = ((IFile)propsRes).getContents()) {
							props.load(inputStream);

							String nameValue = props.getProperty("name");

							if (!CoreUtil.isNullOrEmpty(nameValue)) {
								name = nameValue;
							}
						}
						catch (IOException ioe) {
							ThemeCore.logError("Unable to load plugin package properties.", ioe);
						}
					}

					ThemeDescriptorHelper themeDescriptorHelper = new ThemeDescriptorHelper(project);

					ILiferayProject lProject = LiferayCore.create(project);

					String type = lProject.getProperty("theme.type", "vm");

					String version = "6.2.0";

					ILiferayPortal portal = lProject.adapt(ILiferayPortal.class);

					if (portal != null) {
						version = portal.getVersion();
					}

					themeDescriptorHelper.createDefaultFile(
						webproject.getDefaultDocrootFolder(), version, id, name, type);

					try {
						webproject.getDefaultDocrootFolder().refreshLocal(IResource.DEPTH_INFINITE, null);
					}
					catch (Exception e) {
						ThemeCore.logError(e);
					}
				}

				return Status.OK_STATUS;
			}

		}.schedule();
	}

	protected boolean shouldProcessResourceChangedEvent(IResourceChangeEvent event) {
		if (event == null) {
			return false;
		}

		IResourceDelta delta = event.getDelta();

		int deltaKind = delta.getKind();

		if ((deltaKind == IResourceDelta.REMOVED) || (deltaKind == IResourceDelta.REMOVED_PHANTOM)) {
			return false;
		}

		return true;
	}

	protected boolean shouldProcessResourceDelta(IResourceDelta delta) {
		IPath fullPath = delta.getFullPath();

		// IDE-110 IDE-648

		IWebProject webproject = LiferayCore.create(IWebProject.class, delta.getResource().getProject());

		if ((webproject == null) || (webproject.getDefaultDocrootFolder() == null)) {
			return false;
		}

		IFolder webappRoot = webproject.getDefaultDocrootFolder();

		IPath diffPath = webappRoot.getFolder(new Path("_diffs")).getFullPath();

		return diffPath.isPrefixOf(fullPath);
	}

	private IFile _getWorkspaceFile(IPath path) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		return root.getFile(path);
	}

	private static class Msgs extends NLS {

		public static String compilingTheme;

		static {
			initializeMessages(ThemeDiffResourceListener.class.getName(), Msgs.class);
		}

	}

}