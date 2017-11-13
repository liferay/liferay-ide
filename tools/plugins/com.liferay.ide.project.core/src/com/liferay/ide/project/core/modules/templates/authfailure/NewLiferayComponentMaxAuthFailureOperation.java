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

package com.liferay.ide.project.core.modules.templates.authfailure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentMaxAuthFailureOperation extends NewLiferayComponentAuthFailureOperation {

	public NewLiferayComponentMaxAuthFailureOperation() {
	}

	@Override
	protected List<String> getProperties() {
		List<String> properties = new ArrayList<>();

		Collections.addAll(properties, _PROPERTIES_MAX_LIST);

		return properties;
	}

	private static final String[] _PROPERTIES_MAX_LIST = {"key=auth.max.failures"};

}