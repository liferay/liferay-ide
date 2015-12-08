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

package com.liferay.ide.ui.tests.swtbot.page;

import com.liferay.ide.ui.tests.swtbot.condition.ShellCondition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

public abstract class ShellPageObject<T extends SWTBot> extends AbstractPageObject<T>
{

    protected String title;

    public ShellPageObject( T bot, String title )
    {
        super( bot );

        this.title = title;
    }

    public void waitForPageToOpen()
    {
        bot.waitUntil( new ShellCondition( title, true ) );
    }

    protected void waitForPageToClose()
    {
        bot.waitUntil( new ShellCondition( title, false ) );
    }

    public void closeIfOpen()
    {
        long oldTimeOut = SWTBotPreferences.TIMEOUT;

        SWTBotPreferences.TIMEOUT = 1000;

        try
        {
            SWTBotShell[] shells = bot.shells();

            for( SWTBotShell shell : shells )
            {
                if( shell.getText().equals( title ) )
                {
                    log.warn( "force closing of still open shell\"" + shell.getText() + "\"" );

                    shell.close();

                    bot.waitUntil( new ShellCondition( title, false ) );

                    break;
                }
            }
        }
        catch( WidgetNotFoundException e )
        {
        }
        catch( TimeoutException e )
        {
        }
        finally
        {
            SWTBotPreferences.TIMEOUT = oldTimeOut;
        }
    }

    public String getTitle()
    {
        return title;
    }

}
