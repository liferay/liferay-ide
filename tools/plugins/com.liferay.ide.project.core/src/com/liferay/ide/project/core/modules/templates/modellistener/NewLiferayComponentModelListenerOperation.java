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

package com.liferay.ide.project.core.modules.templates.modellistener;

import com.liferay.ide.project.core.modules.ServiceContainer;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;
import com.liferay.ide.project.core.util.TargetPlatformUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentModelListenerOperation extends AbstractLiferayComponentTemplate {

	public NewLiferayComponentModelListenerOperation() {
	}

	@Override
	protected List<String[]> getComponentDependency() throws CoreException {
		List<String[]> componentDependency = super.getComponentDependency();

		try {
			ServiceContainer serviceBundle = TargetPlatformUtil.getServiceBundle(serviceName);

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
		return _EXTENSION_CLASS;
	}

	@Override
	protected List<String> getImports() {
		List<String> imports = new ArrayList<>();

		imports.add("com.liferay.portal.kernel.exception.ModelListenerException");
		imports.add("com.liferay.portal.kernel.model.BaseModelListener");
		imports.add(modelClass);
		imports.add("com.liferay.portal.kernel.model.ModelListener");

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

	private static final String _EXTENSION_CLASS = "ModelListener.class";

	private static final String[] _PROPERTIES_LIST = {};

	private static final String _SUPER_CLASS = "BaseModelListener";

	private static final String _TEMPLATE_FILE = "modellistener/modellistener.ftl";

}