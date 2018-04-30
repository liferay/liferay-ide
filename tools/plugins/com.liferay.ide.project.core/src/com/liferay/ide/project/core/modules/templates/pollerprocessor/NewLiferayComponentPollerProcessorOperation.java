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

package com.liferay.ide.project.core.modules.templates.pollerprocessor;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
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

import java.net.URL;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentPollerProcessorOperation extends AbstractLiferayComponentTemplate {

	public NewLiferayComponentPollerProcessorOperation() {
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

					IFile pollerClassFile = prepareClassFile(componentClassName);

					_sourceCodeOperation(pollerClassFile, "poller");

					IFile pollerPortletClassFile = prepareClassFile(componentClassName + "Portlet");

					_sourceCodeOperation(pollerPortletClassFile, "pollerPortlet");

					doMergeResourcesOperation();

					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				}
			}
		}
		catch (Exception e) {
			throw new CoreException(ProjectCore.createErrorStatus(e));
		}
	}

	@Override
	protected void doMergeResourcesOperation() throws CoreException {
		try {
			IFolder resourceFolder = liferayProject.getSourceFolder("resources");

			IFolder contentFolder = resourceFolder.getFolder("content");

			IFile languageProperties = contentFolder.getFile(new Path("Language.properties"));

			File languagePropertiesFile = languageProperties.getLocation().toFile();

			if (FileUtil.exists(languagePropertiesFile)) {
				String originContent = FileUtil.readContents(languagePropertiesFile, true);

				Class<?> clazz = getClass();

				URL sampleFileURL = clazz.getClassLoader().getResource(
					TEMPLATE_DIR + "/pollerprocessor/poller-language.properties");

				String addContent = FileUtil.readContents(
					new File(FileLocator.toFileURL(sampleFileURL).getFile()), true);

				String totalContent = originContent + System.getProperty("line.separator") + addContent;

				FileUtil.writeFile(languagePropertiesFile, totalContent.getBytes(), projectName);
			}
			else {
				createSampleFile(languageProperties, "pollerprocessor/poller-language.properties");
			}

			IFolder metaFolder = resourceFolder.getFolder("META-INF/resources");

			IFile mainJs = metaFolder.getFile(new Path(componentClassName.toLowerCase() + "/js/main.js"));

			if (FileUtil.notExists(mainJs)) {
				createSampleFile(mainJs, "pollerprocessor/poller-main.js");
			}

			IFile initJsp = metaFolder.getFile(new Path(componentClassName.toLowerCase() + "/init.jsp"));

			if (FileUtil.notExists(initJsp)) {
				createSampleFile(initJsp, "pollerprocessor/poller-init.jsp");
			}

			IFile viewJsp = metaFolder.getFile(new Path(componentClassName.toLowerCase() + "/view.jsp"));

			if (FileUtil.notExists(viewJsp)) {
				createSampleFile(
					viewJsp, "pollerprocessor/poller-view.jsp", "/init.jsp",
					"/" + componentClassName.toLowerCase() + "/init.jsp");
			}
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
		return _POLLER_TEMPLATE_FILE;
	}

	private String _getPollerExtensionClass() {
		return _POLLER_EXTENSION_CLASSES;
	}

	private List<String> _getPollerImports() {
		List<String> imports = new ArrayList<>();

		imports.add("com.liferay.portal.kernel.json.JSONFactoryUtil");
		imports.add("com.liferay.portal.kernel.json.JSONObject");
		imports.add("com.liferay.portal.kernel.log.Log");
		imports.add("com.liferay.portal.kernel.log.LogFactoryUtil");
		imports.add("com.liferay.portal.kernel.poller.BasePollerProcessor");
		imports.add("com.liferay.portal.kernel.poller.DefaultPollerResponse");
		imports.add("com.liferay.portal.kernel.poller.PollerProcessor");
		imports.add("com.liferay.portal.kernel.poller.PollerRequest");
		imports.add("com.liferay.portal.kernel.poller.PollerResponse");
		imports.add("java.util.Date");

		imports.addAll(super.getImports());

		return imports;
	}

	private String _getPollerPortletExtensionClass() {
		return _POLLER_PORTLET_EXTENSION_CLASSES;
	}

	private List<String> _getPollerPortletImports() {
		List<String> imports = new ArrayList<>();

		imports.add("javax.portlet.Portlet");
		imports.add("com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet");
		imports.addAll(super.getImports());

		return imports;
	}

	private List<String> _getPollerPortletProperties() {
		List<String> properties = new ArrayList<>();

		Collections.addAll(properties, _POLLER_PORTLET_PROPERTIES_LIST);

		for (String property : super.getProperties()) {
			properties.add(property);
		}

		properties.add("javax.portlet.init-param.template-path=/");
		properties.add("com.liferay.portlet.poller-processor-class=" + packageName + "." + componentClassName);
		properties.add("javax.portlet.display-name=" + componentClassName);
		properties.add("javax.portlet.portlet.info.short-title=" + componentClassName);
		properties.add("javax.portlet.portlet.info.title=" + componentClassName);
		properties.add(
			"com.liferay.portlet.header-portlet-javascript=/" + componentClassName.toLowerCase() + "/js/main.js");
		properties.add("javax.portlet.init-param.view-template=/" + componentClassName.toLowerCase() + "/view.jsp");
		properties.add("javax.portlet.resource-bundle=content.Language");

		return properties;
	}

	private String _getPollerPortletSuperClass() {
		return _POLLER_PORTLET_SUPER_CLASSES;
	}

	private List<String> _getPollerProperties() {
		List<String> properties = new ArrayList<>();

		for (String property : super.getProperties()) {
			properties.add(property);
		}

		properties.add("javax.portlet.name=" + componentClassName + "Portlet");

		return properties;
	}

	private String _getPollerSuperClass() {
		return _POLLER_SUPER_CLASSES;
	}

	private Map<String, Object> _getTemplateMap(String type) {
		Map<String, Object> root = new HashMap<>();

		if (type.equals("poller")) {
			root.put("classname", componentClassName);
			root.put("extensionclass", _getPollerExtensionClass());
			root.put("importlibs", _getPollerImports());
			root.put("properties", _getPollerProperties());
			root.put("supperclass", _getPollerSuperClass());
		}
		else {
			root.put("classname", componentClassName + "Portlet");
			root.put("extensionclass", _getPollerPortletExtensionClass());
			root.put("importlibs", _getPollerPortletImports());
			root.put("properties", _getPollerPortletProperties());
			root.put("supperclass", _getPollerPortletSuperClass());
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

	private static final String _POLLER_EXTENSION_CLASSES = "PollerProcessor.class";

	private static final String _POLLER_PORTLET_EXTENSION_CLASSES = "Portlet.class";

	private static final String[] _POLLER_PORTLET_PROPERTIES_LIST = {
		"com.liferay.portlet.css-class-wrapper=portlet-pollprocessor-blade",
		"com.liferay.portlet.display-category=category.sample", "com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false", "com.liferay.portlet.remoteable=true",
		"com.liferay.portlet.render-weight=50", "javax.portlet.expiration-cache=0",
		"javax.portlet.portlet.info.keywords=pollprocessor", "javax.portlet.security-role-ref=power-user,user"
	};

	private static final String _POLLER_PORTLET_SUPER_CLASSES = "MVCPortlet";

	private static final String _POLLER_SUPER_CLASSES = "BasePollerProcessor";

	private static final String _POLLER_TEMPLATE_FILE = "pollerprocessor/pollerprocessor.ftl";

}