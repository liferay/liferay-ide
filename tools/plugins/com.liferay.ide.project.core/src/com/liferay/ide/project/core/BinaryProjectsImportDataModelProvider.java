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

import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class BinaryProjectsImportDataModelProvider extends SDKProjectsImportDataModelProvider {

	@Override
	public IStatus createSelectedProjectsErrorStatus() {
		return ProjectCore.createErrorStatus(Msgs.selectOneBinary);
	}

	@Override
	public IDataModelOperation getDefaultOperation() {
		return new BinaryProjectsImportOperation(model);
	}

	@Override
	public void init() {
		super.init();

		ProjectUtil.setDefaultRuntime(getDataModel());
	}

	private static class Msgs extends NLS {

		public static String selectOneBinary;

		static {
			initializeMessages(BinaryProjectsImportDataModelProvider.class.getName(), Msgs.class);
		}

	}

}