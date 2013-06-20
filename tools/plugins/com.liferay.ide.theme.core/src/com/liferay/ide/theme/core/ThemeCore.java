/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.theme.core;

import com.liferay.ide.core.util.FileUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class ThemeCore extends Plugin
{

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.theme.core"; //$NON-NLS-1$

    // The shared instance
    private static ThemeCore plugin;

    private static ThemeDiffResourceListener themeDiffResourceListener;

    public static IStatus createErrorStatus( Exception ex )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex );
    }

    public static IStatus createErrorStatus( String msg )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg );
    }

    public static IStatus createErrorStatus( String msg, Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
    }

    public static IStatus createWarningStatus( String msg )
    {
        return new Status( IStatus.WARNING, PLUGIN_ID, msg );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ThemeCore getDefault()
    {
        return plugin;
    }

    public static String getThemeProperty( String propertyName, IProject project )
    {
        String retval = null;

        try
        {
            Document buildXmlDoc = FileUtil.readXML( project.getFile( "build.xml" ).getContents(), null, null ); //$NON-NLS-1$

            NodeList properties = buildXmlDoc.getElementsByTagName( "property" ); //$NON-NLS-1$

            for( int i = 0; i < properties.getLength(); i++ )
            {
                final Node item = properties.item( i );
                Node name = item.getAttributes().getNamedItem( "name" ); //$NON-NLS-1$

                if( name != null && propertyName.equals( name.getNodeValue() ) )
                {
                    Node value = item.getAttributes().getNamedItem( "value" ); //$NON-NLS-1$

                    retval = value.getNodeValue();
                    break;
                }
            }
        }
        catch( CoreException e )
        {
            e.printStackTrace();
        }

        if( retval == null )
        {
            if( propertyName.equals( "theme.parent" ) ) //$NON-NLS-1$
            {
                retval = "_styled"; //$NON-NLS-1$
            }

            if( propertyName.equals( "theme.type" ) ) //$NON-NLS-1$
            {
                retval = "vm"; //$NON-NLS-1$
            }
        }

        return retval;
    }

    public static void logError( Exception ex )
    {
        getDefault().getLog().log( createErrorStatus( ex ) );
    }

    /**
     * The constructor
     */
    public ThemeCore()
    {
        // themeDiffResourceListener = new ThemeDiffResourceListener();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        plugin = this;

        // ResourcesPlugin.getWorkspace().addResourceChangeListener(
        // themeDiffResourceListener, IResourceChangeEvent.POST_CHANGE);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );

        if( themeDiffResourceListener != null )
        {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener( themeDiffResourceListener );
        }
    }

    public static void logError( String msg )
    {
        getDefault().getLog().log( createErrorStatus( msg ) );
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( msg, e ) );
    }
}
