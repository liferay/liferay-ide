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

package com.liferay.ide.swtbot.ui.tests.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public abstract class AbstractWidgetPO extends AbstractPO
{

    protected int index = -1;
    protected String label;

    public AbstractWidgetPO( SWTBot bot )
    {
        super( bot );
    }

    public AbstractWidgetPO( SWTBot bot, int index )
    {
        super( bot );
        this.index = index;
    }

    public AbstractWidgetPO( SWTBot bot, String label )
    {
        super( bot );
        this.label = label;
    }

    public AbstractWidgetPO( SWTBot bot, String label, int index )
    {
        super( bot );
        this.index = index;
        this.label = label;
    }

    public void contextMenu( String menu )
    {
        getWidget().contextMenu( menu ).click();
    }

    public String getLabel()
    {
        return label;
    }

    public String getText()
    {
        return getWidget().getText();
    }

    protected abstract AbstractSWTBot<?> getWidget();

    public boolean isActive()
    {
        return getWidget().isActive();
    }

    public boolean isEnabled()
    {
        return getWidget().isEnabled();
    }

    public boolean isVisible()
    {
        return getWidget().isVisible();
    }

    public void rightClick( String menu )
    {
        contextMenu( menu );
    }

    public void setFocus()
    {
        getWidget().setFocus();
    }

    protected Event createMouseEvent( int x, int y, int button, int stateMask, int count )
    {
        Event event = new Event();
        event.time = (int) System.currentTimeMillis();
        event.widget = getWidget().widget;
        event.display = getWidget().display;
        event.x = x;
        event.y = y;
        event.button = button;
        event.stateMask = stateMask;
        event.count = count;
        return event;
    }

    private void mouseDown( final int x, final int y, final int button )
    {
        asyncExec( new VoidResult()
        {

            @Override
            public void run()
            {
                Event event = createMouseEvent( x, y, button, 0, 0 );
                event.type = SWT.MouseDown;
                getWidget().display.post( event );
            }
        } );
    }

    private void moveMouse( final int x, final int y )
    {
        asyncExec( new VoidResult()
        {

            @Override
            public void run()
            {
                Event event = createMouseEvent( x, y, 0, 0, 0 );
                event.type = SWT.MouseMove;
                getWidget().display.post( event );
            }
        } );
    }

    protected void syncExec( VoidResult toExecute )
    {
        UIThreadRunnable.syncExec( getWidget().display, toExecute );
    }

    public void click( final int x, final int y )
    {
        syncExec( new VoidResult()
        {

            @Override
            public void run()
            {
                moveMouse( x, y );
                mouseDown( x, y, 1 );
                mouseUp( x, y, 1 );
            }
        } );
    }

    private void mouseUp( final int x, final int y, final int button )
    {
        asyncExec( new VoidResult()
        {

            @Override
            public void run()
            {
                Event event = createMouseEvent( x, y, button, 0, 0 );
                event.type = SWT.MouseUp;
                getWidget().display.post( event );
            }
        } );
    }

    protected void asyncExec( VoidResult toExecute )
    {
        UIThreadRunnable.asyncExec( getWidget().display, toExecute );
    }

}
