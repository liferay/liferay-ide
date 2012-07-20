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

package com.liferay.ide.server.ui;

import com.liferay.ide.project.core.IProjectDefinition;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.LabelProvider;
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
                LiferayServerUIPlugin.PLUGIN_ID, "/icons/e16/plugin.png" ).createImage();
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
                    IProjectDefinition projectDef = ProjectCorePlugin.getProjectDefinition( liferayFacet );
                    return LiferayServerUIPlugin.imageDescriptorFromPlugin(
                        LiferayServerUIPlugin.PLUGIN_ID, "/icons/e16/" + projectDef.getShortName() + ".png" ).createImage();
                }
                else
                {
                    String type = ProjectUtil.getLiferayPluginType( project.getLocation().toOSString() );
                    return LiferayServerUIPlugin.imageDescriptorFromPlugin(
                        LiferayServerUIPlugin.PLUGIN_ID, "/icons/e16/" + type + ".png" ).createImage();
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
            return "Liferay Plugins";
        }
        else
        {
            return null;
        }
    }

}
