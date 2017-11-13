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

package com.liferay.ide.project.core;

import com.liferay.ide.core.ExtensionReader;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.content.IContentType;

/**
 * @author Kuo Zhang
 */
public class LiferayDescriptorHelperReader extends ExtensionReader<LiferayDescriptorHelper> {

	public static LiferayDescriptorHelperReader getInstance() {
		if (_instance == null) {
			_instance = new LiferayDescriptorHelperReader();
		}

		return _instance;
	}

	public LiferayDescriptorHelper[] getAllHelpers() {
		return getExtensions().toArray(new LiferayDescriptorHelper[0]);
	}

	public LiferayDescriptorHelper[] getHelpers(IContentType contentType) {
		List<LiferayDescriptorHelper> retval = new ArrayList<>();

		for (LiferayDescriptorHelper helper : getAllHelpers()) {
			if (contentType.equals(helper.getContentType())) {
				retval.add(helper);
			}
		}

		return retval.toArray(new LiferayDescriptorHelper[0]);
	}

	@Override
	protected LiferayDescriptorHelper initElement(IConfigurationElement configElement, LiferayDescriptorHelper helper) {
		helper.setContentType(configElement.getAttribute(_CONTENTTYPEBINDING_ELEMENT));

		return helper;
	}

	private LiferayDescriptorHelperReader() {
		super(ProjectCore.PLUGIN_ID, _EXTENSION, _HELPER_ELEMENT);
	}

	private static final String _CONTENTTYPEBINDING_ELEMENT = "contentTypeBinding";

	private static final String _EXTENSION = "liferayDescriptorHelpers";

	private static final String _HELPER_ELEMENT = "liferayDescriptorHelper";

	private static LiferayDescriptorHelperReader _instance;

}