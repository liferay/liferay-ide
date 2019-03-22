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

package com.liferay.ide.project.core.modules.templates.mvcportlet;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;
import com.liferay.ide.project.core.modules.templates.BndProperties;
import com.liferay.ide.project.core.modules.templates.BndPropertiesValue;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentMVCPortletOperation extends AbstractLiferayComponentTemplate {

	public NewLiferayComponentMVCPortletOperation() {
	}

	@Override
	protected void doMergeResourcesOperation() throws CoreException {
		try {
			IFolder resourceFolder = liferayProject.getSourceFolder("resources");

			IFolder contentFolder = resourceFolder.getFolder("content");

			IFile languageProperties = contentFolder.getFile(new Path("Language.properties"));

			File languagePropertiesFile = FileUtil.getFile(languageProperties);

			if (FileUtil.exists(languagePropertiesFile)) {
				String originContent = FileUtil.readContents(languagePropertiesFile, true);

				Class<?> clazz = getClass();

				ClassLoader classLoader = clazz.getClassLoader();

				URL sampleFileURL = classLoader.getResource(TEMPLATE_DIR + "/mvcportlet/mvc-language.properties");

				String addContent = FileUtil.readContents(FileUtil.getFile(FileLocator.toFileURL(sampleFileURL)), true);

				String totalContent = originContent + System.getProperty("line.separator") + addContent;

				FileUtil.writeFile(languagePropertiesFile, totalContent.getBytes(), projectName);
			}
			else {
				createSampleFile(languageProperties, "mvcportlet/mvc-language.properties");
			}

			IFolder metaFolder = resourceFolder.getFolder("META-INF/resources");

			IFile initJsp = metaFolder.getFile(new Path(componentClassName.toLowerCase() + "/init.jsp"));

			if (FileUtil.notExists(initJsp)) {
				createSampleFile(initJsp, "mvcportlet/mvc-init.jsp");
			}

			IFile viewJsp = metaFolder.getFile(new Path(componentClassName.toLowerCase() + "/view.jsp"));

			if (FileUtil.notExists(viewJsp)) {
				createSampleFile(
					viewJsp, "mvcportlet/mvc-view.jsp", "/init.jsp",
					"/" + componentClassName.toLowerCase() + "/init.jsp");
			}
		}
		catch (Exception e) {
			throw new CoreException(ProjectCore.createErrorStatus(e));
		}
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
		imports.add("com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet");

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

		properties.add("javax.portlet.init-param.view-template=/" + componentClassName.toLowerCase() + "/view.jsp");

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
		bndProperty.addValue("-jsp", new BndPropertiesValue("*.jsp,*.jspf"));
		bndProperty.addValue(
			"-plugin.bundle",
			new BndPropertiesValue("com.liferay.ant.bnd.resource.bundle.ResourceBundleLoaderAnalyzerPlugin"));
		bndProperty.addValue("-plugin.jsp", new BndPropertiesValue("com.liferay.ant.bnd.jsp.JspAnalyzerPlugin"));
		bndProperty.addValue("-sources", new BndPropertiesValue("true"));
	}

	private static final String _EXTENSION_CLASS = "Portlet.class";

	private static final String[] _PROPERTIES_LIST = {
		"com.liferay.portlet.display-category=category.sample", "com.liferay.portlet.instanceable=true",
		"javax.portlet.init-param.template-path=/", "javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	};

	private static final String _SUPER_CLASS = "MVCPortlet";

	private static final String _TEMPLATE_FILE = "mvcportlet/mvcportlet.ftl";

}