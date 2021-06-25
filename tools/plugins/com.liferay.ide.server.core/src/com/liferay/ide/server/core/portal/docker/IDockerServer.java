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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 * @author Ethan Sun
 */
public interface IDockerServer {

	public static final String DOCKER_DAEMON_CONNECTION = "docker-daemon-connection";

	public static final String DOCKER_REGISTRY_INFO = "docker-registry-info";

	public static final String ID = "com.liferay.ide.server.core.dockerServers";

	public static final String PROP_REGISTRY_URL = "registry.url";

	public static final String PROP_REGISTRY_URL_DXP = "https://registry.hub.docker.com/v2/repositories/liferay/dxp";

	public static final String PROP_REGISTRY_URL_PORTAL =
		"https://registry.hub.docker.com/v2/repositories/liferay/portal";

	public static final String PROP_REPO_DXP = "liferay/dxp";

	public static final String PROP_REPO_NAME = "repo.name";

	public static final String PROP_REPO_PORTAL = "liferay/portal";

	public static final String PROP_STATE = "state";

	public boolean canPublishModule(IServer server, IModule module);

	public void cleanDockerImage(IProgressMonitor monitor);

	public void createDockerContainer(IProgressMonitor monitor);

	public void dockerDeploy(IProject project, IProgressMonitor monitor);

	public void removeDockerContainer(IProgressMonitor monitor);

	public void startDockerContainer(IProgressMonitor monitor);

	public void stopDockerContainer(IProgressMonitor monitor);

}