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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.sdk.core.ISDKConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Gregory Amerson
 * @author Kamesh Sampath
 */
public class HookPluginFacetInstall extends PluginFacetInstall {

	@Override
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
		throws CoreException {

		super.execute(project, fv, config, monitor);

		IDataModel model = (IDataModel)config;

		IDataModel masterModel = (IDataModel)model.getProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM);

		if ((masterModel != null) && masterModel.getBooleanProperty(CREATE_PROJECT_OPERATION)) {

			// IDE-1122 SDK creating project has been moved to Class NewPluginProjectWizard

			String hookName = this.masterModel.getStringProperty(HOOK_NAME);

			IPath projectTempPath = (IPath)masterModel.getProperty(PROJECT_TEMP_PATH);

			processNewFiles(projectTempPath.append(hookName + ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX));

			FileUtil.deleteDir(projectTempPath.toFile(), true);

			// End IDE-1122

			try {
				this.project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
			catch (Exception e) {
				ProjectCore.logError(e);
			}
		}
		else if (shouldSetupDefaultOutputLocation()) {
			setupDefaultOutputLocation();
		}

		// IDE-491 don't add this in the webxml by default

		if (shouldConfigureDeploymentAssembly()) {

			// IDE-565

			configureDeploymentAssembly(IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER, DEFAULT_DEPLOY_PATH);
		}
	}

	@Override
	protected String getDefaultOutputLocation() {
		return IPluginFacetConstants.HOOK_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER;
	}

}