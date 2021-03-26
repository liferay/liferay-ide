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

package com.liferay.ide.server.core.portal.docker;

import com.liferay.ide.server.core.portal.docker.PortalDockerServerStreamsProxy.LiferayTaskProgressListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */
public interface IDockerSupporter {

	public static final String ID = "com.liferay.ide.server.core.dockerSupporters";

	public boolean canPublishModule(IServer server, IModule module);

	public void createDockerContainer(IProgressMonitor monitor);

	public void dockerDeploy(IProject project, IProgressMonitor monitor);

	public void logDockerContainer(IProject project, LiferayTaskProgressListener listener, IProgressMonitor monitor);

	public void removeDockerContainer(IProgressMonitor monitor);

	public void startDockerContainer(IProgressMonitor monitor);

	public void stopDockerContainer(IProgressMonitor monitor);

}