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

package com.liferay.ide.project.core.modules;

/**
 * @author Simon Jiang
 */
public class BndPropertiesValue {

	public BndPropertiesValue() {
	}

	public BndPropertiesValue(String value) {
		_formatedValue = value;
		_originalValue = value;
	}

	public BndPropertiesValue(String formatedValue, String originalValue) {
		_formatedValue = formatedValue;
		_originalValue = originalValue;
	}

	public String getFormatedValue() {
		return _formatedValue;
	}

	public int getKeyIndex() {
		return _keyIndex;
	}

	public String getOriginalValue() {
		return _originalValue;
	}

	public boolean isMultiLine() {
		return _multiLine;
	}

	public void setFormatedValue(String formatedValue) {
		_formatedValue = formatedValue;
	}

	public void setKeyIndex(int keyIndex) {
		_keyIndex = keyIndex;
	}

	public void setMultiLine(boolean multiLine) {
		_multiLine = multiLine;
	}

	public void setOriginalValue(String originalValue) {
		_originalValue = originalValue;
	}

	private String _formatedValue;
	private int _keyIndex;
	private boolean _multiLine;
	private String _originalValue;

}