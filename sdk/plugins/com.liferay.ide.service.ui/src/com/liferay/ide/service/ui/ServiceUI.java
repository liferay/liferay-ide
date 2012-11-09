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

package com.liferay.ide.service.ui;

import com.liferay.ide.service.ui.template.ServiceBuilderTemplateContextTypeIds;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ServiceUI extends AbstractUIPlugin
{

    // The shared instance
    private static ServiceUI plugin;

    // The plug-in ID
    public static final String PLUGIN_ID = "com.liferay.ide.service.ui"; //$NON-NLS-1$

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ServiceUI getDefault()
    {
        return plugin;
    }

    public static void logError( Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e ) );
    }

    private ContributionContextTypeRegistry fContextTypeRegistry;

    private ContributionTemplateStore fTemplateStore;

    /**
     * The constructor
     */
    public ServiceUI()
    {
    }

    public ContextTypeRegistry getTemplateContextRegistry()
    {
        if( fContextTypeRegistry == null )
        {
            ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();

            registry.addContextType( ServiceBuilderTemplateContextTypeIds.NEW );

            fContextTypeRegistry = registry;
        }

        return fContextTypeRegistry;
    }

    public TemplateStore getTemplateStore()
    {
        if( fTemplateStore == null )
        {
            fTemplateStore =
                new ContributionTemplateStore(
                    getTemplateContextRegistry(), getPreferenceStore(), "com.liferay.ide.portlet.ui.custom_templates" );

            try
            {
                fTemplateStore.load();
            }
            catch( IOException e )
            {
                logError( e );
            }
        }

        return fTemplateStore;
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
