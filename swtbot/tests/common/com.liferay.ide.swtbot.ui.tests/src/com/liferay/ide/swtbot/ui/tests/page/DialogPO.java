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
public class DialogPO extends CancelPO
{

    private String _confirmButton;

    public DialogPO( SWTBot bot, String title )
    {
        super( bot, title, null );
    }

    public DialogPO( SWTBot bot, String cancelButtonText, String confirmButtonText )
    {
        this( bot, "", cancelButtonText, confirmButtonText );
    }

    public DialogPO( SWTBot bot, String title, String cancelButtonText, String confirmButtonText )
    {
        super( bot, title, cancelButtonText );

        _confirmButton = confirmButtonText;
    }

    public void confirm()
    {
        clickClosingButton( confirmButton() );
    }

    public SWTBotButton confirmButton()
    {
        return bot.button( _confirmButton );
    }
}
