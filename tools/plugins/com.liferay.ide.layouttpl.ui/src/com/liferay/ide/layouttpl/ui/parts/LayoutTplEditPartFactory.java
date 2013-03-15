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

import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * @author Gregory Amerson
 */
public class LayoutTplEditPartFactory implements EditPartFactory
{

    public EditPart createEditPart( EditPart context, Object modelElement )
    {
        // get EditPart for model element
        EditPart part = getPartForElement( modelElement );

        // store model element in EditPart
        part.setModel( modelElement );

        return part;
    }

    protected EditPart getPartForElement( Object modelElement )
    {
        if( modelElement instanceof LayoutTplDiagram )
        {
            return new LayoutTplDiagramEditPart();
        }

        if( modelElement instanceof PortletLayout )
        {
            return new PortletLayoutEditPart();
        }

        if( modelElement instanceof PortletColumn )
        {
            return new PortletColumnEditPart();
        }

        throw new RuntimeException( "Can't create part for model element: " + //$NON-NLS-1$
            ( ( modelElement != null ) ? modelElement.getClass().getName() : "null" ) ); //$NON-NLS-1$
    }

}
