/*******************************************************************************
 * Copyright (c) 2008 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Kay-Uwe Graw - initial API and implementation

 *******************************************************************************/

package com.liferay.ide.ui.tests.swtbot.condition;

import org.junit.Assert;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * ICondition implementation to wait for a view to become visible or not visible This is useful when the creation of a
 * view takes a while after the initiating user action has been carried out
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public class ViewVisibleCondition implements ICondition
{

    private final String identifier;

    private SWTWorkbenchBot bot;

    private final boolean visible;
    private final boolean isId;

    public ViewVisibleCondition( String viewIdentifier, boolean visible, boolean isId )
    {
        this.identifier = viewIdentifier;

        this.visible = visible;

        this.isId = isId;
    }

    public String getFailureMessage()
    {
        if( visible )
        {
            return "wait for view " + identifier + " is visible failed"; //$NON-NLS-1$
        }
        else
        {
            return "wait for view " + identifier + " is not visible failed"; //$NON-NLS-1$
        }
    }

    public void init( SWTBot bot )
    {
        if( bot instanceof SWTWorkbenchBot )
        {
            this.bot = SWTWorkbenchBot.class.cast( bot );
        }
        else
        {
            Assert.fail( "init with wrong bot class" );
        }
    }

    public boolean test() throws Exception
    {
        return viewIsVisible() == visible;
    }

    private SWTBotView getView()
    {
        long oldTimeOut = SWTBotPreferences.TIMEOUT;

        SWTBotPreferences.TIMEOUT = 1000;

        SWTBotView view = null;

        try
        {
            if( isId )
            {
                view = bot.viewById( identifier );
            }
            else
            {
                view = bot.viewByTitle( identifier );
            }

        }
        catch( WidgetNotFoundException e )
        {
        }
        finally
        {
            SWTBotPreferences.TIMEOUT = oldTimeOut;
        }

        return view;
    }

    private boolean viewIsVisible()
    {
        final SWTBotView view = getView();

        if( view != null )
        {
            return UIThreadRunnable.syncExec( new BoolResult()
            {

                public Boolean run()
                {
                    if( view.getWidget() instanceof Control )
                    {
                        return ( (Control) view.getWidget() ).isVisible();
                    }
                    else
                    {
                        return false;
                    }
                }
            } );
        }

        return false;
    }
}
