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

package com.liferay.ide.eclipse.portlet.core;

import com.liferay.ide.eclipse.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.server.core.AbstractPluginDeployer;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Greg Amerson
 */
public class ExtPluginDeployer extends AbstractPluginDeployer {

	public ExtPluginDeployer() {
		super();
	}

	public ExtPluginDeployer(String facetId) {
		super(facetId);
	}

	@Override
	public boolean prePublishModule(int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor) {
		boolean publish = false;

		if (kind != IServer.PUBLISH_FULL || moduleTree == null) {
			return publish;
		}

		try {
			if (deltaKind == ServerBehaviourDelegate.ADDED) {
				addExtModule(moduleTree[0]);
			}
			else if (deltaKind == ServerBehaviourDelegate.REMOVED) {
				removeExtModule(moduleTree[0]);
			}
		}
		catch (Exception e) {
			PortletCore.logError("Failed pre-publishing ext module.", e);
		}

		return publish;
	}

	protected void addExtModule(IModule module)
		throws CoreException {
		SDK sdk = null;
		IProject project = module.getProject();

		sdk = ProjectUtil.getSDK(project, IPluginFacetConstants.LIFERAY_EXT_FACET);

		if (sdk == null) {
			throw new CoreException(
				PortletCore.createErrorStatus("No SDK for project configured. Could not deploy ext module"));
		}

		IRuntime runtime = ServerUtil.getRuntime(project);

		String appServerDir = runtime.getLocation().toOSString();
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("app.server.type", "tomcat");
		properties.put("app.server.dir", appServerDir);
		properties.put("app.server.deploy.dir", appServerDir + "/webapps");
		properties.put("app.server.lib.global.dir", appServerDir + "/lib/ext");
		properties.put("app.server.portal.dir", appServerDir + "/webapps/ROOT");

		IStatus status = sdk.deployExtPlugin(project, properties);
		if (!status.isOK()) {
			throw new CoreException(status);
		}
	}

	protected void removeExtModule(IModule module) {

		// TODO try to uninstall ext files
	}
}
