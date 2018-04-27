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

package com.liferay.ide.core.util;

import com.liferay.ide.core.LiferayCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Gregory Amerson
 */
public class MarkerUtil {

	public static void clearMarkers(IResource resource, String makerType, String sourceId) {
		if (!resource.isAccessible()) {
			return;
		}

		try {
			IMarker[] markers = resource.findMarkers(makerType, true, IResource.DEPTH_INFINITE);

			for (IMarker marker : markers) {
				try {
					if ((sourceId == null) ||
						((sourceId != null) && sourceId.equals(marker.getAttribute(IMarker.SOURCE_ID)))) {

						marker.delete();
					}
				}
				catch (CoreException ce) {
					LiferayCore.logError("Unable to delete marker", ce);
				}
			}
		}
		catch (CoreException ce) {
			LiferayCore.logError("Unable to find markers", ce);
		}
	}

	public static IMarker[] findMarkers(IResource resource, String markerType, String sourceId) {
		if (!resource.isAccessible()) {
			return new IMarker[0];
		}

		List<IMarker> retval = new ArrayList<>();

		try {
			IMarker[] markers = resource.findMarkers(markerType, true, IResource.DEPTH_INFINITE);

			if (sourceId != null) {
				for (IMarker marker : markers) {
					if (sourceId.equals(marker.getAttribute(IMarker.SOURCE_ID, ""))) {
						retval.add(marker);
					}
				}
			}
			else {
				Collections.addAll(retval, markers);
			}
		}
		catch (CoreException ce) {
			LiferayCore.logError(ce);
		}

		return retval.toArray(new IMarker[0]);
	}

	public static void setMarker(
			IResource resource, String markerType, int markerSeverity, String markerMsg, String markerLocation,
			String markerSourceId)
		throws CoreException {

		IMarker marker = resource.createMarker(markerType);

		marker.setAttribute(IMarker.SEVERITY, markerSeverity);
		marker.setAttribute(IMarker.MESSAGE, markerMsg);
		marker.setAttribute(IMarker.LOCATION, markerLocation);
		marker.setAttribute(IMarker.SOURCE_ID, markerSourceId);
	}

}