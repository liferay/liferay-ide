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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public abstract class LiferayDataModelOperation
	extends AbstractDataModelOperation implements IArtifactEditOperationDataModelProperties {

	public LiferayDataModelOperation(IDataModel model, TemplateStore templateStore, TemplateContextType contextType) {
		super(model);

		this.templateStore = templateStore;

		this.contextType = contextType;
	}

	protected TemplateContextType getContextType() {
		return contextType;
	}

	protected IProject getTargetProject() {
		String projectName = model.getStringProperty(PROJECT_NAME);

		return ProjectUtil.getProject(projectName);
	}

	protected TemplateStore getTemplateStore() {
		return templateStore;
	}

	protected TemplateContextType contextType;
	protected TemplateStore templateStore;

}