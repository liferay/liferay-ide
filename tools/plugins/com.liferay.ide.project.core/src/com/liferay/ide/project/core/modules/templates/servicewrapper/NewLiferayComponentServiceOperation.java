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

package com.liferay.ide.project.core.modules.templates.servicewrapper;

import com.liferay.ide.project.core.modules.ServiceContainer;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;
import com.liferay.ide.project.core.util.TargetPlatformUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentServiceOperation extends AbstractLiferayComponentTemplate {

	public NewLiferayComponentServiceOperation() {
	}

	@Override
	protected List<String[]> getComponentDependency() throws CoreException {
		List<String[]> componentDependency = super.getComponentDependency();

		try {
			ServiceContainer serviceBundle = TargetPlatformUtil.getServiceWrapperBundle(serviceName);

			if (serviceBundle != null) {
				Version retriveVersion = new Version(serviceBundle.getBundleVersion());

				componentDependency.add(new String[] {
					serviceBundle.getBundleGroup(), serviceBundle.getBundleName(),
					retriveVersion.getMajor() + "." + retriveVersion.getMinor() + ".0"
				});
			}
		}
		catch (Exception e) {
		}

		return componentDependency;
	}

	@Override
	protected String getExtensionClass() {
		return "ServiceWrapper.class";
	}

	@Override
	protected List<String> getImports() {
		List<String> imports = new ArrayList<>();

		imports.add("com.liferay.portal.kernel.service.ServiceWrapper");
		imports.add(serviceName);
		imports.addAll(super.getImports());

		return imports;
	}

	@Override
	protected String getSuperClass() {
		if (serviceName != null) {
			int servicePos = serviceName.lastIndexOf(".");

			_serviceClassName = serviceName.substring(servicePos + 1);
		}

		return _serviceClassName;
	}

	@Override
	protected String getTemplateFile() {
		return _TEMPLATE_FILE;
	}

	private static final String _TEMPLATE_FILE = "servicewrapper/servicewrapper.ftl";

	private String _serviceClassName;

}