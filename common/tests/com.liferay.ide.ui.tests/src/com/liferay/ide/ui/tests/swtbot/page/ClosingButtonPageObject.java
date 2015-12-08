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

import com.liferay.ide.ui.tests.swtbot.condition.WidgetEnabledCondition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;

/**
 * base class for a page object representing a shell with a button which closes the shell
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 * @param <T>
 *            - the bot class
 */
public abstract class ClosingButtonPageObject<T extends SWTBot> extends ShellPageObject<T>
{

    public ClosingButtonPageObject( T bot, String title )
    {
        super( bot, title );
    }

    protected void clickClosingButton( SWTBotButton button )
    {
        clickButton( button );

        waitForPageToClose();
    }

    protected void clickButton( SWTBotButton button )
    {
        bot.waitUntil( new WidgetEnabledCondition( button, true ) );

        button.click();
    }
}
