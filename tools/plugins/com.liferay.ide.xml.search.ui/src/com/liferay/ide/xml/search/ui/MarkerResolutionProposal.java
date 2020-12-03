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

package com.liferay.ide.xml.search.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

/**
 * @author Gregory Amerson
 */
public class MarkerResolutionProposal implements ICompletionProposal {

	public MarkerResolutionProposal(IMarkerResolution resolution, IMarker marker) {
		_resolution = resolution;
		_marker = marker;
	}

	public void apply(IDocument document) {
		_resolution.run(_marker);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof MarkerResolutionProposal)) {
			return false;
		}

		MarkerResolutionProposal other = (MarkerResolutionProposal)obj;

		if (_resolution == null) {
			if (other._resolution != null) {
				return false;
			}
		}
		else if (!_resolution.equals(other._resolution)) {
			return false;
		}

		return true;
	}

	public String getAdditionalProposalInfo() {
		if (_resolution instanceof IMarkerResolution2) {
			IMarkerResolution2 markerResolution = (IMarkerResolution2)_resolution;

			return markerResolution.getDescription();
		}

		String problemDesc = _marker.getAttribute(IMarker.MESSAGE, null);

		if (problemDesc != null) {
			return problemDesc;
		}

		return null;
	}

	public IContextInformation getContextInformation() {
		return null;
	}

	public String getDisplayString() {
		return _resolution.getLabel();
	}

	public Image getImage() {
		if (_resolution instanceof IMarkerResolution2) {
			IMarkerResolution2 markerResolution = (IMarkerResolution2)_resolution;

			return markerResolution.getImage();
		}

		return null;
	}

	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public int hashCode() {
		return 31 + ((_resolution == null) ? 0 : _resolution.hashCode());
	}

	private final IMarker _marker;
	private final IMarkerResolution _resolution;

}