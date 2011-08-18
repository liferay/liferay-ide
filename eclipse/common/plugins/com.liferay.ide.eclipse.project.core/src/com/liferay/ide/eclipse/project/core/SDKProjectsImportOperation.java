/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.project.core;

import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;
import com.liferay.ide.eclipse.sdk.util.SDKUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author Greg Amerson
 */
public class SDKProjectsImportOperation extends AbstractDataModelOperation
	implements ISDKProjectsImportDataModelProperties {

	List<IProject> createdProjects;

	public SDKProjectsImportOperation(IDataModel model) {
		super(model);

		createdProjects = new ArrayList<IProject>();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
		throws ExecutionException {

		Object[] selectedProjects = (Object[]) getDataModel().getProperty(SELECTED_PROJECTS);

		String sdkLocation = getDataModel().getStringProperty(SDK_LOCATION);

		if (selectedProjects != null && selectedProjects.length > 0) {
			SDK sdk = SDKManager.getInstance().getSDK(new Path(sdkLocation));

			// need to add the SDK to workspace if not already available.
			if (sdk == null) {
				sdk = SDKUtil.createSDKFromLocation(new Path(sdkLocation));
			}

			if (sdk != null && sdk.isValid() && !(SDKManager.getInstance().containsSDK(sdk))) {
				SDKManager.getInstance().addSDK(sdk);
			}
		}

		IRuntime runtime = (IRuntime) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);

		for (int i = 0; i < selectedProjects.length; i++) {
			if (selectedProjects[i] instanceof ProjectRecord) {

				try {
					IProject project =
						ProjectUtil.importProject((ProjectRecord) selectedProjects[i], runtime, sdkLocation, monitor);

					if (project != null) {
						createdProjects.add(project);
					}
				}
				catch (CoreException e) {
					return ProjectCorePlugin.createErrorStatus(e);
				}
			}
		}

		return Status.OK_STATUS;
	}

}
