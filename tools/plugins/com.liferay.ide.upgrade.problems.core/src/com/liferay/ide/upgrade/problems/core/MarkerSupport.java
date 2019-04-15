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

package com.liferay.ide.upgrade.problems.core;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;

import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public interface MarkerSupport {

	public default void addMarkers(Collection<UpgradeProblem> upgradeProblems) {
		upgradeProblems.stream(
		).filter(
			upgradeProblem -> FileUtil.exists(upgradeProblem.getResource())
		).forEach(
			upgradeProblem -> {
				IResource resource = upgradeProblem.getResource();

				try {
					IMarker marker = resource.createMarker(UpgradeProblem.MARKER_TYPE);

					upgradeProblem.setMarkerId(marker.getId());

					upgradeProblemToMarker(upgradeProblem, marker);
				}
				catch (CoreException ce) {
				}
			}
		);
	}

	public default void deleteMarker(IMarker marker) {
		try {
			marker.delete();
		}
		catch (CoreException ce) {
		}
	}

	public default IMarker findMarker(UpgradeProblem upgradeProblem) {
		if (upgradeProblem == null) {
			return null;
		}

		IResource resource = upgradeProblem.getResource();

		long markerId = upgradeProblem.getMarkerId();

		try {
			return resource.findMarker(markerId);
		}
		catch (CoreException ce) {
			return null;
		}
	}

	public default boolean markerExists(IMarker marker) {
		if ((marker != null) && marker.exists()) {
			return true;
		}

		return false;
	}

	public default void removeMarkers(Collection<UpgradeProblem> upgradeProblems) {
		upgradeProblems.stream(
		).map(
			this::findMarker
		).filter(
			this::markerExists
		).forEach(
			this::deleteMarker
		);
	}

	public default void upgradeProblemToMarker(UpgradeProblem upgradeProblem, IMarker marker) throws CoreException {
		marker.setAttribute(IMarker.CHAR_START, upgradeProblem.getStartOffset());
		marker.setAttribute(IMarker.CHAR_END, upgradeProblem.getEndOffset());
		marker.setAttribute(IMarker.LINE_NUMBER, upgradeProblem.getLineNumber());
		marker.setAttribute(IMarker.MESSAGE, upgradeProblem.getTitle());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_AUTOCORRECTCONTEXT, upgradeProblem.getAutoCorrectContext());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_HTML, upgradeProblem.getHtml());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_SUMMARY, upgradeProblem.getSummary());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_STATUS, upgradeProblem.getStatus());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_TICKET, upgradeProblem.getTicket());
		marker.setAttribute(UpgradeProblem.MARKER_ATTRIBUTE_TYPE, upgradeProblem.getType());

		IResource resource = upgradeProblem.getResource();

		marker.setAttribute(IMarker.LOCATION, resource.getName());

		marker.setAttribute(IMarker.SEVERITY, upgradeProblem.getMarkerType());
	}

}