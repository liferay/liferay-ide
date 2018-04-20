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

package com.liferay.ide.server.util;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.IPluginPublisher;
import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
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

		if (ListUtil.isNotEmpty(moduleTree) && (moduleTree[0].getProject() != null)) {
			IProject project = moduleTree[0].getProject();

			IFacetedProject facetedProject = ServerUtil.getFacetedProject(project);

			if (facetedProject != null) {
				IProjectFacet liferayFacet = ServerUtil.getLiferayFacet(facetedProject);

				if (liferayFacet != null) {
					String facetId = liferayFacet.getId();

					IRuntime runtime = null;

					try {
						IServer server = delegate.getServer();

						runtime = server.getRuntime();

						IRuntimeType runtimeType = runtime.getRuntimeType();

						if (runtime != null) {
							IPluginPublisher pluginPublisher = LiferayServerCore.getPluginPublisher(
								facetId, runtimeType.getId());

							if (pluginPublisher != null) {
								retval = pluginPublisher.prePublishModule(
									delegate, kind, deltaKind, moduleTree, resourceDelta, monitor);
							}
						}
					}
					catch (Exception e) {
						LiferayServerCore.logError("Plugin publisher failed", e);
					}
				}
			}
		}

		return retval;
	}

}