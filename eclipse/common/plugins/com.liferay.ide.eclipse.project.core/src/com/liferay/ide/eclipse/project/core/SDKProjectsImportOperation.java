/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.core.facet.PluginFacetProjectCreationDataModelProvider;
import com.liferay.ide.eclipse.project.core.util.PluginFacetUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectWorkingCopy;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
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
		
		for (int i = 0; i < selectedProjects.length; i++) {
			if (selectedProjects[i] instanceof ProjectRecord) {
				IStatus status = importProject((ProjectRecord) selectedProjects[i], monitor);
				
				if (!status.isOK()) {
					return status;
				}
			}
		}

		return Status.OK_STATUS;
	}

	private IProject createExistingProject(final ProjectRecord record, IProgressMonitor monitor)
		throws CoreException {
		
		String projectName = record.getProjectName();
		
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		
		IProject project = workspace.getRoot().getProject(projectName);

		if (record.description == null) {
			// error case
			record.description = workspace.newProjectDescription(projectName);
			
			IPath locationPath = new Path(record.projectSystemFile.getAbsolutePath());

			// If it is under the root use the default location
			if (Platform.getLocation().isPrefixOf(locationPath)) {
				record.description.setLocation(null);
			}
			else {
				record.description.setLocation(locationPath);
			}
		}
		else {
			record.description.setName(projectName);
		}

		monitor.beginTask("Importing project", 100);
		
		project.create(record.description, new SubProgressMonitor(monitor, 30));
		
		project.open(IResource.FORCE, new SubProgressMonitor(monitor, 70));

		// IFile webXmlPath = project.getFile("docroot/WEB-INF/web.xml");

		IFacetedProject fProject = ProjectFacetsManager.create(project, true, monitor);
		
		FacetedProjectWorkingCopy fpwc = new FacetedProjectWorkingCopy(fProject);
		
		PluginFacetUtil.configureProjectAsPlugin(fpwc, this.model);
		
		fpwc.commitChanges(monitor);
		
		monitor.done();
		
		return project;
	}

	private IProject createNewProject(ProjectRecord projectRecord, IProgressMonitor monitor)
		throws CoreException {

		IDataModel newProjectDataModel =
			DataModelFactory.createDataModel(new PluginFacetProjectCreationDataModelProvider());
		
		// we are importing so set flag to not create anything
		newProjectDataModel.setBooleanProperty(IPluginProjectDataModelProperties.CREATE_PROJECT_OPERATION, false);
		
		String sdkLocation = getDataModel().getStringProperty(SDK_LOCATION);
		
		String sdkName = PluginFacetUtil.getSDKName(sdkLocation);
		// if the get sdk from the location
		newProjectDataModel.setProperty(IPluginProjectDataModelProperties.LIFERAY_SDK_NAME, sdkName);

		if (projectRecord.getProjectName().endsWith("-portlet")) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_PORTLET, true);
		}
		else if (projectRecord.getProjectName().endsWith("-hook")) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_HOOK, true);
		}
		else if (projectRecord.getProjectName().endsWith("-ext")) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_EXT, true);
		}

		IPath webXmlPath = projectRecord.getProjectLocation().append("docroot/WEB-INF/web.xml");
		
		ProjectUtil.setGenerateDD(newProjectDataModel, !(webXmlPath.toFile().exists()));

		IFacetedProjectWorkingCopy fpjwc =
			(IFacetedProjectWorkingCopy) newProjectDataModel.getProperty(FACETED_PROJECT_WORKING_COPY);

		fpjwc.setProjectName(projectRecord.getProjectName());
		
		fpjwc.setProjectLocation(projectRecord.getProjectLocation());
		
		PluginFacetUtil.configureProjectAsPlugin(fpjwc, this.model);

		fpjwc.commitChanges(monitor);
		
		return fpjwc.getProject();
	}

	protected IStatus importProject(ProjectRecord projectRecord, IProgressMonitor monitor) {
		IProject project = null;
		
		if (projectRecord.projectSystemFile != null) {
			try {
				project = createExistingProject(projectRecord, monitor);
			}
			catch (CoreException e) {
				return ProjectCorePlugin.createErrorStatus(e);
			}
		}
		else if (projectRecord.liferayProjectDir != null) {
			try {
				project = createNewProject(projectRecord, monitor);
			}
			catch (CoreException e) {
				return ProjectCorePlugin.createErrorStatus(e);
			}
		}
		
		if (project != null) {
			createdProjects.add(project);
		}
		
		return Status.OK_STATUS;
	}

}
