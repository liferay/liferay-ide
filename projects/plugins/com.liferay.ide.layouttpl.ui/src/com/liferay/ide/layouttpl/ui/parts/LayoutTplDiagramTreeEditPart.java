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

package com.liferay.ide.layouttpl.ui.parts;

import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.layouttpl.ui.model.ModelElement;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.swt.graphics.Image;

/**
 * @author Gregory Amerson
 */
public class LayoutTplDiagramTreeEditPart extends BaseTreeEditPart
{

    public LayoutTplDiagramTreeEditPart( LayoutTplDiagram model )
    {
        super( model );
    }

    protected void createEditPolicies()
    {
        // If this editpart is the root content of the viewer, then disallow
        // removal
        if( getParent() instanceof RootEditPart )
        {
            installEditPolicy( EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy() );
        }
    }

    private LayoutTplDiagram getCastedModel()
    {
        return (LayoutTplDiagram) getModel();
    }

    protected List<ModelElement> getModelChildren()
    {
        return getCastedModel().getRows();
    }

    public void propertyChange( PropertyChangeEvent evt )
    {
        String prop = evt.getPropertyName();
        if( LayoutTplDiagram.ROW_ADDED_PROP.equals( prop ) )
        {
            // add a child to this edit part
            // causes an additional entry to appear in the tree of the outline
            // view
            addChild( createChild( evt.getNewValue() ), -1 );
        }
        else if( LayoutTplDiagram.ROW_REMOVED_PROP.equals( prop ) )
        {
            // remove a child from this edit part
            // causes the corresponding edit part to disappear from the tree in
            // the outline view
            removeChild( getEditPartForChild( evt.getNewValue() ) );
        }
        else
        {
            refreshVisuals();
        }
    }

    @Override
    protected Image getImage()
    {
        return super.getImage();
    }

    @Override
    protected String getText()
    {
        return super.getText();
    }
}
