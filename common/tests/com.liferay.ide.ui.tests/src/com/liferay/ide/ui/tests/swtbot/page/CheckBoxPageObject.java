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

import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class CheckBoxPageObject<T extends SWTBot> extends AbstractWidgetPageObject<SWTBot>
{

    public CheckBoxPageObject( SWTBot bot, String label )
    {
        super( bot, label );
    }

    public void deselect()
    {
        AbstractSWTBot<? extends Widget> widget = getWidget();

        SWTBotCheckBox checkBox = (SWTBotCheckBox) widget;
        bot.waitUntil( new WidgetEnabledCondition( checkBox, true ) );

        checkBox.deselect();;
    }

    @Override
    protected AbstractSWTBot<?> getWidget()
    {
        return bot.checkBox( label );
    }

    public boolean isChecked()
    {
        AbstractSWTBot<? extends Widget> widget = getWidget();

        SWTBotCheckBox checkBox = (SWTBotCheckBox) widget;

        return checkBox.isChecked();
    }

    public void select()
    {
        AbstractSWTBot<? extends Widget> widget = getWidget();

        SWTBotCheckBox checkBox = (SWTBotCheckBox) widget;
        bot.waitUntil( new WidgetEnabledCondition( checkBox, true ) );

        checkBox.select();
    }

}
