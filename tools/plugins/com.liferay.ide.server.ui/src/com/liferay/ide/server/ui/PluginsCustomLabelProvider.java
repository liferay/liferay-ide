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

package com.liferay.ide.server.ui;

import com.liferay.ide.project.core.ISDKTemplate;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class PluginsCustomLabelProvider extends LabelProvider
{

    public PluginsCustomLabelProvider()
    {
        super();
    }

    @Override
    public Image getImage( Object element )
    {
        if( element instanceof PluginsContent )
        {
            return LiferayServerUIPlugin.imageDescriptorFromPlugin(
                LiferayServerUIPlugin.PLUGIN_ID, "/icons/e16/plugin.png" ).createImage(); //$NON-NLS-1$
        }
        else if( element instanceof ModuleServer )
        {
            try
            {
                ModuleServer server = (ModuleServer) element;
                IProject project = server.getModule()[0].getProject();
                IFacetedProject facetedProject = ProjectUtil.getFacetedProject( project );

                if( facetedProject != null )
                {
                    IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet( facetedProject );
                    ISDKTemplate sdkTemplate = LiferayProjectCore.getSDKTemplate( liferayFacet );

                    return LiferayServerUIPlugin.imageDescriptorFromPlugin(
                        LiferayServerUIPlugin.PLUGIN_ID, "/icons/e16/" + sdkTemplate.getShortName() + ".png" ).createImage(); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    String type = ProjectUtil.getLiferayPluginType( project.getLocation().toOSString() );

                    return LiferayServerUIPlugin.imageDescriptorFromPlugin(
                        LiferayServerUIPlugin.PLUGIN_ID, "/icons/e16/" + type + ".png" ).createImage(); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            catch( Exception ex )
            {
                // best effort no need to log error
            }
        }

        return null;
    }

    @Override
    public String getText( Object element )
    {
        if( element instanceof PluginsContent )
        {
            return Msgs.liferayPlugins;
        }
        else
        {
            return null;
        }
    }

    private static class Msgs extends NLS
    {
        public static String liferayPlugins;

        static
        {
            initializeMessages( PluginsCustomLabelProvider.class.getName(), Msgs.class );
        }
    }
}
