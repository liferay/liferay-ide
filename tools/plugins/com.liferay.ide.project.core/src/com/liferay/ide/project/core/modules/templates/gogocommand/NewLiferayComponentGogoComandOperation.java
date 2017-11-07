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

package com.liferay.ide.project.core.modules.templates.gogocommand;

import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentGogoComandOperation extends AbstractLiferayComponentTemplate {

	public NewLiferayComponentGogoComandOperation() {
	}

	@Override
	protected String getExtensionClass() {
		return _EXTENSION_CLASS;
	}

	@Override
	protected List<String> getImports() {
		List<String> imports = new ArrayList<>();

		imports.add("org.osgi.service.component.annotations.Reference");
		imports.add("com.liferay.portal.kernel.service.UserLocalService");
		imports.addAll(super.getImports());

		return imports;
	}

	@Override
	protected List<String> getProperties() {
		List<String> mvcProperties = new ArrayList<>();

		Collections.addAll(mvcProperties, _PROPERTIES_LIST);

		return mvcProperties;
	}

	@Override
	protected String getTemplateFile() {
		return _TEMPLATE_FILE;
	}

	private static final String _EXTENSION_CLASS = "Object.class";

	private static final String[] _PROPERTIES_LIST = {"osgi.command.scope=blade", "osgi.command.function=usercount"};

	private static final String _TEMPLATE_FILE = "gogocommand/gogocommand.ftl";

}