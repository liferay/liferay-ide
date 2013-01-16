/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.server.core.ILiferayServer;

import org.eclipse.jst.server.tomcat.core.internal.ITomcatServer;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public interface ILiferayTomcatServer extends ILiferayServer, ITomcatServer
{

    /**
     * Property which specifies the directory where liferay scans for autodeployment
     */
    String PROPERTY_AUTO_DEPLOY_DIR = "autoDeployDir"; //$NON-NLS-1$

    String PROPERTY_AUTO_DEPLOY_INTERVAL = "autoDeployInterval"; //$NON-NLS-1$

    String PROPERTY_EXTERNAL_PROPERTIES = "externalProperties"; //$NON-NLS-1$

    String PROPERTY_MEMORY_ARGS = "memoryArgs"; //$NON-NLS-1$

    String PROPERTY_USER_TIMEZONE = "userTimezone"; //$NON-NLS-1$

    String getAutoDeployDirectory();

    String getAutoDeployInterval();

    String getExternalProperties();

    String getMemoryArgs();

    String getUserTimezone();

}
