/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.tomcat.core.util;

import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.server.core.IPluginPublisher;
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;
import com.liferay.ide.eclipse.server.tomcat.core.LiferayTomcatPlugin;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Greg Amerson
 */
public class LiferayPublishHelper {

	public static boolean prePublishModule(
		ServerBehaviourDelegate delegate, int kind, int deltaKind, IModule[] moduleTree,
		IModuleResourceDelta[] resourceDelta, IProgressMonitor monitor) {

		boolean retval = true;

		if (moduleTree != null && moduleTree.length > 0 && moduleTree[0].getProject() != null) {
			IProject project = moduleTree[0].getProject();

			IFacetedProject facetedProject = ProjectUtil.getFacetedProject(project);

			if (facetedProject != null) {
				IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet(facetedProject);

				if (liferayFacet != null) {
					String facetId = liferayFacet.getId();

					IRuntime runtime = null;

					try {
						runtime = ServerUtil.getRuntime(project);
					}
					catch (CoreException ce) {
						// do nothing
					}

					if (runtime != null) {
						IPluginPublisher pluginPublisher =
							LiferayServerCorePlugin.getPluginPublisher(facetId, runtime.getRuntimeType().getId());

						if (pluginPublisher != null) {
							try {
								retval =
									pluginPublisher.prePublishModule(
										delegate, kind, deltaKind, moduleTree, resourceDelta, monitor);
							}
							catch (Exception e) {
								LiferayTomcatPlugin.logError("Plugin publisher failed", e);
							}
						}
					}
				}
			}
		}

		return retval;
	}

}
