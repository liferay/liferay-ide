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

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.server.tomcat.core.util.LiferayTomcatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.tomcat.core.internal.Tomcat70Handler;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class LiferayTomcat70Handler extends Tomcat70Handler implements ILiferayTomcatHandler {

	@Override
	public IStatus canAddModule(IModule module) {
		IStatus status = LiferayTomcatUtil.canAddModule(module, currentServer);

		if (!status.isOK()) {
			return status;
		}

		return super.canAddModule(module);
	}

	@Override
	public String[] getRuntimeVMArguments(IPath installPath, IPath configPath, IPath deployPath, boolean testEnv) {
		List<String> runtimeVMArgs = new ArrayList<>();

		LiferayTomcatUtil.addRuntimeVMArgments(
			runtimeVMArgs, installPath, configPath, deployPath, testEnv, currentServer, getPortalServer());

		Collections.addAll(runtimeVMArgs, super.getRuntimeVMArguments(installPath, configPath, deployPath, testEnv));

		return runtimeVMArgs.toArray(new String[0]);
	}

	public void setCurrentServer(IServer server) {
		currentServer = server;
	}

	protected ILiferayTomcatServer getPortalServer() {
		if (portalServer == null) {
			portalServer = (ILiferayTomcatServer)getServer().loadAdapter(ILiferayTomcatServer.class, null);
		}

		return portalServer;
	}

	protected IServer getServer() {
		return currentServer;
	}

	protected IServer currentServer;
	protected ILiferayTomcatServer portalServer;

}