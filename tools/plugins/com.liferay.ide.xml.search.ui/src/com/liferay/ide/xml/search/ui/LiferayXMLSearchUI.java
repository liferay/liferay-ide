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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.xml.search.ui.editor.ServiceXmlContextType;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class LiferayXMLSearchUI extends AbstractUIPlugin
{

    // The shared instance
    private static LiferayXMLSearchUI plugin;

    private ContextTypeRegistry contextTypeRegistry;
    private TemplateStore templateStore;

    public static String PORTLET_IMG = "portlet";

    // The plug-in ID
    public static final String PLUGIN_ID = "com.liferay.ide.xml.search.ui";

    public static final String SERVICE_XML_TEMPLATES_KEY = PLUGIN_ID + ".service_xml_templates";

    public static IStatus createErrorStatus( Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e );
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
    public static LiferayXMLSearchUI getDefault()
    {
        return plugin;
    }

    // add context type for templates of service.xml
    public ContextTypeRegistry getContextTypeRegistry()
    {
        if( contextTypeRegistry == null )
        {
            ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
            registry.addContextType( ServiceXmlContextType.ID_SERVICE_XML_TAG );

            contextTypeRegistry = registry;
        }

        return contextTypeRegistry;
    }

    public TemplateStore getServiceXmlTemplateStore()
    {
        if( templateStore == null )
        {
            templateStore =
                new ContributionTemplateStore(
                    getContextTypeRegistry(), getPreferenceStore(), SERVICE_XML_TEMPLATES_KEY );

            try
            {
                templateStore.load();
            }
            catch( IOException e )
            {
                logError( "Error loading template store.", e );
            }
        }

        return templateStore;
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( msg, e ) );
    }

    public static void logError( Throwable t )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, t.getMessage(), t ) );
    }

    /**
     * The constructor
     */
    public LiferayXMLSearchUI()
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

        final URL baseIconsURL = getBundle().getEntry( "icons/" );
        final ImageDescriptor portletImage = ImageDescriptor.createFromURL( new URL( baseIconsURL, "portlet.png" ) );

        getImageRegistry().put( PORTLET_IMG, portletImage );
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
