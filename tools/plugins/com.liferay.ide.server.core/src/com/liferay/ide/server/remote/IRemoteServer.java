/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.server.remote;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.ILiferayServer;

import org.eclipse.wst.server.core.model.IURLProvider;

/**
 * @author Greg Amerson
 */
public interface IRemoteServer extends ILiferayServer, IURLProvider
{

    String ATTR_ADJUST_DEPLOYMENT_TIMESTAMP = "adjust-deployment-timestamp"; //$NON-NLS-1$

    String ATTR_HOSTNAME = "hostname"; //$NON-NLS-1$

    String ATTR_LIFERAY_PORTAL_CONTEXT_PATH = "liferay-portal-context-path"; //$NON-NLS-1$

    String ATTR_SERVER_MANAGER_CONTEXT_PATH = "server-manager-context-path"; //$NON-NLS-1$

    boolean DEFAULT_ADJUST_DEPLOYMENT_TIMESTAMP = defaultPrefs.getBoolean( "adjust.deployment.timestamp", true ); //$NON-NLS-1$

    String DEFAULT_LIFERAY_PORTAL_CONTEXT_PATH = defaultPrefs.get( "default.liferay.portal.context.path", StringPool.EMPTY ); //$NON-NLS-1$

    String DEFAULT_SERVER_MANAGER_CONTEXT_PATH = defaultPrefs.get( "default.server.manager.context.path", StringPool.EMPTY ); //$NON-NLS-1$

    boolean getAdjustDeploymentTimestamp();

    String getHost();

    int getHttpPort();

    String getId();

    String getLiferayPortalContextPath();

    String getServerManagerContextPath();

    void setAdjustDeploymentTimestamp( boolean adjustDemploymentTimestamp );

}
