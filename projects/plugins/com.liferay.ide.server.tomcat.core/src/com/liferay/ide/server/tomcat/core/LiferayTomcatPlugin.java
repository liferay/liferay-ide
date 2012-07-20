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

import com.liferay.ide.core.CorePlugin;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 * 
 * @author Greg Amerson
 */
public class LiferayTomcatPlugin extends CorePlugin
{

    // The shared instance
    private static LiferayTomcatPlugin plugin;

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.server.tomcat.core";

    public static final String PREFERENCES_ADDED_EXT_PLUGIN_TOGGLE_KEY = "ADDED_EXT_PLUGIN_TOGGLE_KEY";

    public static final String PREFERENCES_ADDED_EXT_PLUGIN_WITHOUT_ZIP_TOGGLE_KEY =
        "ADDED_EXT_PLUGIN_WITHOUT_ZIP_TOGGLE_KEY";

    public static final String PREFERENCES_EE_UPGRADE_MSG_TOGGLE_KEY = "EE_UPGRADE_MSG_TOGGLE_KEY";

    public static final String PREFERENCES_REMOVE_EXT_PLUGIN_TOGGLE_KEY = "REMOVE_EXT_PLUGIN_TOGGLE_KEY";

    public static IStatus createErrorStatus( String msg )
    {
        return createErrorStatus( PLUGIN_ID, msg );
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static LiferayTomcatPlugin getDefault()
    {
        return plugin;
    }

    @SuppressWarnings( "deprecation" )
    public static IPersistentPreferenceStore getPreferenceStore()
    {
        return new ScopedPreferenceStore( new InstanceScope(), PLUGIN_ID );
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( PLUGIN_ID, msg, e ) );
    }

    public static IStatus warning( String msg )
    {
        return createWarningStatus( msg, PLUGIN_ID );
    }

    public static IStatus warning( String msg, Exception e )
    {
        return createWarningStatus( msg, PLUGIN_ID, e );
    }

    /**
     * The constructor
     */
    public LiferayTomcatPlugin()
    {
    }

    private void cleanupVersionFiles()
    {
        File versionProps = LiferayTomcatPlugin.getDefault().getStateLocation().append( "version.properties" ).toFile();

        if( versionProps.exists() )
        {
            if( !versionProps.delete() )
            {
                versionProps.deleteOnExit();
            }
        }

        File serverInfos =
            LiferayTomcatPlugin.getDefault().getStateLocation().append( "serverInfos.properties" ).toFile();

        if( serverInfos.exists() )
        {
            if( !serverInfos.delete() )
            {
                serverInfos.deleteOnExit();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    public void start( BundleContext context ) throws Exception
    {

        super.start( context );

        plugin = this;

        // portalSourcePartListener = new PortalSourcePartListener();
        //
        // PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
        //
        // public void run() {
        // IWorkbenchWindow workbenchWindow =
        // PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        //
        // if (workbenchWindow != null) {
        // workbenchWindow.getPartService().addPartListener((IPartListener2)
        // portalSourcePartListener);
        // }
        // }
        // });
        //
        // PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
        //
        // public void windowActivated(IWorkbenchWindow window) {
        // }
        //
        // public void windowClosed(IWorkbenchWindow window) {
        // }
        //
        // public void windowDeactivated(IWorkbenchWindow window) {
        // }
        //
        // public void windowOpened(IWorkbenchWindow window) {
        // window.getPartService().addPartListener((IPartListener2)
        // portalSourcePartListener);
        // }
        // });

        cleanupVersionFiles();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop( BundleContext context ) throws Exception
    {
        cleanupVersionFiles();

        plugin = null;

        super.stop( context );
    }
}
