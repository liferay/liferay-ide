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

	public static final String ID = "com.liferay.ide.server.portal.docker";

	@Override
	public String getContainerId() {
		return getAttribute(docker_container_id, (String)null);
	}

	@Override
	public String getContainerName() {
		return getAttribute(docker_container_name, (String)null);
	}

	@Override
	public String getHealthCheckUrl() {
		return getAttribute(docker_container_health_check_url, (String)null);
	}

	public void setContainerId(String containerId) {
		setAttribute(docker_container_id, containerId);
	}

	public void setContainerName(String name) {
		setAttribute(docker_container_name, name);
	}

	@Override
	public void setDefaults(IProgressMonitor monitor) {
		ServerUtil.setServerDefaultName(getServerWorkingCopy());
	}

	public void setHealthCheckUrl(String healthCheckUrl) {
		setAttribute(docker_container_health_check_url, healthCheckUrl);
	}

	public final String docker_container_health_check_url = "docker-container-health-check-url";
	public final String docker_container_id = "docker-container-id";
	public final String docker_container_images_id = "docker-container-image-id";
	public final String docker_container_name = "docker-container-name";

}