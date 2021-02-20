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

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.ILiferayServer;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public interface PortalServer extends ILiferayServer {

	public int getAutoPublishTime();

	public boolean getCustomLaunchSettings();

	public boolean getDeveloperMode();

	public String getExternalProperties();

	public String getGogoShellPort();

	public String[] getMemoryArgs();

	public String DEFAULT_HTTP_PORT = defaultPrefs.get("default.http.port", StringPool.EMPTY);

	public String ID = "com.liferay.ide.server.portal";

	public String PROPERTY_CUSTOM_LAUNCH_SETTINGS = "customLaunchSettings";

	public String PROPERTY_DEVELOPER_MODE = "developerMode";

	public String PROPERTY_EXTERNAL_PROPERTIES = "externalProperties";

	public String PROPERTY_GOGOSHELL_PORT = "gogoShellPort";

	public String PROPERTY_MEMORY_ARGS = "memoryArgs";

	public String PROPERTY_SERVER_MODE = "serverMode";

	public String START = "start";

	public String STOP = "stop";

}