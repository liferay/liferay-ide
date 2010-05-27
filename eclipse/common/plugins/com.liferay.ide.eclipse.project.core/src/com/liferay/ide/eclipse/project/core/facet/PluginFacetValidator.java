/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.project.core.facet;

import com.liferay.ide.eclipse.project.core.ProjectCorePlugin;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.SDK;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectValidator;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * @author Greg Amerson
 */
public abstract class PluginFacetValidator implements IFacetedProjectValidator {

	public final static String MARKER_ID = "com.liferay.ide.eclipse.project.core.facet.validator";

	public void validate(IFacetedProject fproj)
		throws CoreException {

		if (fproj == null) {
			return;
		}

		// check for an SDK
		SDK projectSDK = null;

		try {
			projectSDK = ProjectUtil.getSDK(fproj, getProjectFacet());
		}
		catch (Exception e) {
			ProjectCorePlugin.logError(e);
		}

		if (projectSDK == null) {
			fproj.createErrorMarker("No Liferay SDK configured on project " + fproj.getProject().getName());

			return;
		}

		if (projectSDK != null) {
			IStatus status = projectSDK.validate();

			if (!status.isOK()) {
				fproj.createErrorMarker(MARKER_ID + ".sdkError", "Configured Liferay SDK is invalid: " +
					status.getMessage());
			}
		}

	}

	protected abstract IProjectFacet getProjectFacet();

}
