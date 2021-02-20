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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.server.core.gogo.GogoBundleDeployer;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.PublishOperation;

/**
 * @author Gregory Amerson
 */
public class BundlePublishOperation extends PublishOperation {

	public BundlePublishOperation(IServer s, IModule[] modules) {
		server = s;

		this.modules = new ArrayList<>(Arrays.asList(modules));

		IRuntime serverRuntime = server.getRuntime();

		portalRuntime = (PortalRuntime)serverRuntime.loadAdapter(PortalRuntime.class, null);

		if (portalRuntime == null) {
			throw new IllegalArgumentException("Could not get portal runtime from server " + s.getName());
		}

		portalServerBehavior = (PortalServerBehavior)server.loadAdapter(PortalServerBehavior.class, null);

		if (portalServerBehavior == null) {
			throw new IllegalArgumentException("Could not get portal server behavior from server " + s.getName());
		}
	}

	public void addModule(IModule[] modules) {
		for (IModule m : modules) {
			this.modules.add(m);
		}
	}

	@Override
	public void execute(IProgressMonitor monitor, IAdaptable info) throws CoreException {
	}

	@Override
	public int getKind() {
		return REQUIRED;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	protected GogoBundleDeployer createBundleDeployer() throws Exception {
		return ServerUtil.createBundleDeployer(server);
	}

	protected List<IModule> modules;
	protected PortalRuntime portalRuntime;
	protected PortalServerBehavior portalServerBehavior;
	protected IServer server;

}