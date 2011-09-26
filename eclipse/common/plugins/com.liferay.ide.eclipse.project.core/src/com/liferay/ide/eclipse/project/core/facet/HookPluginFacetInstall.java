/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay and Accenture Services Pvt., Ltd, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.project.core.facet;

import com.liferay.ide.eclipse.core.util.FileUtil;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Greg Amerson
 * @author kamesh.sampath [IDE-450]
 */
public class HookPluginFacetInstall extends PluginFacetInstall {

	@Override
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
		throws CoreException {
		
		super.execute(project, fv, config, monitor);

		IDataModel model = (IDataModel) config;
		
		IDataModel masterModel = (IDataModel) model.getProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM);
		
		if (masterModel != null && masterModel.getBooleanProperty(CREATE_PROJECT_OPERATION)) {
			SDK sdk = getSDK();

			String hookName = this.masterModel.getStringProperty(HOOK_NAME);
			
			// FIX IDE-450
			if ( hookName.endsWith( ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX ) ) {
				hookName = hookName.substring( 0, hookName.indexOf( ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX ) );
			}
			// END FIX IDE-450
			
			String displayName = this.masterModel.getStringProperty(DISPLAY_NAME);

			IPath installPath = sdk.createNewHookProject(hookName, displayName);

			IPath tempInstallPath = installPath.append(hookName + ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX);

			processNewFiles(tempInstallPath, false);
			
			// cleanup hook files
			FileUtil.deleteDir(tempInstallPath.toFile(), true);

			this.project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		else {
			setupDefaultOutputLocation();
		}

		configWebXML();
	}

}
