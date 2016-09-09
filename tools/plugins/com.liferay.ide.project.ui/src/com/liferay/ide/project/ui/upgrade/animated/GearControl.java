/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageActionListener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageValidationListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Andy Wu
 * @author Simon Jiang
 */
public class GearControl extends AbstractCanvas
    implements PageNavigatorListener, PageActionListener, PageValidationListener
{

    private float angle;

    private final int BORDER = 20;

    private Display display;

    private Image errorImage;
    private Map<String, String> errorMessageMap = new HashMap<String, String>();

    private final Color[] gearBackground = new Color[2];
    private final Color[] gearForeground = new Color[2];

    private int gearMaxNumber = 10;

    private final Path[] gearPaths = new Path[gearMaxNumber];
    private final Point[] tooltipPoints = new Point[gearMaxNumber];

    private Color DARK_GRAY;
    private Color GRAY;
    private Color WHITE;
    private Color tooltipColor;

    private int hover = NONE;

    private boolean needRedraw = false;

    private Font numberFont;

    private int oldHover = NONE;

    private int oldSelection = NONE;

    private boolean overflow;

    private final double RADIAN = 2 * Math.PI / 360;

    private float radius;

    private int selection;

    private final List<SelectionChangedListener> selectionChangedListeners =
        Collections.synchronizedList( new ArrayList<SelectionChangedListener>() );

    private float speed;

    private long startAnimation;

    private final int TEETH = 8;

    private final float ANGLE = 360 / TEETH;

    private Font tooltipFont;

    public GearControl( Composite parent, int style )
    {
        super( parent, style );

        init();

        scheduleRun();
    }

    public void addSelectionChangedListener( SelectionChangedListener listener )
    {
        selectionChangedListeners.add( listener );
    }

    private Path drawGear(
        GC gc, Display display, double cx, double cy, double outerR, double innerR, float angleOffset )
    {
        double radian2 = ANGLE / 2 * RADIAN;
        double radian3 = .06;

        Path path = new Path( display );

        for( int i = 0; i < TEETH; i++ )
        {
            double radian = ( i * ANGLE + angleOffset ) * RADIAN;
            double x = cx + outerR * Math.cos( radian );
            double y = cy - outerR * Math.sin( radian );

            if( i == 0 )
            {
                path.moveTo( (int) x, (int) y );
            }

            double r1 = radian + radian3;
            double r3 = radian + radian2;
            double r2 = r3 - radian3;
            double r4 = r3 + radian2;

            x = cx + innerR * Math.cos( r1 );
            y = cy - innerR * Math.sin( r1 );
            path.lineTo( (int) x, (int) y );

            x = cx + innerR * Math.cos( r2 );
            y = cy - innerR * Math.sin( r2 );
            path.lineTo( (int) x, (int) y );

            x = cx + outerR * Math.cos( r3 );
            y = cy - outerR * Math.sin( r3 );
            path.lineTo( (int) x, (int) y );

            x = cx + outerR * Math.cos( r4 );
            y = cy - outerR * Math.sin( r4 );
            path.lineTo( (int) x, (int) y );
        }

        path.close();
        gc.fillPath( path );
        gc.drawPath( path );

        return path;
    }

    public final int getSelection()
    {
        return selection;
    }

    protected void init()
    {
        super.init();

        display = getDisplay();

        errorImage = JFaceResources.getImage( Dialog.DLG_IMG_MESSAGE_ERROR );

        WHITE = display.getSystemColor( SWT.COLOR_WHITE );
        GRAY = display.getSystemColor( SWT.COLOR_GRAY );
        DARK_GRAY = display.getSystemColor( SWT.COLOR_DARK_GRAY );

        Font initialFont = getFont();
        FontData[] fontData = initialFont.getFontData();

        for( int i = 0; i < fontData.length; i++ )
        {
            fontData[i].setHeight( 16 );
            fontData[i].setStyle( SWT.BOLD );
        }

        baseFont = new Font( display, fontData );

        numberFont = createFont( 24 );
        tooltipFont = createFont( 24 );

        radius = 32;
        setSize( (int) ( gearMaxNumber * 2 * radius ), (int) ( 2 * radius ) );

        // Not selected.
        gearBackground[0] = createColor( 169, 171, 202 );
        gearForeground[0] = createColor( 140, 132, 171 );

        // Selected.
        gearBackground[1] = createColor( 247, 148, 30 );
        gearForeground[1] = createColor( 207, 108, 0 );

        tooltipColor = createColor( 253, 232, 206 );
    }

    @Override
    protected boolean needRedraw()
    {
        boolean retVal = false;

        if( needRedraw )
        {
            needRedraw = false;
            retVal = true;
        }

        if( overflow )
        {
            overflow = false;
            retVal = true;
        }

        if( hover != oldHover )
        {
            retVal = true;
        }

        if( speed >= ANGLE )
        {
            startAnimation = 0;

            return retVal;
        }

        long now = System.currentTimeMillis();

        if( startAnimation == 0 )
        {
            startAnimation = now;
        }

        long timeSinceStart = now - startAnimation;

        speed = timeSinceStart * ANGLE / 400;
        angle += speed;

        return true;
    }

    @Override
    protected void onMouseDown( int x, int y )
    {
        if( x != Integer.MIN_VALUE && y != Integer.MIN_VALUE )
        {
            GC gc = new GC( this );

            for( int i = 0; i < gearPaths.length; i++ )
            {
                Path path = gearPaths[i];

                if( path != null && path.contains( x, y, gc, false ) )
                {
                    if( i != getSelection() )
                    {
                        setSelection( i );
                    }
                }
            }
        }
    }

    @Override
    protected void onMouseMove( int x, int y )
    {
        if( x != Integer.MIN_VALUE && y != Integer.MIN_VALUE )
        {
            GC gc = new GC( this );

            int number = UpgradeView.getPageNumber();

            for( int i = 0; i < number; i++ )
            {
                Path path = gearPaths[i];

                if( path != null && path.contains( x, y, gc, false ) )
                {
                    if( i != hover )
                    {
                        hover = i;
                    }

                    return;
                }
            }
        }

        hover = NONE;
    }

    @Override
    public void onPageAction( PageActionEvent event )
    {
        int targetPageIndex = event.getTargetPageIndex();

        needRedraw = true;

        if( targetPageIndex != NONE )
        {
            setSelection( targetPageIndex );
        }

    }

    @Override
    public void onPageNavigate( PageNavigateEvent event )
    {
        setSelection( event.getTargetPage() );
    }

    @Override
    public void onValidation( PageValidateEvent event )
    {
        String pageId = event.getPageId();
        String message = event.getMessage();

        errorMessageMap.put( pageId, message );

        Page page = UpgradeView.getPage( selection );

        if( page.getPageId().equals( pageId ) )
        {
            needRedraw = true;
        }
    }

    @Override
    protected void paint( GC gc )
    {
        gc.setFont( getBaseFont() );
        gc.setLineWidth( 3 );
        gc.setAntialias( SWT.ON );

        int alpha = Math.min( (int) ( 255 * speed / ANGLE ), 255 );

        int number = UpgradeView.getPageNumber();

        for( int i = 0; i < number; i++ )
        {
            tooltipPoints[i] = paintGear( gc, i, alpha );
        }

        if( hover >= 0 && hover < tooltipPoints.length )
        {
            Point point = tooltipPoints[hover];

            String title = UpgradeView.getPage( hover ).getTitle();

            gc.setFont( tooltipFont );
            gc.setForeground( DARK_GRAY );
            gc.setBackground( tooltipColor );

            Rectangle rectangle = drawText( gc, point.x, point.y + 14, title, 2 );

            gc.setForeground( GRAY );
            gc.setLineWidth( 1 );
            gc.drawRectangle( rectangle );
        }

        paintErrorMessage( gc );

        oldHover = hover;

    }

    private void paintErrorMessage( GC gc )
    {
        Page page = UpgradeView.getPage( selection );

        String pageId = page.getPageId();

        String errorMessage = errorMessageMap.get( pageId );

        if( errorMessage != null && !errorMessage.equals( "ok" ) )
        {
            drawImage( gc, errorImage, 30, 130 );

            gc.setBackground( tooltipColor );
            gc.setForeground( DARK_GRAY );
            gc.setLineWidth( 1 );

            Rectangle rectangle = drawTextNotCenter( gc, 40, 120, errorMessage, 2 );

            gc.drawRectangle( rectangle );
        }
    }

    private Point paintBadge( GC gc, double x, double y, double outerR, int i, int alpha )
    {
        if( selection >= gearMaxNumber )
        {
            gc.setAlpha( 255 - alpha );
        }
        else if( oldSelection >= gearMaxNumber )
        {
            gc.setAlpha( alpha );
        }

        Image badgeImage = null;

        Page page = UpgradeView.getPage( i );

        PageAction pageAction = page.getSelectedAction();

        if( pageAction != null )
        {
            badgeImage = pageAction.getBageImage();
        }

        if( badgeImage != null )
        {
            gc.drawImage( badgeImage, (int) ( x - badgeImage.getBounds().width / 2 ), (int) ( y - outerR - 12 ) );
            gc.setAlpha( 255 );
        }

        return new Point( (int) x, (int) ( y + outerR ) );
    }

    private Point paintGear( GC gc, int i, int alpha )
    {
        double offset = 2 * i * radius;
        double x = BORDER + radius + offset;
        double y = BORDER + radius;
        double r2 = (double) radius * .8f;
        double r3 = (double) radius * .5f;

        int selected = 0;
        double factor = 1;

        if( i == oldSelection )
        {
            if( speed < ANGLE / 2 )
            {
                selected = 1;
            }
        }
        else if( i == selection )
        {
            if( speed >= ANGLE / 2 )
            {
                selected = 1;
                factor += ( ANGLE - speed ) * .02;
            }
            else
            {
                factor += speed * .02;
            }
        }

        boolean hovered = false;

        if( i == hover )
        {
            factor += .1;
            oldHover = hover;
            if( selected == 0 )
            {
                hovered = true;
            }
        }

        double outerR = factor * radius;
        double innerR = factor * r2;
        float angleOffset = ( angle + i * ANGLE ) * ( i % 2 == 1 ? -1 : 1 ) / 7;

        gc.setForeground( hovered ? DARK_GRAY : gearForeground[selected] );
        gc.setBackground( hovered ? GRAY : gearBackground[selected] );

        if( outerR < 32.0 )
        {
            outerR = 32.0D;
        }

        if( innerR < 25.0 )
        {
            innerR = 25.600000381469727D;
        }

        Path path = drawGear( gc, display, x, y, outerR, innerR, angleOffset );

        if( gearPaths[i] != null )
        {
            gearPaths[i].dispose();
        }

        gearPaths[i] = path;

        int ovalX = (int) ( x - factor * r3 );
        int ovalY = (int) ( y - factor * r3 );
        int ovalR = (int) ( 2 * factor * r3 );
        gc.setBackground( WHITE );
        gc.fillOval( ovalX, ovalY, ovalR, ovalR );
        gc.drawOval( ovalX, ovalY, ovalR, ovalR );

        if( i < gearMaxNumber )
        {
            String number = Integer.toString( i + 1 );

            gc.setForeground( selected == 1 ? gearForeground[1] : GRAY );
            gc.setFont( numberFont );

            drawText( gc, x, y - 1, number );
        }

        return paintBadge( gc, x, y, outerR, i, alpha );
    }

    public void restart()
    {
        angle = 0;
        speed = 0;
    }

    public final void setSelection( int selection )
    {
        hover = NONE;
        oldHover = NONE;

        if( selection < 0 )
        {
            selection = 0;
            overflow = true;
        }
        else if( selection > gearMaxNumber - 1 )
        {
            selection = gearMaxNumber - 1;
            overflow = true;
        }

        if( overflow )
        {
            overflow = false;

            while( needRedraw() )
            {
            }

            overflow = true;

            return;
        }

        oldSelection = this.selection;

        this.selection = selection;

        for( SelectionChangedListener listener : selectionChangedListeners )
        {
            listener.onSelectionChanged( selection );
        }

        restart();
    }
}
