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

package com.liferay.ide.layouttpl.ui.parts;

import com.liferay.ide.layouttpl.ui.policies.LayoutTplDiagramLayoutEditPolicy;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Color;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
public class LayoutTplDiagramEditPart extends PortletRowLayoutEditPart
{
    public static final int DIAGRAM_MARGIN = 10;
    public boolean visualEditorSupported;

    public LayoutTplDiagramEditPart()
    {
    }

    public LayoutTplDiagramEditPart( boolean supported )
    {
        this.visualEditorSupported = supported;
    }

    @Override
    protected void configurePanel( Panel panel )
    {
        super.configurePanel( panel );

        if( visualEditorSupported )
        {
            panel.setBackgroundColor( new Color( null, 10, 10, 10 ) );
        }
        else
        {
            panel.setBackgroundColor( new Color( null, 196, 196, 196 ) );
            panel.add( new Label( Msgs.layoutTplNotSupported ) );
        }
    }

    protected void createEditPolicies()
    {
        // disallows the removal of this edit part from its parent
        installEditPolicy( EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy() );

        // handles constraint changes (e.g. moving and/or resizing) of model
        // elements and creation of new model elements
        installEditPolicy( EditPolicy.LAYOUT_ROLE, new LayoutTplDiagramLayoutEditPolicy() );
    }

    @Override
    public int getMargin()
    {
        return DIAGRAM_MARGIN;
    }

    private static class Msgs extends NLS
    {
        public static String layoutTplNotSupported;

        static
        {
            initializeMessages( LayoutTplDiagramEditPart.class.getName(), Msgs.class );
        }
    }
}
