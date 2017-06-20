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

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class ConfirmPO extends ClosingButtonPO
{

    protected final String text;

    public ConfirmPO( SWTBot bot, String title, String text )
    {
        super( bot, title );

        this.text = text;
    }

    public void confirm()
    {
        clickClosingButton( confirmButton() );
    }

    protected SWTBotButton confirmButton()
    {
        return bot.button( text );
    }

}
