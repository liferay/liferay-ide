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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class NavigatorControl extends AbstractCanvas implements SelectionChangedListener
{

    public static final int BORDER = 30;

    private static final int BACK = NONE - 1;
    private static final int NEXT = BACK - 1;
    private static final int CHOICES = NEXT - 1;

    private int hover = NONE;

    private int oldHover = NONE;

    private final Image[] backImages = new Image[2];
    private final Image[] nextImages = new Image[2];

    private int buttonR;
    private int answerY;

    private Rectangle backBox;
    private Rectangle nextBox;

    private int pageY;

    private Rectangle[] actionBoxes;

    private Display display;

    private int select = 0;

    private boolean needRedraw = false;

    private final List<PageNavigatorListener> naviListeners =
        Collections.synchronizedList( new ArrayList<PageNavigatorListener>() );

    private final List<PageActionListener> actionListeners =
        Collections.synchronizedList( new ArrayList<PageActionListener>() );

    public NavigatorControl( Composite parent, int style )
    {
        super( parent, style | SWT.DOUBLE_BUFFERED );

        display = getDisplay();

        setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );

        init();

        scheduleRun();
    }

    protected boolean actionOnMouseDown( int x, int y )
    {
        int i = getAction( x, y );

        if( i != NONE )
        {
            doAction( i );

            return true;
        }

        return false;
    }

    protected int actionOnMouseMove( int x, int y )
    {
        int i = getAction( x, y );

        if( i != NONE )
        {
            // pageBufferUpdated = false;
            return CHOICES - i;
        }

        return NONE;
    }

    public void addPageActionListener( PageActionListener listener )
    {
        this.actionListeners.add( listener );
    }

    public void addPageNavigateListener( PageNavigatorListener listener )
    {
        this.naviListeners.add( listener );
    }

    private void doAction( int i )
    {
        Page page = getSelectedPage();
        PageAction oldSelection = page.getSelectedAction();
        PageAction[] pageActions = page.getActions();

        PageAction targetAction = pageActions[i];

        if( targetAction.equals( oldSelection ) )
        {
            targetAction = null;
        }

        page.setSelectedAction( targetAction );

        PageActionEvent event = new PageActionEvent();

        event.setTargetPageIndex( NONE );

        if( page.showNextPage() && targetAction != null )
        {
            event.setTargetPageIndex( select + 1 );
        }

        for( PageActionListener listener : actionListeners )
        {
            listener.onPageAction( event );
        }

        needRedraw = true;
    }

    public final int getAction( int x, int y )
    {
        PageAction[] actions = getSelectedPage().getActions();

        if( actions == null || actions.length < 1 )
        {
            return NONE;
        }

        for( int i = 0; i < actions.length; i++ )
        {
            Rectangle box = actionBoxes[i];

            if( box != null && box.contains( x, y ) )
            {
                return i;
            }
        }

        return NONE;
    }

    private Page getSelectedPage()
    {
        return UpgradeView.getPage( select );
    }

    protected void init()
    {
        super.init();

        backImages[0] = loadImage( "back.png" );
        backImages[1] = loadImage( "back_hover.png" );

        nextImages[0] = loadImage( "next.png" );
        nextImages[1] = loadImage( "next_hover.png" );

        buttonR = nextImages[0].getBounds().height / 2;

        answerY = 5 + buttonR;

        actionBoxes = new Rectangle[2];

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

        if( hover != oldHover )
        {
            retVal = true;
        }

        return retVal;
    }

    @Override
    protected void onMouseDown( int x, int y )
    {
        boolean isNavigate = false;

        if( x != Integer.MIN_VALUE && y != Integer.MIN_VALUE )
        {
            Page page = getSelectedPage();

            if( page != null )
            {
                PageNavigateEvent event = new PageNavigateEvent();

                if( page.showBackPage() && backBox != null && backBox.contains( x, y ) )
                {
                    event.setTargetPage( select - 1 );

                    isNavigate = true;
                }

                if( page.showNextPage() && nextBox != null && nextBox.contains( x, y ) )
                {
                    event.setTargetPage( select + 1 );

                    isNavigate = true;
                }

                if( isNavigate )
                {
                    for( PageNavigatorListener listener : naviListeners )
                    {
                        listener.onPageNavigate( event );
                    }
                }

                actionOnMouseDown( x, y );
            }
        }
    }

    @Override
    protected void onMouseMove( int x, int y )
    {
        if( x != Integer.MIN_VALUE && y != Integer.MIN_VALUE )
        {
            Page page = getSelectedPage();

            if( page != null )
            {
                if( page.showBackPage() && backBox != null && backBox.contains( x, y ) )
                {
                    hover = BACK;
                    return;
                }

                if( page.showNextPage() && nextBox != null && nextBox.contains( x, y ) )
                {
                    hover = NEXT;
                    return;
                }

                hover = actionOnMouseMove( x, y );
                return;
            }
        }
        else
        {
            hover = NONE;
        }
    }

    @Override
    public void onSelectionChanged( int targetSelection )
    {
        select = targetSelection;

        needRedraw = true;
    }

    @Override
    protected void paint( GC gc )
    {
        gc.setFont( getBaseFont() );
        gc.setLineWidth( 3 );
        gc.setAntialias( SWT.ON );

        Page page = getSelectedPage();

        backBox = null;
        nextBox = null;

        if( page.showBackPage() )
        {
            backBox = drawImage( gc, backImages[hover == BACK ? 1 : 0], getBounds().width / 2 - 200, answerY );
        }

        if( page.showNextPage() )
        {
            nextBox = drawImage( gc, nextImages[hover == NEXT ? 1 : 0], getBounds().width / 2 + 200, answerY );
        }

        paintActions( gc, page );

        oldHover = hover;
    }

    public Rectangle paintAction( GC gc, int index, int x, int y, boolean hovered, boolean selected, PageAction action )
    {
        Image[] images = action.getImages();

        Image image = images[0];

        if( hovered )
        {
            image = images[2];
        }
        else if( selected )
        {
            image = images[1];
        }

        return drawImage( gc, image, x, y );
    }

    private void paintActions( GC gc, Page page )
    {
        PageAction[] actions = page.getActions();
        PageAction selectedAction = page.getSelectedAction();

        if( actions == null )
        {
            return;
        }

        boolean selecteds[] = new boolean[actions.length];
        boolean hovereds[] = new boolean[actions.length];

        Point sizes[] = new Point[actions.length];

        // int width = ( actions.length - 1 ) * BORDER;
        int height = 0;

        for( int i = 0; i < actions.length; i++ )
        {
            selecteds[i] = actions[i].equals( selectedAction );

            if( CHOICES - i == hover )
            {
                // oldHover = hover;
                hovereds[i] = true;
            }

            sizes[i] = actions[i].getSize();
            // width += sizes[i].x;
            height = Math.max( height, sizes[i].y );
        }

        int x = getBounds().width / 2 - 40;

        int y = answerY - pageY;

        for( int i = 0; i < actions.length; i++ )
        {
            PageAction action = actions[i];

            actionBoxes[i] = paintAction( gc, i, x, y, hovereds[i], selecteds[i], action );

            x = getBounds().width / 2 + 40;
        }
    }
}
