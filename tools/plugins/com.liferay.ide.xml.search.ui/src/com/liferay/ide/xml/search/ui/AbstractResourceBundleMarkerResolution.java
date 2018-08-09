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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.server.util.ComponentUtil;
import com.liferay.ide.xml.search.ui.validators.LiferayBaseValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.views.markers.internal.Util;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public abstract class AbstractResourceBundleMarkerResolution extends CommonWorkbenchMarkerResolution {

	public AbstractResourceBundleMarkerResolution(IMarker marker) {
		super(marker);
	}

	public IMarker[] findOtherMarkers(IMarker[] markers) {
		List<IMarker> otherMarkers = new ArrayList<>();

		for (IMarker marker : markers) {
			if ((marker != null) && !marker.equals(this.marker) &&
				XMLSearchConstants.RESOURCE_BUNDLE_QUERY_ID.equals(
					marker.getAttribute(LiferayBaseValidator.MARKER_QUERY_ID, ""))) {

				otherMarkers.add(marker);
			}
		}

		return otherMarkers.toArray(new IMarker[0]);
	}

	public String getDescription() {
		return getLabel();
	}

	public void run(IMarker[] markers, IProgressMonitor monitor) {
		Set<IFile> files = new HashSet<>();

		for (IMarker marker : markers) {
			monitor.subTask(Util.getProperty(IMarker.MESSAGE, marker));

			resolve(marker);

			IResource resource = marker.getResource();

			if (resource instanceof IFile && !files.contains(resource)) {
				files.add((IFile)resource);
			}
		}

		for (IFile file : files) {
			ComponentUtil.validateFile(file, monitor);
		}
	}

	protected String getDefaultResourceValue(String resourceKey) {
		if (CoreUtil.isNullOrEmpty(resourceKey)) {
			return "";
		}

		String[] words = resourceKey.split("-");

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < words.length; i++) {
			String word = words[i];

			if (i == 0) {
				word = word.replaceFirst(word.substring(0, 1), StringUtil.toUpperCase(word.substring(0, 1)));
			}

			sb.append(word);

			sb.append(" ");
		}

		return StringUtil.trim(sb);
	}

	protected String getResourceKey(IMarker marker) {
		if (marker == null) {
			return "";
		}

		return marker.getAttribute(XMLSearchConstants.TEXT_CONTENT, "");
	}

}