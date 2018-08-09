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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;

/**
 * @author Kuo Zhang
 */
public class CompoundRegion implements IRegion {

	public CompoundRegion(ITextViewer textViewer, int textOffset) {
		this.textViewer = textViewer;
		this.textOffset = textOffset;
	}

	public void addRegion(IRegion region) {
		Iterator<IRegion> it = _regions.iterator();

		IRegion r;

		while (it.hasNext()) {
			r = it.next();

			if (r.equals(region)) {
				return;
			}

			// In jsp editor, TempMarker and TempRegion are used,
			// but MarkerRegion still have a higher priority,
			// if MarkerRegion and TemporaryRegion represent the same problem,
			// keep the MarkerRegion

			if (region instanceof TemporaryRegion && r instanceof MarkerRegion) {
				if (_compareRegions((MarkerRegion)r, (TemporaryRegion)region)) {
					return;
				}
			}

			if (region instanceof MarkerRegion && r instanceof TemporaryRegion) {
				if (_compareRegions((MarkerRegion)region, (TemporaryRegion)r)) {
					it.remove();
				}
			}
		}

		_regions.add(region);
		int start = Math.min(region.getOffset(), _offset);
		int end = Math.max(region.getOffset() + region.getLength(), _offset + _length);
		_offset = start;
		_length = end - start;
	}

	public int getLength() {
		return _length;
	}

	public int getOffset() {
		return _offset;
	}

	public List<IRegion> getRegions() {
		return _regions;
	}

	public final int textOffset;
	public final ITextViewer textViewer;

	// Compare MarkerRegion and TemporayRegion

	private boolean _compareRegions(MarkerRegion m, TemporaryRegion t) {
		try {
			MarkerAnnotation annotation = m.getAnnotation();
			TemporaryAnnotation annotation2 = t.getAnnotation();

			if ((m.getLength() == t.getLength()) && (m.getOffset() == t.getOffset()) &&
				StringUtil.equals(annotation.getText(), annotation2.getText())) {

				return true;
			}
		}
		catch (NullPointerException npe) {
			return false;
		}

		return false;
	}

	private int _length = Integer.MIN_VALUE;
	private int _offset = Integer.MAX_VALUE;
	private List<IRegion> _regions = new ArrayList<>();

}