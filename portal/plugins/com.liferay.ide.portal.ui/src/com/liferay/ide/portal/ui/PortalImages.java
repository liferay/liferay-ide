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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Gregory Amerson
 */
public class PortalImages
{
    //	public static final Image IMG_LOADING = createImage( "e16/waiting_16x16.gif" ); //$NON-NLS-1$

    public static final Image IMG_USER_TEMPLATE = createImage( "template_obj.gif" ); //$NON-NLS-1$

    // public static final Image IMG_WORKFLOW_DEFINITION = createImage( "e16/workflow_16x16.png" );

    // public static final Image IMG_WORKFLOW_DEFINITION_WIZBAN = createImage( "wizban/workflow_definition_16x16.png" );

    // public static final Image IMG_WORKFLOW_DEFINITIONS_FOLDER = createImage( "e16/definitions_16x16.png" );

    private static ImageDescriptor create( String key )
    {
        try
        {
            ImageDescriptor imageDescriptor = createDescriptor( key );
            ImageRegistry imageRegistry = getImageRegistry();

            if( imageRegistry != null )
            {
                imageRegistry.put( key, imageDescriptor );
            }

            return imageDescriptor;
        }
        catch( Exception ex )
        {
            PortalUI.logError( ex );
            return null;
        }
    }

    private static ImageDescriptor createDescriptor( String image )
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin( PortalUI.PLUGIN_ID, "icons/" + image ); //$NON-NLS-1$
    }

    private static Image createImage( String key )
    {
        create( key );
        ImageRegistry imageRegistry = getImageRegistry();

        return imageRegistry == null ? null : imageRegistry.get( key );
    }

    private static ImageRegistry getImageRegistry()
    {
        PortalUI plugin = PortalUI.getDefault();

        return plugin == null ? null : plugin.getImageRegistry();
    }

}
