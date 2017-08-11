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

package com.liferay.ide.swtbot.ui.page;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

import com.liferay.ide.swtbot.ui.condition.ShellCondition;
import com.liferay.ide.swtbot.ui.util.StringPool;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class Shell extends BasePageObject
{

    public Shell( SWTBot bot )
    {
        super( bot );
    }

    public Shell( SWTBot bot, String label )
    {
        super( bot, label );
    }

    public Shell( SWTBot bot, String label, int index )
    {
        super( bot, label, index );
    }

    public void activate()
    {
        getShell().isActive();
    }

    public void close()
    {
        getShell().close();
    }

    public void clickBtn( Button btn )
    {
        btn.click();
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
                if( shell.getText().equals( label ) )
                {
                    log.warn( "force closing of still open shell\"" + shell.getText() + StringPool.DOUBLE_QUOTE );

                    shell.close();

                    bot.waitUntil( new ShellCondition( label, false ) );

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

    protected SWTBotShell getShell()
    {
        if( hasIndex() )
        {
            return bot.shell( label, index );
        }

        return bot.shell( label );
    }

    public String getTitle()
    {
        return label;
    }

    public boolean isOpen()
    {
        return getShell().isVisible();
    }

    public void setFocus()
    {
        getShell().setFocus();
    }

    public void waitForPageToClose()
    {
        bot.waitUntil( new ShellCondition( label, false ) );
    }

    public void waitForPageToOpen()
    {
        bot.waitUntil( new ShellCondition( label, true ) );
    }

}
