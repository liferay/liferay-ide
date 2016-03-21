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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * ICondition implementation to wait for an editor to become active or inactive This is useful when the creation of an
 * editor takes a while after the initiating user action has been carried out
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public class EditorActiveCondition implements ICondition
{

    private final String name;

    private SWTWorkbenchBot bot;

    private final boolean active;

    public EditorActiveCondition( String name, boolean active )
    {
        this.name = name;

        this.active = active;
    }

    public String getFailureMessage()
    {
        if( active )
        {
            return "wait for editor " + name + " is active failed";
        }
        else
        {
            return "wait for editor " + name + " is not active failed";
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
        return editorIsActive( name ) == active;
    }

    private SWTBotEditor getEditor( String name )
    {
        long oldTimeOut = SWTBotPreferences.TIMEOUT;

        SWTBotPreferences.TIMEOUT = 1000;

        try
        {
            return bot.editorByTitle( name );

        }
        catch( WidgetNotFoundException e )
        {
        }
        finally
        {
            SWTBotPreferences.TIMEOUT = oldTimeOut;
        }

        return null;
    }

    private boolean editorIsActive( String editorName )
    {
        final SWTBotEditor editor = getEditor( editorName );

        if( editor != null )
        {
            return UIThreadRunnable.syncExec( new BoolResult()
            {

                public Boolean run()
                {
                    return editor.isActive();
                }
            } );
        }

        return false;
    }
}
