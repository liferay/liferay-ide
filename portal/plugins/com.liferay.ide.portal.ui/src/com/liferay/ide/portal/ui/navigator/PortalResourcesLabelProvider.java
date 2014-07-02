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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/
package com.liferay.ide.portal.ui.navigator;

import com.liferay.ide.portal.ui.PortalUI;
import com.liferay.ide.ui.navigator.AbstractLabelProvider;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;


/**
 * @author Gregory Amerson
 */
public class PortalResourcesLabelProvider extends AbstractLabelProvider
{

    public PortalResourcesLabelProvider()
    {
        super();
    }
    
    private final static String PORTAL = "PORTAL"; //$NON-NLS-1$

    @Override
    protected void initalizeImageRegistry( ImageRegistry registry )
    {
        registry.put(
            PORTAL,
            PortalUI.imageDescriptorFromPlugin( PortalUI.PLUGIN_ID, "icons/e16/portal.png" ) ); //$NON-NLS-1$
    }
    
    @Override
    public Image getImage( Object element )
    {
        if( element instanceof PortalResourcesRootNode )
        {
            return getImageRegistry().get( PORTAL );
        }

        return null;
    }

    @Override
    public String getText( Object element )
    {
        if( element instanceof PortalResourcesRootNode )
        {
            return Msgs.liferayPortalResources;
        }

        return null;
    }

    private static class Msgs extends NLS
    {
        public static String liferayPortalResources;

        static
        {
            initializeMessages( PortalResourcesLabelProvider.class.getName(), Msgs.class );
        }
    }
}
