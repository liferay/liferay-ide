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

import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * @author Gregory Amerson
 */
public class OsgiConnectionImpl implements OsgiConnection
{
    private final MBeanServerConnection mbsc;

    public OsgiConnectionImpl( int port )
    {
        try
        {
            final JMXServiceURL url = new JMXServiceURL( "service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi" );
            final JMXConnector jmxc = JMXConnectorFactory.connect( url, null );
            mbsc = jmxc.getMBeanServerConnection();
        }
        catch( Exception e )
        {
            throw new IllegalArgumentException( "Unable to connect to jmx server at port " + port );
        }
    }

    public OsgiBundle[] getBundles()
    {
        final List<OsgiBundle> retval = new ArrayList<OsgiBundle>();

        try
        {
            final Set<ObjectName> names = this.mbsc.queryNames( new ObjectName( "osgi.core:type=bundleState,*" ), null );
            final TabularData data =
                (TabularData) this.mbsc.invoke( names.iterator().next(), "listBundles", null, null );

            for( Object value : data.values() )
            {
                final CompositeData cd = (CompositeData) value;

                try
                {
                    retval.add( OsgiBundle.newFromData( cd ) );
                }
                catch( Exception e)
                {
                    LiferayServerCore.logError( "Could not get bundle data " + cd );
                }
            }
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "Unable to get bundles", e );
        }

        return retval.toArray( new OsgiBundle[0] );
    }

    public boolean ping()
    {
        try
        {
            return this.mbsc != null &&
                this.mbsc.queryNames( new ObjectName( "osgi.core:type=bundleState,*" ), null ) != null;
        }
        catch( Exception e )
        {
            return false;
        }
    }

    public IStatus instalBundle( String location, File bundle )
    {
        IStatus retval = Status.OK_STATUS;

        try
        {
            final ObjectName objectName =
                this.mbsc.queryNames( new ObjectName( "osgi.core:type=framework,*" ), null ).iterator().next();

            final Object[] params = new Object[] { location, bundle.toURI().toURL().toExternalForm() };
            final String[] signature = new String[] { String.class.getName(), String.class.getName() };

            Object installed = this.mbsc.invoke( objectName, "installBundleFromURL", params, signature );

            if( installed instanceof Long )
            {
                retval = Status.OK_STATUS;
            }
        }
        catch( Exception e )
        {
            retval = LiferayServerCore.error( "Error installing bundle " + bundle.getName(), e );
        }

        return retval;
    }

}
