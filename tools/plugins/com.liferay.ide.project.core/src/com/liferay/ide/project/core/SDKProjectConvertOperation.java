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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SDKPluginFacetUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class SDKProjectConvertOperation
	extends AbstractDataModelOperation implements ISDKProjectsImportDataModelProperties {

	public SDKProjectConvertOperation(IDataModel model) {
		super(model);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Object[] selectedProjects = (Object[])getDataModel().getProperty(SELECTED_PROJECTS);

		for (Object project : selectedProjects) {
			if (project instanceof ProjectRecord) {
				IStatus status = convertProject((ProjectRecord)project, monitor);

				if (!status.isOK()) {
					return status;
				}
			}
		}

		return Status.OK_STATUS;
	}

	public IProject convertedProject;

	protected IProject convertExistingProject(ProjectRecord record, IProgressMonitor monitor) throws CoreException {
		String projectName = record.getProjectName();

		IWorkspace workspace = CoreUtil.getWorkspace();

		IProject project = CoreUtil.getProject(projectName);

		if (record.description == null) {

			// error case

			record.description = workspace.newProjectDescription(projectName);

			IPath locationPath = new Path(record.projectSystemFile.getAbsolutePath());

			// If it is under the root use the default location

			IPath location = Platform.getLocation();

			if (location.isPrefixOf(locationPath)) {
				record.description.setLocation(null);
			}
			else {
				record.description.setLocation(locationPath);
			}
		}
		else {
			record.description.setName(projectName);
		}

		monitor.beginTask(Msgs.importingProject, 100);

		project.open(IResource.FORCE, CoreUtil.newSubmonitor(monitor, 70));

		IFacetedProject fProject = ProjectFacetsManager.create(project, true, monitor);

		FacetedProjectWorkingCopy fpwc = new FacetedProjectWorkingCopy(fProject);

		String sdkLocation = getDataModel().getStringProperty(SDK_LOCATION);

		IRuntime runtime = (IRuntime)model.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);

		String pluginType = ProjectUtil.guessPluginType(fpwc);

		SDKPluginFacetUtil.configureProjectAsRuntimeProject(fpwc, runtime, pluginType, sdkLocation, record);

		fpwc.commitChanges(monitor);

		monitor.done();

		return project;
	}

	protected IStatus convertProject(ProjectRecord projectRecord, IProgressMonitor monitor) {
		IProject project = null;

		if (projectRecord.project != null) {
			try {
				project = convertExistingProject(projectRecord, monitor);
			}
			catch (CoreException ce) {
				return ProjectCore.createErrorStatus(ce);
			}
		}

		convertedProject = project;

		return Status.OK_STATUS;
	}

	protected String getSDKName() {
		String sdkLocation = getDataModel().getStringProperty(SDK_LOCATION);

		IPath sdkLocationPath = new Path(sdkLocation);

		SDKManager sdkManager = SDKManager.getInstance();

		SDK sdk = sdkManager.getSDK(sdkLocationPath);

		String sdkName = null;

		if (sdk != null) {
			sdkName = sdk.getName();
		}
		else {
			sdk = SDKUtil.createSDKFromLocation(sdkLocationPath);

			sdkManager.addSDK(sdk);

			sdkName = sdk.getName();
		}

		return sdkName;
	}

	private static class Msgs extends NLS {

		public static String importingProject;

		static {
			initializeMessages(SDKProjectConvertOperation.class.getName(), Msgs.class);
		}

	}

}