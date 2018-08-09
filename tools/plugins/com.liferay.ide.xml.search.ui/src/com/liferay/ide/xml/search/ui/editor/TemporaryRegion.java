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

import com.liferay.ide.core.util.StringUtil;

import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;

/**
 * the region used to locate TemporaryAnnotation
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class TemporaryRegion implements IRegion {

	public TemporaryRegion(int offset, int length, TemporaryAnnotation applicable) {
		_offset = offset;
		_length = length;
		_annotation = applicable;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TemporaryRegion)) {
			return false;
		}

		TemporaryRegion compared = (TemporaryRegion)obj;

		String annotationText = _annotation.getText();
		TemporaryAnnotation comparedAnnotation = compared._annotation;

		if ((_length == compared._length) && (_offset == compared._offset) &&
			StringUtil.equals(annotationText, comparedAnnotation.getText())) {

			return true;
		}

		return super.equals(obj);
	}

	public TemporaryAnnotation getAnnotation() {
		return _annotation;
	}

	public int getLength() {
		return _length;
	}

	public int getOffset() {
		return _offset;
	}

	private final TemporaryAnnotation _annotation;
	private final int _length;
	private final int _offset;

}