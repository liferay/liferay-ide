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
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings("restriction")
public class BinaryProjectsImportOperation
	extends AbstractDataModelOperation implements ISDKProjectsImportDataModelProperties {

	public BinaryProjectsImportOperation(IDataModel model) {
		super(model);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		String sdkLocation = model.getStringProperty(ISDKProjectsImportDataModelProperties.SDK_LOCATION);

		IRuntime runtime = (IRuntime)model.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);

		Object[] projects = (Object[])model.getProperty(ISDKProjectsImportDataModelProperties.SELECTED_PROJECTS);

		BridgedRuntime bridgedRuntime = (BridgedRuntime)model.getProperty(
			IFacetProjectCreationDataModelProperties.FACET_RUNTIME);

		WorkspaceJob job = new WorkspaceJob(Msgs.importingBinaryProjectPlugins) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				if (projects == null) {
					return Status.OK_STATUS;
				}

				SDKManager sdkManager = SDKManager.getInstance();

				SDK liferaySDK = sdkManager.getSDK(new Path(sdkLocation));

				Object[] seleBinaryRecords = (Object[])projects;

				monitor.beginTask(Msgs.creatingSDKProjects, seleBinaryRecords.length);

				ProjectRecord[] projectRecords = new ProjectRecord[seleBinaryRecords.length];

				for (int i = 0; i < seleBinaryRecords.length; i++) {
					BinaryProjectRecord pluginBinaryRecord = (BinaryProjectRecord)seleBinaryRecords[i];

					try {
						monitor.subTask(Msgs.creatingPlugin + pluginBinaryRecord.getLiferayPluginName());

						projectRecords[i] = ProjectImportUtil.createSDKPluginProject(
							bridgedRuntime, pluginBinaryRecord, liferaySDK);

						monitor.worked(1);
					}
					catch (IOException ioe) {
						throw new CoreException(ProjectCore.createErrorStatus("Error creating project.", ioe));
					}
				}

				monitor.done();

				ProjectImportUtil.createWorkspaceProjects(projectRecords, runtime, sdkLocation, monitor);

				return Status.OK_STATUS;
			}

		};

		job.setUser(true);

		job.schedule();

		return Status.OK_STATUS;
	}

	private static class Msgs extends NLS {

		public static String creatingPlugin;
		public static String creatingSDKProjects;
		public static String importingBinaryProjectPlugins;

		static {
			initializeMessages(BinaryProjectsImportOperation.class.getName(), Msgs.class);
		}

	}

}