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

import com.liferay.ide.server.core.ILiferayServerWorkingCopy;

import org.eclipse.jst.server.tomcat.core.internal.ITomcatServer;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public interface ILiferayTomcatServer extends ILiferayServerWorkingCopy, ITomcatServer {

	public static final String PROPERTY_AUTO_DEPLOY_DIR = "autoDeployDir";

	public static final String PROPERTY_AUTO_DEPLOY_INTERVAL = "autoDeployInterval";

	public static final String PROPERTY_EXTERNAL_PROPERTIES = "externalProperties";

	public static final String PROPERTY_MEMORY_ARGS = "memoryArgs";

	public static final String PROPERTY_SERVER_MODE = "serverMode";

	public static final String PROPERTY_USE_DEFAULT_PORTAL_SERVER_SETTINGS = "useDefaultPortalServerSettings";

	public static final String PROPERTY_USER_TIMEZONE = "userTimezone";

	public String getAutoDeployDirectory();

	public String getAutoDeployInterval();

	public String getExternalProperties();

	public String getMemoryArgs();

	public int getServerMode();

	public boolean getUseDefaultPortalServerSettings();

	public String getUserTimezone();

}