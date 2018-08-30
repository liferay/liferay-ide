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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author Gregory Amerson
 */
public class LiferayProjectImportOperation
	extends AbstractDataModelOperation implements ILiferayProjectImportDataModelProperties {

	public LiferayProjectImportOperation(IDataModel model) {
		super(model);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		ProjectRecord projectRecord = (ProjectRecord)getDataModel().getProperty(PROJECT_RECORD);

		if (projectRecord == null) {
			return ProjectCore.createErrorStatus("Project record to import is null.");
		}

		File projectDir = FileUtil.getFile(projectRecord.getProjectLocation());

		SDK sdk = SDKUtil.getSDKFromProjectDir(projectDir);

		SDKManager sdkManager = SDKManager.getInstance();

		if ((sdk != null) && !sdkManager.containsSDK(sdk)) {
			sdkManager.addSDK(sdk);
		}

		IRuntime runtime = (IRuntime)model.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);

		try {
			ProjectImportUtil.importProject(projectRecord, runtime, FileUtil.toOSString(sdk.getLocation()), monitor);
		}
		catch (CoreException ce) {
			return ProjectCore.createErrorStatus(ce);
		}

		return Status.OK_STATUS;
	}

}