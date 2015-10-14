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

package com.liferay.ide.project.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Greg Amerson
 */
public class ProjectUI extends AbstractUIPlugin
{

    // Shared images
    public static final String WAR_IMAGE_ID = "war.image"; //$NON-NLS-1$
    public static final String CHECKED_IMAGE_ID = "checked.image"; //$NON-NLS-1$
    public static final String MIGRATION_TASKS_IMAGE_ID = "migration.tasks.image"; //$NON-NLS-1$
    public static final String UNCHECKED_IMAGE_ID = "unchecked.image"; //$NON-NLS-1$
    public static final String EXPANDALL_IMAGE_ID = "expandall.image"; //$NON-NLS-1$

    public static final String LAST_SDK_IMPORT_LOCATION_PREF = "last.sdk.import.location"; //$NON-NLS-1$

    // The shared instance
    private static ProjectUI plugin;

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.project.ui"; //$NON-NLS-1$

    public static IStatus createErrorStatus( String msg )
    {
        return createErrorStatus( msg, null );
    }

    public static IStatus createErrorStatus( String msg, Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
    }

    public static IStatus createInfoStatus( String msg )
    {
        return new Status( IStatus.INFO, PLUGIN_ID, msg, null );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ProjectUI getDefault()
    {
        return plugin;
    }

    public static void logError( Exception e )
    {
        getDefault().getLog().log( createErrorStatus( e.getMessage(), e ) );
    }

    // private static IConfigurationElement[] pluginWizardFragmentElements;

    // public static IPluginWizardFragment getPluginWizardFragment(String pluginFacetId) {
    // if (CoreUtil.isNullOrEmpty(pluginFacetId)) {
    // return null;
    // }
    //
    // IConfigurationElement[] fragmentElements = getPluginWizardFragmentsElements();
    //
    // for (IConfigurationElement fragmentElement : fragmentElements) {
    // if (pluginFacetId.equals(fragmentElement.getAttribute("facetId"))) {
    // try {
    // Object o = fragmentElement.createExecutableExtension("class");
    //
    // if (o instanceof IPluginWizardFragment) {
    // IPluginWizardFragment fragment = (IPluginWizardFragment) o;
    // fragment.setFragment(true);
    // return fragment;
    // }
    // }
    // catch (CoreException e) {
    // ProjectUIPlugin.logError("Could not load plugin wizard fragment for " + pluginFacetId, e);
    // }
    // }
    // }
    //
    // return null;
    // }

    // public static IConfigurationElement[] getPluginWizardFragmentsElements() {
    // if (pluginWizardFragmentElements == null) {
    // pluginWizardFragmentElements =
    // Platform.getExtensionRegistry().getConfigurationElementsFor(IPluginWizardFragment.ID);
    // }
    //
    // return pluginWizardFragmentElements;
    // }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( msg, e ) );
    }

    public static void logInfo( String msg )
    {
        getDefault().getLog().log( createInfoStatus( msg ) );
    }

    /**
     * The constructor
     */
    public ProjectUI()
    {
    }

    @Override
    protected void initializeImageRegistry( ImageRegistry registry )
    {
        Bundle bundle = Platform.getBundle( PLUGIN_ID );
        IPath path = new Path( "icons/e16/war.gif" ); //$NON-NLS-1$
        URL url = FileLocator.find( bundle, path, null );
        ImageDescriptor desc = ImageDescriptor.createFromURL( url );
        registry.put( WAR_IMAGE_ID, desc );

        IPath checked = new Path( "icons/e16/checked.png" ); //$NON-NLS-1$
        URL checkedurl = FileLocator.find( bundle, checked, null );
        ImageDescriptor checkeddesc = ImageDescriptor.createFromURL( checkedurl );
        registry.put( CHECKED_IMAGE_ID, checkeddesc );

        IPath unchecked = new Path( "icons/e16/unchecked.png" ); //$NON-NLS-1$
        URL uncheckedurl = FileLocator.find( bundle, unchecked, null );
        ImageDescriptor uncheckeddesc = ImageDescriptor.createFromURL( uncheckedurl );
        registry.put( UNCHECKED_IMAGE_ID, uncheckeddesc );

        IPath migrationtasks = new Path( "icons/e16/migration-tasks.png" ); //$NON-NLS-1$
        URL migrationtasksurl = FileLocator.find( bundle, migrationtasks, null );
        ImageDescriptor migrationtasksdesc = ImageDescriptor.createFromURL( migrationtasksurl );
        registry.put( MIGRATION_TASKS_IMAGE_ID, migrationtasksdesc );

        IPath expandall = new Path( "icons/e16/expandall.gif" ); //$NON-NLS-1$
        URL expandallurl = FileLocator.find( bundle, expandall, null );
        ImageDescriptor expandalldesc = ImageDescriptor.createFromURL( expandallurl );
        registry.put( EXPANDALL_IMAGE_ID, expandalldesc );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    @Override
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    @Override
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;

        super.stop( context );
    }

}
