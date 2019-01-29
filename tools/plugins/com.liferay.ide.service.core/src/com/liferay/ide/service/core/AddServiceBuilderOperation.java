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

package com.liferay.ide.service.core;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.WizardUtil;
import com.liferay.ide.service.core.operation.INewServiceBuilderDataModelProperties;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class AddServiceBuilderOperation
	extends AbstractDataModelOperation implements INewServiceBuilderDataModelProperties {

	public AddServiceBuilderOperation(IDataModel model) {
		super(model);
	}

	public void createDefaultServiceBuilderFile(IFile serviceBuilderFile, IProgressMonitor monitor)
		throws CoreException {

		String descriptorVersion = null;

		try {
			ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, serviceBuilderFile.getProject());

			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			Version portalVersion = new Version(portal.getVersion());

			descriptorVersion = portalVersion.getMajor() + "." + portalVersion.getMinor() + ".0";
		}
		catch (Exception e) {
			ProjectCore.logError("Could not determine liferay runtime version", e);
			descriptorVersion = "6.0.0";
		}

		WizardUtil.createDefaultServiceBuilderFile(
			serviceBuilderFile, descriptorVersion, getDataModel().getBooleanProperty(USE_SAMPLE_TEMPLATE),
			getDataModel().getStringProperty(PACKAGE_PATH), getDataModel().getStringProperty(NAMESPACE),
			getDataModel().getStringProperty(AUTHOR), monitor);

		getDataModel().setProperty(CREATED_SERVICE_FILE, serviceBuilderFile);
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IStatus retval = null;

		IStatus status = _createServiceBuilderFile(getTargetProject(), monitor);

		if (!status.isOK()) {
			return status;
		}

		return retval;
	}

	@SuppressWarnings("restriction")
	protected IProject getTargetProject() {
		String projectName = model.getStringProperty(PROJECT_NAME);

		return ProjectUtil.getProject(projectName);
	}

	private IStatus _createServiceBuilderFile(IProject project, IProgressMonitor monitor) {

		// IDE-110 IDE-648

		IWebProject webproject = LiferayCore.create(IWebProject.class, project);

		if ((webproject == null) || (webproject.getDefaultDocrootFolder() == null)) {
			return ServiceCore.createErrorStatus("Could not find webapp root folder.");
		}

		IFolder defaultDocroot = webproject.getDefaultDocrootFolder();
		Path path = new Path("WEB-INF/" + getDataModel().getStringProperty(SERVICE_FILE));

		IFile serviceBuilderFile = defaultDocroot.getFile(path);

		if (FileUtil.notExists(serviceBuilderFile)) {
			try {
				createDefaultServiceBuilderFile(serviceBuilderFile, monitor);
			}
			catch (Exception ex) {
				return ServiceCore.createErrorStatus(ex);
			}
		}

		return Status.OK_STATUS;
	}

}