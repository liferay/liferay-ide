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

package com.liferay.ide.project.core.modules.templates.portlet;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentPortletOperation extends AbstractLiferayComponentTemplate {

	public NewLiferayComponentPortletOperation() {
	}

	@Override
	protected List<Artifact> getComponentDependencies() throws CoreException {
		List<Artifact> dependencies = super.getComponentDependencies();

		dependencies.add(new Artifact("javax.portlet", "portlet-api", "2.0", "compileOnly", null));

		return dependencies;
	}

	@Override
	protected String getExtensionClass() {
		return _EXTENSION_CLASS;
	}

	@Override
	protected List<String> getImports() {
		List<String> imports = new ArrayList<>();

		imports.add("javax.portlet.Portlet");
		imports.add("java.io.IOException");
		imports.add("java.io.PrintWriter");
		imports.add("javax.portlet.GenericPortlet");
		imports.add("javax.portlet.PortletException");
		imports.add("javax.portlet.RenderRequest");
		imports.add("javax.portlet.RenderResponse");

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

		properties.add("javax.portlet.display-name=" + componentNameWithoutTemplateName + " Portlet");

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

	private static final String _EXTENSION_CLASS = "Portlet.class";

	private static final String[] _PROPERTIES_LIST = {
		"com.liferay.portlet.display-category=category.sample", "com.liferay.portlet.instanceable=true",
		"javax.portlet.security-role-ref=power-user,user"
	};

	private static final String _SUPER_CLASS = "GenericPortlet";

	private static final String _TEMPLATE_FILE = "portlet/portlet.ftl";

}