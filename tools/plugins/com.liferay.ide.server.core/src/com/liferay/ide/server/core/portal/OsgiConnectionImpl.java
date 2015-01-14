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
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public class OsgiConnectionImpl implements OsgiConnection
{

    public static OsgiBundle newFromData( CompositeData cd )
    {
        final String id = cd.get( "Identifier" ).toString();
        final String bsn = cd.get( "SymbolicName" ).toString();
        final String state = cd.get( "State" ).toString();
        final Version version = new Version( cd.get( "Version" ).toString() );

        return new OsgiBundle( id, bsn, state, version );
    }

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

    public IStatus deployBundle( String location, File bundle )
    {
        IStatus retval = null;

        try
        {
            final ObjectName framework =
                this.mbsc.queryNames( new ObjectName( "osgi.core:type=framework,*" ), null ).iterator().next();

            Object installed =
                this.mbsc.invoke( framework, "installBundleFromURL", new Object[] { location,
                    bundle.toURI().toURL().toExternalForm() }, new String[] { String.class.getName(),
                    String.class.getName() } );

            if( installed instanceof Long )
            {
                long bundleId = (Long) installed;

                if( bundleId > 0 )
                {
                    // may have already been installed so lets run update TODO only do this if needed

                    this.mbsc.invoke(
                        framework, "updateBundleFromURL", new Object[] { bundleId,
                            bundle.toURI().toURL().toExternalForm() },
                        new String[] { "long", String.class.getName() } );

                    this.mbsc.invoke(
                        framework, "refreshBundle", new Object[] { bundleId }, new String[] { "long" } );

                    this.mbsc.invoke(
                        framework, "startBundle", new Object[] { bundleId }, new String[] { "long" } );
                }

                retval = new Status( IStatus.OK, LiferayServerCore.PLUGIN_ID, (int) bundleId, null, null );
            }
        }
        catch( Exception e )
        {
            retval = LiferayServerCore.error( "Error installing bundle " + bundle.getName(), e );
        }

        return retval;
    }

    // TODO investigate why this is so slow
    public OsgiBundle[] getBundles()
    {
        final List<OsgiBundle> retval = new ArrayList<OsgiBundle>();

        try
        {
            final Set<ObjectName> names =
                this.mbsc.queryNames( new ObjectName( "osgi.core:type=bundleState,*" ), null );
            final TabularData data =
                (TabularData) this.mbsc.invoke( names.iterator().next(), "listBundles", null, null );

            for( Object value : data.values() )
            {
                final CompositeData cd = (CompositeData) value;

                try
                {
                    retval.add( newFromData( cd ) );
                }
                catch( Exception e )
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

    public IStatus uninstallBundle( String symbolicName )
    {
        IStatus retval = null;

        for( OsgiBundle bundle : getBundles() )
        {
            if( bundle.getSymbolicName().equals( symbolicName ) )
            {
                try
                {
                    final ObjectName framework =
                        this.mbsc.queryNames( new ObjectName( "osgi.core:type=framework,*" ), null ).iterator().next();

                    this.mbsc.invoke(
                        framework, "uninstallBundle", new Object[] { Long.parseLong( bundle.getId() ) },
                        new String[] { "long" } );

                    retval = Status.OK_STATUS;
                }
                catch( Exception e )
                {
                    retval = LiferayServerCore.error( "Error uninstall bundle " + bundle.getSymbolicName(), e );
                }

                break;
            }
        }

        return retval;
    }

}
