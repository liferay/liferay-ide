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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;

import com.liferay.ide.swtbot.ui.tests.condition.WidgetEnabledCondition;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public abstract class ClosingButtonPO extends ShellPO
{

    public ClosingButtonPO( SWTBot bot, String title )
    {
        super( bot, title );
    }

    protected void clickButton( SWTBotButton button )
    {
        bot.waitUntil( new WidgetEnabledCondition( button, true ) );

        button.click();
    }

    protected void clickClosingButton( SWTBotButton button )
    {
        clickButton( button );
    }

}
