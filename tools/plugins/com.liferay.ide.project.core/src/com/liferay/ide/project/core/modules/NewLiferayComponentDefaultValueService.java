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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.SapphireUtil;

import org.apache.commons.lang.WordUtils;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentDefaultValueService extends DefaultValueService {

	@Override
	public void dispose() {
		NewLiferayComponentOp op = _op();

		if (op != null) {
			SapphireUtil.detachListener(
				op.property(NewLiferayComponentOp.PROP_COMPONENT_CLASS_TEMPLATE_NAME), _listener);

			SapphireUtil.detachListener(op.property(NewLiferayComponentOp.PROP_PROJECT_NAME), _listener);
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		NewLiferayComponentOp op = _op();

		String projectName = SapphireUtil.getContent(op.getProjectName());

		if (projectName == null) {
			return "";
		}

		IComponentTemplate<NewLiferayComponentOp> componentTemplate = SapphireUtil.getContent(
			op.getComponentClassTemplateName());

		if (componentTemplate != null) {
			String projectTemplate = componentTemplate.getShortName();

			char[] tokens = {'-', '.', '_'};

			String finalProjectName = WordUtils.capitalizeFully(projectName, tokens);

			for (char token : tokens) {
				finalProjectName = finalProjectName.replaceAll("\\" + token, "");
			}

			StringBuffer componentNameBuffer = new StringBuffer(finalProjectName);

			componentNameBuffer.append(projectTemplate);

			return componentNameBuffer.toString();
		}

		return "";
	}

	@Override
	protected void initDefaultValueService() {
		super.initDefaultValueService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayComponentOp op = _op();

		SapphireUtil.attachListener(op.property(NewLiferayComponentOp.PROP_PROJECT_NAME), _listener);
		SapphireUtil.attachListener(op.property(NewLiferayComponentOp.PROP_COMPONENT_CLASS_TEMPLATE_NAME), _listener);
	}

	private NewLiferayComponentOp _op() {
		return context(NewLiferayComponentOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}