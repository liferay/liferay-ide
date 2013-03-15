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

import com.liferay.ide.layouttpl.ui.model.ModelElement;

import java.beans.PropertyChangeListener;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.widgets.Display;

/**
 * @author Gregory Amerson
 */
public abstract class BaseGraphicalEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener
{

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

    protected Display getDisplay()
    {
        return this.getViewer().getControl().getDisplay();
    }

}
