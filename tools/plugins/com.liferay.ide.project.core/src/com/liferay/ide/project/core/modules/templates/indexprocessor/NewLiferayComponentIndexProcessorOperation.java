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

package com.liferay.ide.project.core.modules.templates.indexprocessor;

import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentIndexProcessorOperation extends AbstractLiferayComponentTemplate {

	public NewLiferayComponentIndexProcessorOperation() {
	}

	@Override
	protected String getExtensionClass() {
		return _EXTENSION_CLASS;
	}

	@Override
	protected List<String> getImports() {
		List<String> imports = new ArrayList<>();

		imports.add("com.liferay.portal.kernel.search.BooleanQuery");
		imports.add("com.liferay.portal.kernel.search.Document");
		imports.add("com.liferay.portal.kernel.search.IndexerPostProcessor");
		imports.add("com.liferay.portal.kernel.search.SearchContext");
		imports.add("com.liferay.portal.kernel.search.Summary");
		imports.add("com.liferay.portal.kernel.search.filter.BooleanFilter");
		imports.add("com.liferay.portal.kernel.log.LogFactoryUtil");
		imports.add("com.liferay.portal.kernel.log.Log");
		imports.add("java.util.Locale");
		imports.addAll(super.getImports());

		return imports;
	}

	@Override
	protected List<String> getProperties() {
		List<String> properties = new ArrayList<>();

		Collections.addAll(properties, _PROPERTIES_LIST);

		for (String property : super.getProperties()) {
			properties.add(property);
		}

		return properties;
	}

	@Override
	protected String getSuperClass() {
		return _SUPER_CLASS;
	}

	@Override
	protected String getTemplateFile() {
		return _TEMPLATE_FILE;
	}

	private static final String _EXTENSION_CLASS = "IndexerPostProcessor.class";

	private static final String[] _PROPERTIES_LIST = {"indexer.class.name=com.liferay.portal.model.User"};

	private static final String _SUPER_CLASS = "IndexerPostProcessor";

	private static final String _TEMPLATE_FILE = "indexprocessor/indexprocessor.ftl";

}