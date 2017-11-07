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

package com.liferay.ide.maven.ui;

import com.liferay.ide.maven.core.ILiferayMavenConstants;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * @author Gregory Amerson
 */
public class ConfigProblemMarkerResolutionGenerator implements IMarkerResolutionGenerator2 {

	public IMarkerResolution[] getResolutions(IMarker marker) {
		IMarkerResolution[] retval = null;

		if (correctMarker(marker)) {
			retval = new IMarkerResolution[] {
				new SelectActiveProfilesMarkerResolution(), new NewLiferayProfileMarkerResolution()
			};
		}

		return retval;
	}

	public boolean hasResolutions(IMarker marker) {
		return correctMarker(marker);
	}

	protected boolean correctMarker(IMarker marker) {
		try {
			return ILiferayMavenConstants.LIFERAY_MAVEN_MARKER_CONFIGURATION_WARNING_ID.equals(marker.getType());
		}
		catch (CoreException ce) {
		}

		return false;
	}

}