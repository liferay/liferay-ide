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

package com.liferay.ide.portal.ui;


import com.liferay.ide.portal.ui.templates.StructuresTemplateContext;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class PortalUI extends AbstractUIPlugin
{

    // The shared instance
    private static PortalUI plugin;

    // The plug-in ID
    public static final String PLUGIN_ID = "com.liferay.ide.portal.ui"; //$NON-NLS-1$

    private static final String TEMPLATES_KEY = PLUGIN_ID + ".templates"; //$NON-NLS-1$

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static PortalUI getDefault()
    {
        return plugin;
    }

    public static IPreferenceStore getPrefStore()
    {
        return getDefault().getPreferenceStore();
    }

    public static void logError( Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e ) );
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, msg, e ) );
    }

    public static IStatus logInfo( String msg, IStatus status )
    {
        return new Status( IStatus.INFO, PLUGIN_ID, msg, status.getException());
    }

    private ContributionContextTypeRegistry contextTypeRegistry;

    private ContributionTemplateStore templateStore;

    /**
     * The constructor
     */
    public PortalUI()
    {
    }

    public ContextTypeRegistry getTemplateContextRegistry()
    {
        if( contextTypeRegistry == null )
        {
            ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();

            for( StructuresTemplateContext contextType : StructuresTemplateContext.values() )
            {
                registry.addContextType( contextType.getContextTypeId() );
            }

            contextTypeRegistry = registry;
        }

        return contextTypeRegistry;
    }

    public TemplateStore getTemplateStore()
    {
        if( templateStore == null )
        {
            templateStore =
                new ContributionTemplateStore( getTemplateContextRegistry(), getPreferenceStore(), TEMPLATES_KEY );

            try
            {
                templateStore.load();
            }
            catch( IOException ex )
            {
                logError( "Unable to load structures templates", ex ); //$NON-NLS-1$
            }
        }

        return templateStore;
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

}
