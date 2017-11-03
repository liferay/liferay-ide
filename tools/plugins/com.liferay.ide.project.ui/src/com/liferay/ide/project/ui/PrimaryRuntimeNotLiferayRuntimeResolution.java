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

import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;

/**
 * @author Kuo Zhang
 */
public final class PrimaryRuntimeNotLiferayRuntimeResolution extends PrimaryRuntimeNotSetResolution {

	public void run(IMarker marker) {
		IFacetedProject fproj = ProjectUtil.getFacetedProject((IProject)marker.getResource());

		try {
			fproj.setPrimaryRuntime(null, null);
			fproj.setTargetedRuntimes(null, null);
		}
		catch (CoreException ce) {
			ProjectUI.logError(ce);
		}

		super.run(marker);
	}

}