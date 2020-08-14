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

package com.liferay.ide.project.core.descriptor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Kuo Zhang
 */
public abstract class AddNewPortletOperation implements IDescriptorOperation {

	public abstract IStatus addNewPortlet(IDataModel model);

	@Override
	public final IStatus execute(Object... params) {
		if ((params != null) && (params.length == 1) && (params[0] instanceof IDataModel)) {
			return addNewPortlet((IDataModel)params[0]);
		}

		return Status.OK_STATUS;
	}

}