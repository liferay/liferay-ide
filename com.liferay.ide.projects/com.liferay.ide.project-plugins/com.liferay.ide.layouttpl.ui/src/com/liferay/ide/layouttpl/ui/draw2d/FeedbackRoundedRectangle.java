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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.draw2d;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.swt.SWT;

/**
 * @author Gregory Amerson
 */
public class FeedbackRoundedRectangle extends RoundedRectangle
{

    public FeedbackRoundedRectangle()
    {
        super();
        setBackgroundColor( ColorConstants.white );
        setAlpha( 128 );
        setAntialias( SWT.ON );
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj == null )
        {
            return false;
        }

        if( !( obj instanceof RoundedRectangle ) )
        {
            return false;
        }

        RoundedRectangle rRect = (RoundedRectangle) obj;

        return this.getSize().equals( rRect.getSize() ) &&
            this.getBackgroundColor().equals( rRect.getBackgroundColor() ) &&
            this.getLocation().equals( rRect.getLocation() ) && this.getAlpha().equals( rRect.getAlpha() );
    }

}
