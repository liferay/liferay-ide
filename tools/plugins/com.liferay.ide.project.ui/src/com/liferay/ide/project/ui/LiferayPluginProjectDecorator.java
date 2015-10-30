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

import com.liferay.ide.core.LiferayNature;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;

/**
 * @author Greg Amerson
 * @author Terry Jia
 */
public class LiferayPluginProjectDecorator extends LabelProvider implements ILightweightLabelDecorator
{

    private static final String EXT_FACET = "liferay.ext"; //$NON-NLS-1$

    private static final String HOOK_FACET = "liferay.hook"; //$NON-NLS-1$

    private static final String ICON_DIR = "icons/ovr"; //$NON-NLS-1$

    private static final String LAYOUTTPL_FACET = "liferay.layouttpl"; //$NON-NLS-1$

    private static ImageDescriptor LIFERAY;

    /* The constants are duplicated here to avoid plugin loading. */
    private static final String PORTLET_FACET = "liferay.portlet"; //$NON-NLS-1$

    private static final String THEME_FACET = "liferay.theme"; //$NON-NLS-1$

    private static final String WEB_FACET = "liferay.web"; //$NON-NLS-1$

    /**
     * This gets a .gif from the icons folder.
     */
    private static ImageDescriptor getImageDescriptor( String key )
    {
        ImageDescriptor imageDescriptor = null;
        if( key != null )
        {
            String gif = "/" + key + ".png"; //$NON-NLS-1$ //$NON-NLS-2$

            IPath path = new Path( ICON_DIR ).append( gif );

            URL gifImageURL = FileLocator.find( Platform.getBundle( ProjectUI.PLUGIN_ID ), path, null );

            if( gifImageURL != null )
            {
                imageDescriptor = ImageDescriptor.createFromURL( gifImageURL );
            }
        }

        return imageDescriptor;
    }

    private static ImageDescriptor getLiferay()
    {
        if( LIFERAY == null )
        {
            LIFERAY = getImageDescriptor( "liferay_decoration" );
        }

        return LIFERAY;
    }

    public void decorate( Object element, IDecoration decoration )
    {
        if( element instanceof IProject )
        {
            IProject project = (IProject) element;

            ImageDescriptor overlay = null;

            if( hasFacet( project, PORTLET_FACET ) )
            {
                overlay = getLiferay();
            }
            else if( hasFacet( project, HOOK_FACET ) )
            {
                overlay = getLiferay();
            }
            else if( hasFacet( project, EXT_FACET ) )
            {
                overlay = getLiferay();
            }
            else if( hasFacet( project, LAYOUTTPL_FACET ) )
            {
                overlay = getLiferay();
            }
            else if( hasFacet( project, THEME_FACET ) )
            {
                overlay = getLiferay();
            }
            else if( hasFacet( project, WEB_FACET ) )
            {
                overlay = getLiferay();
            }
            else if ( hasNature( project, LiferayNature.NATURE_ID ) )
            {
                overlay = getLiferay();
            }

            if( overlay != null )
            {
                // next two lines dangerous!
                // DecorationContext ctx = (DecorationContext) decoration.getDecorationContext();
                // ctx.putProperty( IDecoration.ENABLE_REPLACE, true );

                decoration.addOverlay( overlay, IDecoration.TOP_RIGHT );
            }
        }
    }

    private boolean hasNature( IProject project, String id )
    {
        try
        {
            return project.hasNature( id );
        }
        catch( CoreException e )
        {
            return false;
        }
    }

    private boolean hasFacet( IProject project, String facet )
    {
        try
        {
            return FacetedProjectFramework.hasProjectFacet( project, facet );
        }
        catch( CoreException e )
        {
            ProjectUI.logError( e );

            return false;
        }
    }
}
