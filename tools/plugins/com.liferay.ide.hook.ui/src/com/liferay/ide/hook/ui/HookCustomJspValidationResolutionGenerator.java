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

package com.liferay.ide.hook.ui;

import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.hook.core.util.HookUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * @author Simon Jiang
 */
public class HookCustomJspValidationResolutionGenerator implements IMarkerResolutionGenerator2 {

	public IMarkerResolution[] getResolutions(IMarker marker) {
		if (hasResolutions(marker)) {
			return new IMarkerResolution[] {new HookCustomJspValidationResolution()};
		}
		else {
			return new IMarkerResolution[0];
		}
	}

	public boolean hasResolutions(IMarker marker) {
		boolean hasResolution = false;

		try {
			Object severity = marker.getAttribute(IMarker.SEVERITY);

			if ((severity != null) && severity.equals(IMarker.SEVERITY_ERROR)) {
				String validationId = (String)marker.getAttribute("ValidationId");

				if (validationId.equalsIgnoreCase(HookCore.VALIDATOR_ID)) {
					IProject project = MarkerUtil.getProject(marker);

					IPath customJspPath = HookUtil.getCustomJspPath(project);

					if (customJspPath != null) {
						IPath jspPath = MarkerUtil.getProjectRelativePath(marker);

						IPath relativeCustomJspPath = customJspPath.makeRelativeTo(project.getFullPath());

						if (relativeCustomJspPath.isPrefixOf(jspPath)) {
							hasResolution = true;
						}
					}
				}
			}
		}
		catch (Exception e) {
			HookCore.logError("Get marker attribute error. ", e);
		}

		return hasResolution;
	}

}