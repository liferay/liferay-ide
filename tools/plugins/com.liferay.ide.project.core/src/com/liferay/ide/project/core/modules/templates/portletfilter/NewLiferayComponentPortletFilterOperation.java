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

package com.liferay.ide.project.core.modules.templates.portletfilter;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentPortletFilterOperation extends AbstractLiferayComponentTemplate {

	public NewLiferayComponentPortletFilterOperation() {
	}

	@Override
	public void doExecute(NewLiferayComponentOp op, IProgressMonitor monitor) throws CoreException {
		try {
			initializeOperation(op);

			project = CoreUtil.getProject(projectName);

			if (project != null) {
				liferayProject = LiferayCore.create(project);

				if (liferayProject != null) {
					initFreeMarker();

					IFile pollerClassFile = prepareClassFile(componentNameWithoutTemplateName + "Portlet");

					_sourceCodeOperation(pollerClassFile, "portlet");

					IFile pollerPortletClassFile = prepareClassFile(componentNameWithoutTemplateName + "RenderFilter");

					_sourceCodeOperation(pollerPortletClassFile, "renderFilter");

					op.setComponentClassName(componentNameWithoutTemplateName + "RenderFilter");

					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				}
			}

			doMergeDependencyOperation();
		}
		catch (Exception e) {
			throw new CoreException(ProjectCore.createErrorStatus(e));
		}
	}

	@Override
	protected List<String[]> getComponentDependency() throws CoreException {
		List<String[]> componentDependency = super.getComponentDependency();

		componentDependency.add(new String[] {"javax.portlet", "portlet-api", "2.0"});

		return componentDependency;
	}

	@Override
	protected String getTemplateFile() {
		return _PORTLET_FILTER_TEMPLATE_FILE;
	}

	private String _getPortletExtensionClass() {
		return _PORTLET_EXTENSION_CLASSES;
	}

	private String _getPortletFilterExtensionClass() {
		return _PORTLET_FILTER_EXTENSION_CLASSES;
	}

	private List<String> _getPortletFilterImports() {
		List<String> imports = new ArrayList<>();

		imports.add("java.io.IOException");
		imports.add("javax.portlet.filter.FilterChain");
		imports.add("javax.portlet.filter.FilterConfig");
		imports.add("javax.portlet.filter.PortletFilter");
		imports.add("javax.portlet.filter.RenderFilter");
		imports.add("javax.portlet.RenderRequest");
		imports.add("javax.portlet.RenderResponse");
		imports.add("javax.portlet.PortletException");

		imports.addAll(super.getImports());

		return imports;
	}

	private List<String> _getPortletFilterProperties() {
		List<String> properties = new ArrayList<>();

		for (String property : super.getProperties()) {
			properties.add(property);
		}

		properties.add("javax.portlet.name=blade_portlet_filter_" + componentNameWithoutTemplateName + "Portlet");

		return properties;
	}

	private String _getPortletFilterSuperClass() {
		return _PORTLET_FILTER_SUPER_CLASSES;
	}

	private List<String> _getPortletImports() {
		List<String> imports = new ArrayList<>();

		imports.add("java.io.IOException");
		imports.add("java.io.PrintWriter");
		imports.add("javax.portlet.GenericPortlet");
		imports.add("javax.portlet.Portlet");
		imports.add("javax.portlet.PortletException");
		imports.add("javax.portlet.RenderRequest");
		imports.add("javax.portlet.RenderResponse");

		imports.addAll(super.getImports());

		return imports;
	}

	private List<String> _getPortletProperties() {
		List<String> properties = new ArrayList<>();

		Collections.addAll(properties, _PORTLET_PROPERTIES_LIST);

		for (String property : super.getProperties()) {
			properties.add(property);
		}

		properties.add("javax.portlet.display-name=" + componentNameWithoutTemplateName + " Filter Portlet");
		properties.add("javax.portlet.name=blade_portlet_filter_" + componentNameWithoutTemplateName + "Portlet");

		return properties;
	}

	private String _getPortletSuperClass() {
		return _PORTLET_SUPER_CLASSES;
	}

	private Map<String, Object> _getTemplateMap(String type) {
		Map<String, Object> root = new HashMap<>();

		if (type.equals("portlet")) {
			root.put("classname", componentNameWithoutTemplateName + "Portlet");
			root.put("extensionclass", _getPortletExtensionClass());
			root.put("importlibs", _getPortletImports());
			root.put("properties", _getPortletProperties());
			root.put("supperclass", _getPortletSuperClass());
		}
		else {
			root.put("classname", componentNameWithoutTemplateName + "RenderFilter");
			root.put("extensionclass", _getPortletFilterExtensionClass());
			root.put("importlibs", _getPortletFilterImports());
			root.put("properties", _getPortletFilterProperties());
			root.put("supperclass", _getPortletFilterSuperClass());
		}

		root.put("componenttype", templateName);
		root.put("packagename", packageName);
		root.put("projectname", projectName);

		return root;
	}

	private void _sourceCodeOperation(IFile srcFile, String type) throws CoreException {
		File file = srcFile.getLocation().toFile();

		try (OutputStream fos = Files.newOutputStream(file.toPath());
				Writer out = new OutputStreamWriter(fos)) {

			Template temp = cfg.getTemplate(getTemplateFile());

			Map<String, Object> root = _getTemplateMap(type);

			temp.process(root, out);

			fos.flush();
		}
		catch (IOException | TemplateException e) {
			throw new CoreException(ProjectCore.createErrorStatus(e));
		}
	}

	private static final String _PORTLET_EXTENSION_CLASSES = "Portlet.class";

	private static final String _PORTLET_FILTER_EXTENSION_CLASSES = "PortletFilter.class";

	private static final String _PORTLET_FILTER_SUPER_CLASSES = "RenderFilter";

	private static final String _PORTLET_FILTER_TEMPLATE_FILE = "portletfilter/portletfilter.ftl";

	private static final String[] _PORTLET_PROPERTIES_LIST = {
		"com.liferay.portlet.display-category=category.sample", "com.liferay.portlet.instanceable=true",
		"javax.portlet.security-role-ref=power-user,user"
	};

	private static final String _PORTLET_SUPER_CLASSES = "GenericPortlet";

}