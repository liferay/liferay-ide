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

import com.liferay.ide.project.core.util.ProjectImportUtil;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author Gregory Amerson
 */
public class SDKProjectsImportOperation
	extends AbstractDataModelOperation implements ISDKProjectsImportDataModelProperties {

	public SDKProjectsImportOperation(IDataModel model) {
		super(model);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		String sdkLocation = model.getStringProperty(ISDKProjectsImportDataModelProperties.SDK_LOCATION);

		IRuntime runtime = (IRuntime)model.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);

		Object[] projects = (Object[])model.getProperty(ISDKProjectsImportDataModelProperties.SELECTED_PROJECTS);

		WorkspaceJob workspaceJob = new WorkspaceJob(Msgs.creatingSDKProjects) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) {
				try {
					ProjectImportUtil.createWorkspaceProjects(projects, runtime, sdkLocation, monitor);
				}
				catch (Exception ex) {
					return ProjectCore.createErrorStatus(ex);
				}

				return Status.OK_STATUS;
			}

		};

		workspaceJob.setUser(true);

		workspaceJob.schedule();

		return Status.OK_STATUS;
	}

	private static class Msgs extends NLS {

		public static String creatingSDKProjects;

		static {
			initializeMessages(SDKProjectsImportOperation.class.getName(), Msgs.class);
		}

	}

}