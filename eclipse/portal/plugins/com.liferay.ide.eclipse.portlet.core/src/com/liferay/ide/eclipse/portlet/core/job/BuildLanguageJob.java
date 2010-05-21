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

package com.liferay.ide.eclipse.portlet.core.job;

import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.job.SDKJob;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Greg Amerson
 */
public class BuildLanguageJob extends SDKJob {

	protected IFile langFile;

	public BuildLanguageJob(IFile langFile) {
		super("Build languages");

		this.langFile = langFile;

		setUser(true);

		setProject(langFile.getProject());
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Building languages...", 100);

		IWorkspaceDescription desc = ResourcesPlugin.getWorkspace().getDescription();

		boolean saveAutoBuild = desc.isAutoBuilding();

		desc.setAutoBuilding(false);

		try {
			ResourcesPlugin.getWorkspace().setDescription(desc);
		}
		catch (CoreException e1) {
			return PortletCore.createErrorStatus(e1);
		}

		SDK sdk = getSDK();

		Map<String, String> properties = new HashMap<String, String>();

		String appServerDir = null;

		try {
			appServerDir = ServerUtil.getRuntime(getProject()).getLocation().toOSString();
		}
		catch (CoreException e) {
			return PortletCore.createErrorStatus(e);
		}

		properties.put("app.server.type", "tomcat");
		properties.put("app.server.dir", appServerDir);
		properties.put("app.server.deploy.dir", appServerDir + "/webapps");
		properties.put("app.server.lib.global.dir", appServerDir + "/lib/ext");
		properties.put("app.server.portal.dir", appServerDir + "/webapps/ROOT");

		monitor.worked(10);

		sdk.buildLanguage(getProject(), langFile, properties);

		monitor.worked(90);

		try {
			getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (CoreException e) {
			return PortletCore.createErrorStatus(e);
		}

		try {
			getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		}
		catch (CoreException e) {
			return PortletCore.createErrorStatus(e);
		}

		try {
			getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (CoreException e) {
			return PortletCore.createErrorStatus(e);
		}

		desc = ResourcesPlugin.getWorkspace().getDescription();
		desc.setAutoBuilding(saveAutoBuild);

		try {
			ResourcesPlugin.getWorkspace().setDescription(desc);
		}
		catch (CoreException e1) {
			return PortletCore.createErrorStatus(e1);
		}

		return Status.OK_STATUS;
	}

}
