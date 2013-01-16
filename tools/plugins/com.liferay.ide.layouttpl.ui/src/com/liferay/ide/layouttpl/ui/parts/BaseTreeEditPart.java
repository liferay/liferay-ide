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

import com.liferay.ide.layouttpl.ui.model.ModelElement;

import java.beans.PropertyChangeListener;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractTreeEditPart;

/**
 * @author Gregory Amerson
 */
public abstract class BaseTreeEditPart extends AbstractTreeEditPart implements PropertyChangeListener
{

    public BaseTreeEditPart( Object model )
    {
        super( model );
    }

    public void activate()
    {
        if( !isActive() )
        {
            super.activate();
            ( (ModelElement) getModel() ).addPropertyChangeListener( this );
        }
    }

    public void deactivate()
    {
        if( isActive() )
        {
            super.deactivate();
            ( (ModelElement) getModel() ).removePropertyChangeListener( this );
        }
    }

    protected EditPart getEditPartForChild( Object child )
    {
        return (EditPart) getViewer().getEditPartRegistry().get( child );
    }

}
