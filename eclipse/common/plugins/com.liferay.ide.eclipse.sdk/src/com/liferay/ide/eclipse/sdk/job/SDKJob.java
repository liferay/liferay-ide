/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.sdk.job;

import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.osgi.service.prefs.Preferences;

/**
 * @author Greg Amerson
 */
public abstract class SDKJob extends Job {

	public static final String PREF_NODE_LIFERAY_PLUGIN_PROJECT = "liferay-plugin-project";

	protected IProject project;

	public SDKJob(String name) {
		super(name);
	}

	protected IProject getProject() {
		return this.project;
	}

	protected SDK getSDK() {
		if (project == null) {
			return null;
		}
		
		IFacetedProject fProject;
		try {
			fProject = ProjectFacetsManager.create(this.project);
			
			for (IProjectFacetVersion fv : fProject.getProjectFacets()) {

				Preferences preferences = fProject.getPreferences(fv.getProjectFacet());
				if (preferences.nodeExists(PREF_NODE_LIFERAY_PLUGIN_PROJECT)) {
					String name =
						preferences.node(PREF_NODE_LIFERAY_PLUGIN_PROJECT).get(
							ISDKConstants.PROPERTY_NAME, "");
					
					return SDKManager.getInstance().getSDK(name);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	protected void setProject(IProject project) {
		this.project = project;
	}
}
