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

package com.liferay.ide.project.core.modules.templates.authenticator;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BndProperties;
import com.liferay.ide.project.core.modules.BndPropertiesValue;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentAuthenticatorOperation extends AbstractLiferayComponentTemplate {

	public NewLiferayComponentAuthenticatorOperation() {
	}

	@Override
	protected void doMergeResourcesOperation() throws CoreException {
		try {
			IFolder resourceFolder = liferayProject.getSourceFolder("resources");

			IFile authIni = resourceFolder.getFile(new Path(componentClassName.toLowerCase() + "/userauth.ini"));

			if (FileUtil.notExists(authIni)) {
				createSampleFile(authIni, "authenticator/authenticator-sample.ini");
			}
		}
		catch (Exception e) {
			throw new CoreException(ProjectCore.createErrorStatus(e));
		}
	}

	@Override
	protected List<Artifact> getComponentDependencies() throws CoreException {
		List<Artifact> dependencies = super.getComponentDependencies();

		dependencies.add(new Artifact("org.apache.shiro", "shiro-core", "1.1.0", "compileOnly", null));

		return dependencies;
	}

	@Override
	protected String getExtensionClass() {
		return _EXTENSION_CLASS;
	}

	@Override
	protected List<String> getImports() {
		List<String> imports = new ArrayList<>();

		imports.add("com.liferay.portal.kernel.log.Log");
		imports.add("com.liferay.portal.kernel.log.LogFactoryUtil");
		imports.add("com.liferay.portal.kernel.security.auth.AuthException");
		imports.add("com.liferay.portal.kernel.security.auth.Authenticator");
		imports.add("java.util.Map");
		imports.add("org.apache.shiro.SecurityUtils");
		imports.add("org.apache.shiro.authc.AuthenticationException");
		imports.add("org.apache.shiro.authc.UsernamePasswordToken");
		imports.add("org.apache.shiro.config.IniSecurityManagerFactory");
		imports.add("org.apache.shiro.mgt.SecurityManager");
		imports.add("org.apache.shiro.subject.Subject");
		imports.add("org.apache.shiro.util.Factory");
		imports.add("org.osgi.service.component.annotations.Activate");

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

	@Override
	protected void setBndProperties(BndProperties bndProperty) {
		bndProperty.addValue("-includeresource", new BndPropertiesValue("@shiro-core-1.1.0.jar"));
		bndProperty.addValue("-sources", new BndPropertiesValue("true"));
	}

	private static final String _EXTENSION_CLASS = "Authenticator.class";

	private static final String[] _PROPERTIES_LIST = {"key=auth.pipeline.pre"};

	private static final String _SUPER_CLASS = "Authenticator";

	private static final String _TEMPLATE_FILE = "authenticator/authenticator.ftl";

}