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

package com.liferay.ide.eclipse.portlet.vaadin.core.operation;

import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.portlet.core.operation.AddPortletOperation;
import com.liferay.ide.eclipse.portlet.vaadin.core.dd.VaadinPortletDescriptorHelper;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.j2ee.internal.common.operations.NewJavaEEArtifactClassOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Henri Sara - borrows from AddPortletOperation by Greg Amerson
 */
@SuppressWarnings("restriction")
public class AddVaadinApplicationOperation extends AddPortletOperation
	implements INewVaadinPortletClassDataModelProperties {

	public AddVaadinApplicationOperation(IDataModel dataModel) {
		super(dataModel);
	}

	@Override
	protected IStatus generateMetaData(IDataModel aModel) {
		if (ProjectUtil.isPortletProject(getTargetProject())) {
			VaadinPortletDescriptorHelper portletDescHelper = new VaadinPortletDescriptorHelper(getTargetProject());

			if (aModel.getBooleanProperty(REMOVE_EXISTING_ARTIFACTS)) {
				portletDescHelper.removeAllPortlets();
			}

			// also adds a dependency to vaadin.jar in
			// liferay-plugin-package.properties
			IStatus status = portletDescHelper.addNewVaadinPortlet(model);

			if (!status.isOK()) {
				PortletCore.getDefault().getLog().log(status);
				return status;
			}
		}

		return OK_STATUS;
	}

	protected NewJavaEEArtifactClassOperation getNewClassOperation() {
		return new NewVaadinApplicationClassOperation(getDataModel());
	}

}
