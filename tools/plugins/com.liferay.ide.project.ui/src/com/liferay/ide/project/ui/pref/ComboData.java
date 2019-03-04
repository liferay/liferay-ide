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

package com.liferay.ide.project.ui.pref;

/**
 * @author Gregory Amerson
 */
public class ComboData {

	public ComboData(String key, int[] severities, int index) {
		_fKey = key;
		_fSeverities = severities;
		_fIndex = index;
	}

	public int getIndex() {
		return _fIndex;
	}

	public String getKey() {
		return _fKey;
	}

	public int getSeverity() {
		if ((_fIndex >= 0) && (_fSeverities != null) && (_fIndex < _fSeverities.length)) {
			return _fSeverities[_fIndex];
		}

		return -1;
	}

	public boolean isChanged() {
		if (_fSeverities[_fIndex] != originalSeverity) {
			return true;
		}

		return false;
	}

	public void setIndex(int index) {
		_fIndex = index;
	}

	/**
	 * Sets the severity index based on <code>severity</code>. If the severity
	 * doesn't exist, the index is set to -1.
	 *
	 * @param severity
	 *            the severity level
	 */
	public void setSeverity(int severity) {
		for (int i = 0; (_fSeverities != null) && (i < _fSeverities.length); i++) {
			if (_fSeverities[i] == severity) {
				_fIndex = i;

				return;
			}
		}

		_fIndex = -1;
	}

	public int originalSeverity = -2;

	private int _fIndex;
	private String _fKey;
	private int[] _fSeverities;

}