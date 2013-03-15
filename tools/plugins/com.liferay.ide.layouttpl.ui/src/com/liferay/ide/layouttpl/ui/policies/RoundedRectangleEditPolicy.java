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

package com.liferay.ide.layouttpl.ui.policies;

import com.liferay.ide.layouttpl.ui.draw2d.ColumnFigure;
import com.liferay.ide.layouttpl.ui.parts.RoundedRectangleResizeHandle;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.gef.handles.ResizeHandle;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "rawtypes" )
public class RoundedRectangleEditPolicy extends ResizableEditPolicy
{

    private Label feedbackLabel;

    protected List createSelectionHandles()
    {
        List handles = super.createSelectionHandles();

        MoveHandle moveHandle = null;
        ResizeHandle resizeHandle = null;

        for( Iterator i = handles.iterator(); i.hasNext(); )
        {
            Object handle = i.next();

            if( handle instanceof MoveHandle )
            {
                moveHandle = (MoveHandle) handle;
            }
            else if( handle instanceof ResizeHandle )
            {
                ResizeHandle tempResizeHandle = (ResizeHandle) handle;

                try
                {
                    Field f = ResizeHandle.class.getDeclaredField( "cursorDirection" ); //$NON-NLS-1$
                    f.setAccessible( true );
                    int cursorDirection = f.getInt( tempResizeHandle );

                    if( cursorDirection == PositionConstants.NORTH_WEST )
                    {
                        resizeHandle = tempResizeHandle;

                    }

                }
                catch( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }

        if( moveHandle != null )
        {
            handles.remove( moveHandle );
        }

        if( resizeHandle != null )
        {
            handles.remove( resizeHandle );
            handles.add( new RoundedRectangleResizeHandle( (GraphicalEditPart) getHost(), PositionConstants.NORTH_WEST ) );
        }

        return handles;
    }

    @Override
    protected void showChangeBoundsFeedback( ChangeBoundsRequest request )
    {
        super.showChangeBoundsFeedback( request );
        IFigure feedbackFigure = getDragSourceFeedbackFigure();

        if( feedbackLabel == null )
        {
            feedbackLabel = new Label()
            {

                @Override
                protected void finalize() throws Throwable
                {

                    super.finalize();
                    Font font = this.getFont();
                    if( font != null && !font.isDisposed() )
                    {
                        font.dispose();
                    }
                }

            };
            feedbackLabel.setText( "50%" ); //$NON-NLS-1$
            Font font = feedbackFigure.getFont();
            FontData[] fontData = font.getFontData();
            for( int i = 0; i < fontData.length; i++ )
            {
                fontData[i].setHeight( 24 );
            }
            Font correctedFont = new Font( font.getDevice(), fontData );
            feedbackLabel.setFont( correctedFont );
        }

        feedbackLabel.setLocation( feedbackFigure.getBounds().getCenter() );
        feedbackLabel.setSize( feedbackLabel.getPreferredSize() );
        addFeedback( feedbackLabel );

        ( (ColumnFigure) getHostFigure() ).setDrawText( false );

    }

    @Override
    protected void eraseChangeBoundsFeedback( ChangeBoundsRequest request )
    {
        super.eraseChangeBoundsFeedback( request );

        if( feedbackLabel != null )
        {
            removeFeedback( feedbackLabel );
            feedbackLabel = null;
        }

        ( (ColumnFigure) getHostFigure() ).setDrawText( true );
    }

}
