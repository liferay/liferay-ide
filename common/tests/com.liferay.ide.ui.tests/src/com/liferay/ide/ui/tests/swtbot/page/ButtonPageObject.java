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
 * ** base class for a page object representing a shell with a title and a cancel button (Dialog or Wizard)
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 * @param <T>
 *            - the bot class
 */
public class ButtonPageObject<T extends SWTBot> extends AbstractWidgetPageObject<T>
{

    public ButtonPageObject( T bot, int index )
    {
        super( bot, index );
    }

    public ButtonPageObject( T bot, String label )
    {
        super( bot, label );
    }

    public ButtonPageObject( T bot, String label, int index )
    {
        super( bot, label, index );
    }

    public void click()
    {
        getWidget().click();
    }

    @Override
    protected SWTBotButton getWidget()
    {
        if( label == null )
        {
            return bot.button( index );
        }

        return bot.button( label, index );
    }

    public boolean isEnabled()
    {
        return ( (SWTBotButton) getWidget() ).isEnabled();
    }

}
