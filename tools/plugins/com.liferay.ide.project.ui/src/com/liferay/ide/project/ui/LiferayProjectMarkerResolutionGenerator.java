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

package com.liferay.ide.project.ui;

import com.liferay.ide.project.core.PluginsSDKProjectRuntimeValidator;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * @author Kuo Zhang
 * @author Simon Jiang
 */
public class LiferayProjectMarkerResolutionGenerator implements IMarkerResolutionGenerator2 {

	public IMarkerResolution[] getResolutions(IMarker marker) {
		IMarkerResolution resolution = null;

		try {
			final String markerSourceId = (String)marker.getAttribute(IMarker.SOURCE_ID);

			if (markerSourceId.equals(PluginsSDKProjectRuntimeValidator.ID_PRIMARY_RUNTIME_NOT_SET)) {
				resolution = new PrimaryRuntimeNotSetResolution();
			}
			else if (markerSourceId.equals(PluginsSDKProjectRuntimeValidator.ID_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME)) {
				resolution = new PrimaryRuntimeNotLiferayRuntimeResolution();
			}
			else if (markerSourceId.equals(PluginsSDKProjectRuntimeValidator.ID_PLUGINS_SDK_NOT_SET)) {
				resolution = new PluginsSDKNotSetResolution();
			}
		}
		catch (CoreException ce) {
			ProjectUI.logError("Marker can not be found.", ce);
		}

		return new IMarkerResolution[] {resolution};
	}

	public boolean hasResolutions(IMarker marker) {
		try {
			return ProjectCore.LIFERAY_PROJECT_MARKER_TYPE.equals(marker.getType());
		}
		catch (CoreException ce) {
			ProjectUI.logError("The marker does not exist.", ce);
		}

		return false;
	}

}