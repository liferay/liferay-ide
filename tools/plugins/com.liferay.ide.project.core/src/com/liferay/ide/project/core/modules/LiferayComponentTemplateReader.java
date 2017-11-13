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

import com.liferay.ide.core.ExtensionReader;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("rawtypes")
public class LiferayComponentTemplateReader extends ExtensionReader<IComponentTemplate> {

	public LiferayComponentTemplateReader() {
		super(ProjectCore.PLUGIN_ID, _EXTENSION, _COMPONENT_TEMPLATE);
	}

	public IComponentTemplate[] getComponentTemplates() {
		return getExtensions().toArray(new IComponentTemplate[0]);
	}

	@Override
	protected IComponentTemplate initElement(
		IConfigurationElement configElement, IComponentTemplate componentTemplate) {

		AbstractLiferayComponentTemplate template = (AbstractLiferayComponentTemplate)componentTemplate;

		template.setDisplayName(configElement.getAttribute(_COMPONENT_TEMPLATE_DISPLAY_NAME_ELEMENT));
		template.setShortName(configElement.getAttribute(_COMPONENT_TEMPLATE_SHORT_NAME_ELEMENT));

		return template;
	}

	private static final String _COMPONENT_TEMPLATE = "liferayComponentTemplate";

	private static final String _COMPONENT_TEMPLATE_DISPLAY_NAME_ELEMENT = "displayName";

	private static final String _COMPONENT_TEMPLATE_SHORT_NAME_ELEMENT = "shortName";

	private static final String _EXTENSION = "liferayComponentTemplates";

}