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

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * @author Gregory Amerson
 */
public class ColumnFigure extends RoundedRectangle
{
    protected Font correctedFont = null;
    protected boolean drawText = true;
    protected String text = null;

    public ColumnFigure()
    {
        super();
        setAntialias( SWT.ON );
        // setText("50%");
    }

    @Override
    public void paintFigure( Graphics graphics )
    {
        super.paintFigure( graphics );

        if( getText() == null )
        {
            return;
        }

        if( !shouldDrawText() )
        {
            return;
        }

        if( correctedFont == null )
        {
            correctFont();
        }

        if( graphics.getFont() != null )
        {
            graphics.setTextAntialias( SWT.ON );
            graphics.setFont( getFont() );
            Dimension extent = FigureUtilities.getTextExtents( getText(), graphics.getFont() );

            graphics.drawString( getText(), bounds.x + ( bounds.width / 2 ) - ( extent.width / 2 ), bounds.y +
                ( bounds.height / 2 ) - ( extent.height / 2 ) );
        }
    }

    protected void correctFont()
    {
        Font initialFont = this.getFont();

        if( initialFont != null && ( !this.getFont().isDisposed() ) && ( !this.getFont().getDevice().isDisposed() ) )
        {
            FontData[] fontData = initialFont.getFontData();

            for( int i = 0; i < fontData.length; i++ )
            {
                fontData[i].setHeight( 24 );
            }

            Font oldFont = null;
            if( correctedFont != null && !correctedFont.isDisposed() )
            {
                oldFont = correctedFont;
            }

            correctedFont = new Font( this.getFont().getDevice(), fontData );
            setFont( correctedFont );

            if( oldFont != null )
            {
                oldFont.dispose();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        if( correctedFont != null && !( correctedFont.isDisposed() ) )
        {
            correctedFont.dispose();
        }
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public boolean shouldDrawText()
    {
        return drawText;
    }

    public void setDrawText( boolean drawText )
    {
        this.drawText = drawText;
    }

}
