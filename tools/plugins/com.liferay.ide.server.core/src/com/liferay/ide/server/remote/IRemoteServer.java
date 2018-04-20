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

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.ILiferayServer;

import org.eclipse.wst.server.core.model.IURLProvider;

/**
 * @author Greg Amerson
 */
public interface IRemoteServer extends ILiferayServer, IURLProvider {

	public boolean getAdjustDeploymentTimestamp();

	public String getHost();

	public String getHTTPPort();

	public String getId();

	public String getLiferayPortalContextPath();

	public String getServerManagerContextPath();

	public void setAdjustDeploymentTimestamp(boolean adjustDemploymentTimestamp);

	public String ATTR_ADJUST_DEPLOYMENT_TIMESTAMP = "adjust-deployment-timestamp";

	public String ATTR_HOSTNAME = "hostname";

	public String ATTR_LIFERAY_PORTAL_CONTEXT_PATH = "liferay-portal-context-path";

	public String ATTR_SERVER_MANAGER_CONTEXT_PATH = "server-manager-context-path";

	public boolean DEFAULT_ADJUST_DEPLOYMENT_TIMESTAMP = defaultPrefs.getBoolean("adjust.deployment.timestamp", true);

	public String DEFAULT_HTTP_PORT = defaultPrefs.get("default.http.port", StringPool.EMPTY);

	public String DEFAULT_LIFERAY_PORTAL_CONTEXT_PATH = defaultPrefs.get(
		"default.liferay.portal.context.path", StringPool.EMPTY);

	public String DEFAULT_SERVER_MANAGER_CONTEXT_PATH = defaultPrefs.get(
		"default.server.manager.context.path", StringPool.EMPTY);

}