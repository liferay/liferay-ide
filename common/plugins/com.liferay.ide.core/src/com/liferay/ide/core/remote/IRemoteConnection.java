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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.core.remote;

/**
 * @author Gregory Amerson
 */
public interface IRemoteConnection
{

    String _API = "/api/jsonws"; //$NON-NLS-1$

    String getHost();

    int getHttpPort();

    String getPassword();

    String getUsername();

    void setHost( String host );

    void setHttpPort( String httpPort );

    void setPassword( String password );

    void setUsername( String username );
}
