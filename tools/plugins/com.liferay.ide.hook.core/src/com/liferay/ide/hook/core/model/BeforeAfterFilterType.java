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

package com.liferay.ide.hook.core.model;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
public enum BeforeAfterFilterType {

	@EnumSerialization(caseSensitive = true, primary = "after-filter")
	@Label(standard = "After Filter")
	AFTER_FILTER("after-filter"),
	@EnumSerialization(caseSensitive = true, primary = "before-filter")
	@Label(standard = "Before Filter")
	BEFORE_FILTER("before-filter");

	public String getText() {
		return _text;
	}

	private BeforeAfterFilterType(String primary) {
		_text = primary;
	}

	private String _text;

}