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

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IStreamMonitor;

/**
 * @author Simon Jiang
 */
public class LiferayServerLogFileStreamsProxy extends LiferayServerFileStreamsProxy {

	public LiferayServerLogFileStreamsProxy(PortalRuntime runtime, ILaunch curLaunch) {
		this(runtime, curLaunch, new LiferayServerOutputStreamMonitor(), new LiferayServerOutputStreamMonitor());
	}

	public LiferayServerLogFileStreamsProxy(
		PortalRuntime runtime, ILaunch curLaunch, LiferayServerOutputStreamMonitor systemOut,
		LiferayServerOutputStreamMonitor systemErr) {

		_launch = null;

		if (runtime == null) {
			return;
		}

		PortalBundle portalBundle = runtime.getPortalBundle();

		_launch = curLaunch;

		try {
			IPath defaultLogPath = portalBundle.getLogPath();

			sysoutFile = defaultLogPath.toOSString();

			if (systemOut != null) {
				sysOut = systemOut;
			}
			else {
				sysOut = new LiferayServerOutputStreamMonitor();
			}

			startMonitoring();
		}
		catch (Exception e) {
		}
	}

	public IStreamMonitor getErrorStreamMonitor() {
		return null;
	}

	public ILaunch getLaunch() {
		return _launch;
	}

	private ILaunch _launch;

}