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
package com.liferay.ide.server.core.portal;

import aQute.remote.util.JMXBundleDeployer;

import java.lang.reflect.Field;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;


/**
 * @author Gregory Amerson
 */
public class BundleDeployer extends JMXBundleDeployer
{

    public BundleDeployer( int jmxRemotePort )
    {
        super( jmxRemotePort );
    }

    public boolean ping()
    {
        try
        {
            Field connectionField = this.getClass().getField( "mBeanServerConnection" );
            connectionField.setAccessible( true );
            MBeanServerConnection connection = (MBeanServerConnection) connectionField.get( this );

            return connection != null &&
                connection.queryNames( new ObjectName( "osgi.core:type=bundleState,*" ), null ) != null;
        }
        catch( Exception e )
        {
            return false;
        }
    }

}
