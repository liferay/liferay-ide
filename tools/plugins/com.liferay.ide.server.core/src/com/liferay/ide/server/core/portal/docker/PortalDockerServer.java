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

import com.liferay.ide.server.core.portal.PortalServerDelegate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Simon Jiang
 */
public class PortalDockerServer extends PortalServerDelegate implements IPortalDockerServer {

	public static final String DOCKER_CONTAINER_HEALTH_CHECK_URL = "docker-container-health-check-url";

	public static final String DOCKER_CONTAINER_ID = "docker-container-id";

	public static final String DOCKER_CONTAINER_IMAGE_ID = "docker-container-image-id";

	public static final String DOCKER_CONTAINER_NAME = "docker-container-name";

	public static final String ID = "com.liferay.ide.server.portal.docker";

	@Override
	public String getContainerId() {
		return getAttribute(DOCKER_CONTAINER_ID, (String)null);
	}

	@Override
	public String getContainerName() {
		return getAttribute(DOCKER_CONTAINER_NAME, (String)null);
	}

	@Override
	public String getHealthCheckUrl() {
		return getAttribute(DOCKER_CONTAINER_HEALTH_CHECK_URL, (String)null);
	}

	public void setContainerId(String containerId) {
		setAttribute(DOCKER_CONTAINER_ID, containerId);
	}

	public void setContainerName(String name) {
		setAttribute(DOCKER_CONTAINER_NAME, name);
	}

	@Override
	public void setDefaults(IProgressMonitor monitor) {
		ServerUtil.setServerDefaultName(getServerWorkingCopy());
	}

	public void setHealthCheckUrl(String healthCheckUrl) {
		setAttribute(DOCKER_CONTAINER_HEALTH_CHECK_URL, healthCheckUrl);
	}

}