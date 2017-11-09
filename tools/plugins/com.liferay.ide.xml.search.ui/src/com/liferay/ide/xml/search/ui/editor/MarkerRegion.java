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

package com.liferay.ide.xml.search.ui.editor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * @author Kuo Zhang
 */
public class MarkerRegion implements IRegion {

	public MarkerRegion(int offset, int length, MarkerAnnotation applicable) {
		_offset = offset;
		_length = length;
		_annotation = applicable;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MarkerRegion)) {
			return false;
		}

		MarkerRegion compared = (MarkerRegion)obj;

		if ((_length == compared._length) && (_offset == compared._offset) && (_annotation.getText() != null) &&
			(compared._annotation.getText() != null) && _annotation.getText().equals(compared._annotation.getText())) {

			return true;
		}

		return super.equals(obj);
	}

	public MarkerAnnotation getAnnotation() {
		return _annotation;
	}

	public int getLength() {
		return _length;
	}

	public int getOffset() {
		return _offset;
	}

	private final MarkerAnnotation _annotation;
	private final int _length;
	private final int _offset;

}