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

import com.liferay.ide.server.core.ILiferayServerWorkingCopy;

/**
 * @author Terry Jia
 */
public interface PortalServerWorkingCopy extends ILiferayServerWorkingCopy, PortalServer {

	public void setCustomLaunchSettings(boolean customLaunchSettings);

	public void setDeveloperMode(boolean developmentMode);

	public void setExternalProperties(String externalProperties);

	public void setGogoShellPort(String gogoShellPort);

	public void setMemoryArgs(String memoryArgs);

}