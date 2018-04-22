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

package com.liferay.ide.server.remote;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public class RemoteUtil {

	public static String detectServerManagerContextPath(RemoteServer remoteServer, IProgressMonitor monitor) {
		return IRemoteServer.DEFAULT_SERVER_MANAGER_CONTEXT_PATH;
	}

	public static IRemoteServer getRemoteServer(IServer server) {
		if (server != null) {
			return (IRemoteServer)server.loadAdapter(IRemoteServer.class, null);
		}

		return null;
	}

}