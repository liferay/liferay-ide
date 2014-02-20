/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.mobile.sdk.core;


import com.liferay.ide.core.util.CoreUtil;
import com.liferay.mobile.android.service.BaseService;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;
import com.liferay.mobile.android.v62.portal.PortalService;
import com.liferay.mobile.android.v62.portlet.PortletService;
import com.liferay.mobile.sdk.MobileSDKBuilder;
import com.liferay.mobile.sdk.http.Action;
import com.liferay.mobile.sdk.http.Discovery;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Gregory Amerson
 */
public class MobileSDKCore extends Plugin
{
    // The shared instance
    private static MobileSDKCore plugin;

    // The plug-in ID
    public static final String PLUGIN_ID = "com.liferay.mobile.sdk.core"; //$NON-NLS-1$

    public static Object checkServerStatus(final String server, final String username, final String password )
    {
        Object retval = null;

        try
        {
            final PortalService portalService = getService( server, username, password, PortalService.class );
            retval = portalService.getBuildNumber();
        }
        catch( Exception e )
        {
            retval = e.getMessage();
        }

        return retval;
    }

    private static boolean containsAPI( List<MobileAPI> apis, String servletContextName )
    {
        for( MobileAPI api : apis )
        {
            if( api.name.equals( servletContextName ) )
            {
                return true;
            }
        }

        return false;
    }

    public static IStatus createErrorStatus( String msg, Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
    }

    private static String[] discoverAPIs( String server, String servletContextName ) throws Exception
    {
        final Discovery discovery = MobileSDKBuilder.discover( server, servletContextName, null );

        final List<String> entities = new ArrayList<String>();

        for( Action action : discovery.getActions() )
        {
            final IPath path = new Path( action.getPath() );
            final String entity = path.segment( 0 );

            if( ! entities.contains( entity ) )
            {
                entities.add( entity );
            }
        }

        return entities.toArray( new String[0] );
    }

    public static MobileAPI[] discoverAPIs( final String server, final String username, final String password )
    {
        final List<MobileAPI> apis = new ArrayList<MobileAPI>();

        apis.add( new MobileAPI( "Liferay core" ) );

        try
        {
            final PortletService portletService = getService( server, username, password, PortletService.class );

            final JSONArray warPortlets = portletService.getWarPortlets();

            for( int i = 0; i < warPortlets.length(); i++ )
            {
                final Object warPortlet = warPortlets.get( i );

                if( warPortlet instanceof JSONObject )
                {
                    final String servletContextName = ( (JSONObject) warPortlet ).getString( "servlet_context_name" );

                    if( ! containsAPI( apis, servletContextName ) )
                    {
                        try
                        {
                            final String[] contextAPIs = discoverAPIs( server, servletContextName );

                            if( ! CoreUtil.isNullOrEmpty( contextAPIs ) )
                            {
                                apis.add( new MobileAPI( servletContextName, contextAPIs ) );
                            }
                        }
                        catch( Exception e )
                        {
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
        }

        return apis.toArray( new MobileAPI[0] );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static MobileSDKCore getDefault()
    {
        return plugin;
    }

    private static <T extends BaseService> T getService(
        String server, String username, String password, Class<T> serviceClass ) throws Exception
    {
        return serviceClass.getConstructor( Session.class ).newInstance( new SessionImpl( server, username, password ) );
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( msg, e ) );
    }

    /**
     * The constructor
     */
    public MobileSDKCore()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );
    }

    public static class MobileAPI
    {
        public String[] apis = new String[0];
        public String name;

        public MobileAPI( String name )
        {
            this.name = name;
        }

        public MobileAPI( String name, String[] apis )
        {
            this.name = name;
            this.apis = apis;
        }

    }
}
