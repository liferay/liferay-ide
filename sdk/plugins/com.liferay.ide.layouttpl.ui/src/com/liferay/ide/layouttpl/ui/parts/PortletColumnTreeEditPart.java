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

import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.policies.PortletColumnComponentEditPolicy;

import java.beans.PropertyChangeEvent;
import java.net.URL;

import org.eclipse.gef.EditPolicy;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Gregory Amerson
 */
public class PortletColumnTreeEditPart extends BaseTreeEditPart
{

    protected Image icon;

    public PortletColumnTreeEditPart( PortletColumn model )
    {
        super( model );

        URL url = LayoutTplUI.getDefault().getBundle().getEntry( "/icons/e16/layout.png" );
        icon = ImageDescriptor.createFromURL( url ).createImage();
    }

    protected void createEditPolicies()
    {
        // allow removal of the associated model element
        installEditPolicy( EditPolicy.COMPONENT_ROLE, new PortletColumnComponentEditPolicy() );
    }

    protected PortletColumn getCastedModel()
    {
        return (PortletColumn) getModel();
    }

    protected Image getImage()
    {
        return icon;
    }

    protected String getText()
    {
        String text = "Portlet Column";

        if( getCastedModel().getWeight() == PortletColumn.DEFAULT_WEIGHT )
        {
            text += " - 100%";
        }
        else
        {
            text += " - " + getCastedModel().getWeight() + "%";
        }

        return text;
    }

    public void propertyChange( PropertyChangeEvent evt )
    {
        refreshVisuals();
    }
}
