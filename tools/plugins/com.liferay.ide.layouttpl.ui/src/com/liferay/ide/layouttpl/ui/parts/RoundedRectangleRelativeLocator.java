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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RelativeLocator;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;

/**
 * @author Gregory Amerson
 */
public class RoundedRectangleRelativeLocator extends RelativeLocator
{

    public RoundedRectangleRelativeLocator( IFigure figure, int direction )
    {
        super( figure, .05, .05 );
    }

    protected Rectangle getReferenceBox()
    {
        IFigure f = getReferenceFigure();
        if( f instanceof HandleBounds )
            return ( (HandleBounds) f ).getHandleBounds();
        return super.getReferenceBox();
    }
}
