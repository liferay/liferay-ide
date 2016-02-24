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

package com.liferay.ide.ui.tests.swtbot.page;

import com.liferay.ide.ui.tests.swtbot.condition.WidgetEnabledCondition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRadio;

/**
 * @author Ashley Yuan
 */
public class RadioPageObject<T extends SWTBot> extends AbstractWidgetPageObject<SWTBot>
{

    public RadioPageObject( SWTBot bot, String label )
    {
        super( bot, label );
    }

    public void click()
    {
        bot.waitUntil( new WidgetEnabledCondition( getWidget(), true ) );

        getWidget().click();
    }

    @Override
    protected SWTBotRadio getWidget()
    {
        return bot.radio( label );
    }

    public boolean isSelected()
    {
        return getWidget().isSelected();
    }

}
