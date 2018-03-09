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

package com.liferay.ide.project.core.modules.templates.portletinactioncommand;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;
import com.liferay.ide.project.core.modules.templates.BndProperties;
import com.liferay.ide.project.core.modules.templates.BndPropertiesValue;

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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentPortletActionCommandOperation extends AbstractLiferayComponentTemplate {

	public NewLiferayComponentPortletActionCommandOperation() {
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

					IFile pollerPortletClassFile = prepareClassFile(componentNameWithoutTemplateName + "ActionCommand");

					_sourceCodeOperation(pollerPortletClassFile, "actionCommand");

					op.setComponentClassName(componentNameWithoutTemplateName + "ActionCommand");

					doMergeResourcesOperation();

					doMergeBndOperation();

					doMergeDependencyOperation();

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

			IFolder metaFolder = resourceFolder.getFolder("META-INF/resources");

			IFile initFtl = metaFolder.getFile(new Path(componentClassName.toLowerCase().toLowerCase() + "/init.ftl"));

			if (FileUtil.notExists(initFtl)) {
				createSampleFile(initFtl, "portletinactioncommand/portletactioncommand-init.ftl");
			}

			IFile viewFtl = metaFolder.getFile(new Path(componentClassName.toLowerCase().toLowerCase() + "/view.ftl"));

			if (FileUtil.notExists(viewFtl)) {
				createSampleFile(viewFtl, "portletinactioncommand/portletactionconmmand-view.ftl");
			}
		}
		catch (Exception e) {
			throw new CoreException(ProjectCore.createErrorStatus(e));
		}
	}

	@Override
	protected List<String[]> getComponentDependency() throws CoreException {
		List<String[]> componentDependency = super.getComponentDependency();

		componentDependency.add(new String[] {"com.liferay.portal", "com.liferay.util.bridges", "2.0.0"});
		componentDependency.add(new String[] {"com.liferay.portal", "com.liferay.util.taglib", "2.0.0"});
		componentDependency.add(new String[] {"javax.portlet", "portlet-api", "2.0"});
		componentDependency.add(new String[] {"javax.servlet", "javax.servlet-api", "3.0.1"});

		return componentDependency;
	}

	@Override
	protected String getTemplateFile() {
		return _PORTLET_ACTION_COMMAND_TEMPLATE_FILE;
	}

	@Override
	protected void setBndProperties(BndProperties bndProperty) {
		StringBuilder formatedValueSb = new StringBuilder();

		formatedValueSb.append("\\");
		formatedValueSb.append(System.getProperty("line.separator"));
		formatedValueSb.append("\t");
		formatedValueSb.append(
			"@com.liferay.util.bridges-2.0.0.jar!/com/liferay/util/bridges/freemarker/FreeMarkerPortlet.class");
		formatedValueSb.append(",");
		formatedValueSb.append("\\");
		formatedValueSb.append(System.getProperty("line.separator"));
		formatedValueSb.append("\t");
		formatedValueSb.append("@com.liferay.util.taglib-2.0.0.jar!/META-INF/*.tld");

		StringBuilder originalValueSb = new StringBuilder();

		originalValueSb.append(
			"@com.liferay.util.bridges-2.0.0.jar!/com/liferay/util/bridges/freemarker/FreeMarkerPortlet.class");
		originalValueSb.append(",");
		originalValueSb.append("@com.liferay.util.taglib-2.0.0.jar!/META-INF/*.tld");

		bndProperty.addValue(
			"-includeresource", new BndPropertiesValue(formatedValueSb.toString(), originalValueSb.toString()));

		bndProperty.addValue("-sources", new BndPropertiesValue("true"));
	}

	private String _getPortletActionCommandExtensionClass() {
		return _PORTLET_ACTION_COMMAND_EXTENSION_CLASSES;
	}

	private List<String> _getPortletActionCommandImports() {
		List<String> imports = new ArrayList<>();

		imports.add("com.liferay.portal.kernel.log.Log");
		imports.add("com.liferay.portal.kernel.log.LogFactoryUtil");
		imports.add("com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand");
		imports.add("com.liferay.portal.kernel.servlet.SessionMessages");
		imports.add("com.liferay.portal.kernel.util.ParamUtil");
		imports.add("com.liferay.portal.kernel.util.StringPool");
		imports.add("javax.portlet.ActionRequest");
		imports.add("javax.portlet.ActionResponse");
		imports.add("javax.portlet.PortletException");
		imports.addAll(super.getImports());

		return imports;
	}

	private List<String> _getPortletActionCommandProperties() {
		List<String> properties = new ArrayList<>();
		String properPackageName = packageName.toString().replace(".", "_");

		properties.add("javax.portlet.name=" + properPackageName + "_" + componentNameWithoutTemplateName + "Portlet");

		properties.add("mvc.command.name=greet");

		return properties;
	}

	private String _getPortletActionCommandSuperClass() {
		return _PORTLET_ACTION_COMMAND_SUPER_CLASSES;
	}

	private String _getPortletExtensionClass() {
		return _PORTLET_EXTENSION_CLASSES;
	}

	private List<String> _getPortletImports() {
		List<String> imports = new ArrayList<>();

		imports.addAll(super.getImports());

		imports.add("javax.portlet.Portlet");
		imports.add("com.liferay.util.bridges.freemarker.FreeMarkerPortlet");

		return imports;
	}

	private List<String> _getPortletProperties() {
		List<String> properties = new ArrayList<>();

		Collections.addAll(properties, _PORTLET_PROPERTIES_LIST);

		for (String property : super.getProperties()) {
			properties.add(property);
		}

		properties.add("javax.portlet.display-name=" + componentNameWithoutTemplateName + " Portlet");
		properties.add("javax.portlet.init-param.view-template=/" + componentClassName.toLowerCase() + "/view.ftl");
		properties.add(
			"com.liferay.portlet.css-class-wrapper=portlet-" + componentNameWithoutTemplateName.toLowerCase());

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
			root.put("classname", componentNameWithoutTemplateName + "ActionCommand");
			root.put("extensionclass", _getPortletActionCommandExtensionClass());
			root.put("importlibs", _getPortletActionCommandImports());
			root.put("properties", _getPortletActionCommandProperties());
			root.put("supperclass", _getPortletActionCommandSuperClass());
		}

		root.put("componenttype", templateName);
		root.put("packagename", packageName);
		root.put("projectname", projectName);

		return root;
	}

	private void _sourceCodeOperation(IFile srcFile, String type) throws CoreException {
		File file = srcFile.getLocation().toFile();

		try (OutputStream fos = Files.newOutputStream(file.toPath())) {
			Template temp = cfg.getTemplate(getTemplateFile());

			Map<String, Object> root = _getTemplateMap(type);

			Writer out = new OutputStreamWriter(fos);

			temp.process(root, out);

			fos.flush();
		}
		catch (IOException | TemplateException e) {
			throw new CoreException(ProjectCore.createErrorStatus(e));
		}
	}

	private static final String _PORTLET_ACTION_COMMAND_EXTENSION_CLASSES = "MVCActionCommand.class";

	private static final String _PORTLET_ACTION_COMMAND_SUPER_CLASSES = "MVCActionCommand";

	private static final String _PORTLET_ACTION_COMMAND_TEMPLATE_FILE =
		"portletinactioncommand/portletactioncommand.ftl";

	private static final String _PORTLET_EXTENSION_CLASSES = "Portlet.class";

	private static final String[] _PORTLET_PROPERTIES_LIST = {
		"com.liferay.portlet.display-category=category.sample", "com.liferay.portlet.instanceable=true",
		"javax.portlet.init-param.template-path=/", "javax.portlet.security-role-ref=power-user,user"
	};

	private static final String _PORTLET_SUPER_CLASSES = "FreeMarkerPortlet";

}