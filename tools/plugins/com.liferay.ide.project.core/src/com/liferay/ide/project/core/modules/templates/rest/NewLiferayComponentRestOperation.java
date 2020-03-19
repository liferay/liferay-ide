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

package com.liferay.ide.project.core.modules.templates.rest;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.project.core.modules.BndProperties;
import com.liferay.ide.project.core.modules.BndPropertiesValue;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentRestOperation extends AbstractLiferayComponentTemplate {

	public NewLiferayComponentRestOperation() {
	}

	@Override
	protected List<Artifact> getComponentDependencies() throws CoreException {
		List<Artifact> dependencies = super.getComponentDependencies();

		dependencies.add(new Artifact("javax.ws.rs", "javax.ws.rs-api", "2.0.1", "compileOnly", null));

		return dependencies;
	}

	@Override
	protected String getExtensionClass() {
		return _EXTENSION_CLASS;
	}

	@Override
	protected List<String> getImports() {
		List<String> imports = new ArrayList<>();

		imports.add("com.liferay.portal.kernel.model.User");
		imports.add("com.liferay.portal.kernel.service.UserLocalService");
		imports.add("java.util.Collections");
		imports.add("java.util.Set");
		imports.add("javax.ws.rs.GET");
		imports.add("javax.ws.rs.Path");
		imports.add("javax.ws.rs.Produces");
		imports.add("javax.ws.rs.core.Application");
		imports.add("org.osgi.service.component.annotations.Reference");
		imports.addAll(super.getImports());

		return imports;
	}

	@Override
	protected List<String> getProperties() {
		List<String> properties = new ArrayList<>();

		Collections.addAll(properties, _PROPERTIES_LIST);

		properties.addAll(super.getProperties());

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

	@Override
	protected void setBndProperties(BndProperties bndProperty) {
		bndProperty.addValue(
			"Require-Capability",
			new BndPropertiesValue("osgi.contract; filter:=\"(&(osgi.contract=JavaJAXRS)(version=2))\""));

		bndProperty.addValue("-sources", new BndPropertiesValue("true"));
	}

	private static final String _EXTENSION_CLASS = "Application.class";

	private static final String[] _PROPERTIES_LIST = {"jaxrs.application=true"};

	private static final String _SUPER_CLASS = "Application";

	private static final String _TEMPLATE_FILE = "rest/rest.ftl";

}