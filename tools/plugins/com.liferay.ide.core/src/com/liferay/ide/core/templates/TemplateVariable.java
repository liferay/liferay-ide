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

package com.liferay.ide.core.templates;

import com.liferay.ide.core.util.CoreUtil;

/**
 * @author Gregory Amerson
 */
public class TemplateVariable {

	public TemplateVariable(String varName, String reqVal) {
		_name = varName;

		if (CoreUtil.isNullOrEmpty(reqVal)) {
			_required = Boolean.FALSE;
		}
		else {
			_required = Boolean.parseBoolean(reqVal);
		}
	}

	public String getName() {
		return _name;
	}

	public boolean isRequired() {
		return _required;
	}

	private String _name;
	private boolean _required;

}