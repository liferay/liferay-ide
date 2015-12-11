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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;

/**
 * Base class for a page object representing a shell with cancel and confirm button
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 * @param <T>
 *            - the bot class
 */
public class DialogPageObject<T extends SWTBot> extends CancelPageObject<T>
{

    private final String text;

    public DialogPageObject( T bot, String title, String cancelButtonText, String confirmButtonText )
    {
        super( bot, title, cancelButtonText );

        text = confirmButtonText;
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
