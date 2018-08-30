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

package com.liferay.ide.project.core.facet;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.sdk.core.ISDKConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 * @author Kamesh Sampath [IDE-450]
 */
@SuppressWarnings("restriction")
public class PortletPluginFacetInstall extends PluginFacetInstall {

	/**
	 * IDE-815 Now with contributed portlet_2_0 uri we no longer need to copy TLD to
	 * user's project
	 */
	@Override
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
		throws CoreException {

		super.execute(project, fv, config, monitor);

		IDataModel model = (IDataModel)config;

		IDataModel masterModel = (IDataModel)model.getProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM);

		if ((masterModel != null) && masterModel.getBooleanProperty(CREATE_PROJECT_OPERATION)) {

			// IDE-1122 SDK creating project has been moved to Class NewPluginProjectWizard

			String portletName = this.masterModel.getStringProperty(PORTLET_NAME);

			IPath projectTempPath = (IPath)masterModel.getProperty(PROJECT_TEMP_PATH);

			processNewFiles(projectTempPath.append(portletName + ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX));

			FileUtil.deleteDir(projectTempPath.toFile(), true);

			// End IDE-1122

			try {
				this.project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
			catch (Exception e) {
				ProjectCore.logError(e);
			}

			if (masterModel.getBooleanProperty(PLUGIN_FRAGMENT_ENABLED)) {
				IDataModel fragmentModel = masterModel.getNestedModel(PLUGIN_FRAGMENT_DM);

				if (fragmentModel != null) {

					// IDE-205 remove view.jsp

					try {
						if (fragmentModel.getBooleanProperty(
								IPluginWizardFragmentProperties.REMOVE_EXISTING_ARTIFACTS)) {

							// IDE-110 IDE-648

							IWebProject lrproject = LiferayCore.create(IWebProject.class, project);

							IFolder webappRoot = lrproject.getDefaultDocrootFolder();

							if (FileUtil.exists(webappRoot)) {
								IFile viewJsp = webappRoot.getFile(new Path("view.jsp"));

								if (FileUtil.exists(viewJsp)) {
									viewJsp.delete(true, monitor);
								}
							}
						}
					}
					catch (Exception ex) {
						ProjectCore.logError("Error deleting view.jsp", ex);
					}
				}
			}
		}
		else if (shouldSetupDefaultOutputLocation()) {
			setupDefaultOutputLocation();
		}

		/*
		 * IDE-815 Now with contributed portlet_2_0 uri we no longer need to copy TLD to
		 * user's project
		 */
		try {
			this.project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (Exception e1) {
			ProjectCore.logError(e1);
		}

		// IDE-417 set a project preference for disabling JSP fragment validation

		try {
			JSPCorePlugin jspCorePlugin = JSPCorePlugin.getDefault();

			Bundle bundle = jspCorePlugin.getBundle();

			IEclipsePreferences node = new ProjectScope(this.project).getNode(bundle.getSymbolicName());

			node.putBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, false);

			node.putBoolean(JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS, true);

			node.flush();
		}
		catch (Exception e) {
			ProjectCore.logError("Could not store jsp fragment validation preference", e);
		}

		if (shouldConfigureDeploymentAssembly()) {

			// IDE-565

			configureDeploymentAssembly(IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER, DEFAULT_DEPLOY_PATH);
		}
	}

	@Override
	protected String getDefaultOutputLocation() {
		return IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER;
	}

}