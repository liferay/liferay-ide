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
 * Contributors:
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.ui.navigator;

import com.liferay.ide.portlet.ui.PortletUIPlugin;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class PortletResourcesLabelProvider extends LabelProvider
{

    private final static String PORTLETS = "PORTLETS";
    private final static String PORTLET = "PORTLET";
    private final static String MODULES = "MODULES";
    private final ImageRegistry imageRegistry;

    public PortletResourcesLabelProvider()
    {
        super();

        this.imageRegistry = new ImageRegistry();

        imageRegistry.put(
            PORTLETS,
            PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "icons/e16/portlets_16x16.png" ) );
        imageRegistry.put(
            PORTLET,
            PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "icons/e16/portlet_16x16.png" ) );
        imageRegistry.put(
            MODULES,
            PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "icons/e16/liferay_modules.png" ) );
    }

    @Override
    public void dispose()
    {
        this.imageRegistry.dispose();
    }

    @Override
    public Image getImage( Object element )
    {
        if( element instanceof PortletResourcesRootNode )
        {
            return this.imageRegistry.get( MODULES );
        }
        else if( element instanceof PortletsNode )
        {
            return this.imageRegistry.get( PORTLETS );
        }
        else if( element instanceof PortletNode )
        {
            return this.imageRegistry.get( PORTLET );
        }

        return null;
    }

    @Override
    public String getText( Object element )
    {
        if( element instanceof PortletResourcesRootNode )
        {
            return "Liferay Portlet Resources";
        }
        else if( element instanceof PortletsNode )
        {
            return "Portlets";
        }
        else if( element instanceof PortletNode )
        {
            PortletNode portletNode = (PortletNode) element;

            return portletNode.getName();
        }

        return null;
    }
}
