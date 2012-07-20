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

package com.liferay.ide.sdk;

import com.liferay.ide.core.CorePlugin;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 * 
 * @author Greg Amerson
 */
public class SDKPlugin extends AbstractUIPlugin
{

    public static final String IMG_ICON_SDK_SMALL = "IMG_ICON_SDK_SMALL";

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.sdk";

    public static final String PREF_KEY_OVERWRITE_USER_BUILD_FILE = "OVERWRITE_USER_BUILD_FILE";

    // The shared instance
    private static SDKPlugin plugin;

    public static IStatus createErrorStatus( String msg )
    {
        return CorePlugin.createErrorStatus( PLUGIN_ID, msg );
    }

    public static IStatus createErrorStatus( Throwable t )
    {
        return CorePlugin.createErrorStatus( PLUGIN_ID, t );
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static SDKPlugin getDefault()
    {
        return plugin;
    }

    public static void logError( Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e ) );
    }

    protected Map<String, ImageDescriptor> imageDescriptors = new HashMap<String, ImageDescriptor>();

    /**
     * The constructor
     */
    public SDKPlugin()
    {
    }

    public Image getImage( String key )
    {
        return getImageRegistry().get( key );
    }

    public ImageDescriptor getImageDescriptor( String key )
    {
        // ensure that registry is created
        getImageRegistry();
        return imageDescriptors.get( key );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop( BundleContext context ) throws Exception
    {

        // delete tmp folder
        File createDir = getDefault().getStateLocation().append( "create" ).toFile();

        if( createDir.exists() )
        {
            FileUtil.deleteDir( createDir, true );
        }

        plugin = null;
        super.stop( context );
    }

    @Override
    protected void initializeImageRegistry( ImageRegistry reg )
    {
        registerImage( reg, IMG_ICON_SDK_SMALL, "/icons/e16/sdk.png" );
    }

    protected void registerImage( ImageRegistry registry, String key, String path )
    {
        try
        {
            ImageDescriptor id = ImageDescriptor.createFromURL( getBundle().getEntry( path ) );
            imageDescriptors.put( key, id );

            registry.put( key, id );
        }
        catch( Exception e )
        {
        }
    }

}
