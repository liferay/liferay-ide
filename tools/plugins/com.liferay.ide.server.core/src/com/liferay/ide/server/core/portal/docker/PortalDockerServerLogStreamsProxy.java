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

import org.eclipse.debug.core.ILaunch;

/**
 * @author Simon Jiang
 */
public class PortalDockerServerLogStreamsProxy extends PortalDockerServerStreamsProxy {

	public PortalDockerServerLogStreamsProxy(
		PortalDockerServer curPortalServer, PortalDockerServerBehavior curServerBehaviour, ILaunch curLaunch) {

		this(
			curPortalServer, curServerBehaviour, curLaunch, new PortalDockerServerOutputStreamMonitor(),
			new PortalDockerServerOutputStreamMonitor());
	}

	public PortalDockerServerLogStreamsProxy(
		PortalDockerServer portalServer, PortalDockerServerBehavior curServerBehaviour, ILaunch curLaunch,
		PortalDockerServerOutputStreamMonitor systemOut, PortalDockerServerOutputStreamMonitor systemErr) {

		_launch = null;

		if ((portalServer == null) || (curServerBehaviour == null)) {
			return;
		}

		_serverBehaviour = curServerBehaviour;

		_launch = curLaunch;

		try {
			if (systemOut != null) {
				sysOut = systemOut;
			}
			else {
				sysOut = new PortalDockerServerOutputStreamMonitor();
			}

			if (systemErr != null) {
				sysErr = systemErr;
			}
			else {
				sysErr = new PortalDockerServerOutputStreamMonitor();
			}

			startMonitoring(portalServer);
		}
		catch (Exception e) {
		}
	}

	public ILaunch getLaunch() {
		return _launch;
	}

	public PortalDockerServerBehavior getServerBehaviour() {
		return _serverBehaviour;
	}

	private ILaunch _launch;
	private PortalDockerServerBehavior _serverBehaviour;

}