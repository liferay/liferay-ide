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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;

import com.liferay.ide.swtbot.ui.tests.condition.WidgetEnabledCondition;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ComboBoxPO extends AbstractWidgetPO
{

    public ComboBoxPO( SWTBot bot, String label )
    {
        super( bot, label );
    }

    public String[] getAvailableComboValues()
    {
        return getWidget().items();
    }

    @Override
    protected SWTBotCombo getWidget()
    {
        return bot.comboBoxWithLabel( label );
    }

    public boolean isEnabled()
    {
        return getWidget().isEnabled();
    }

    public void setSelection( String value )
    {
        bot.waitUntil( new WidgetEnabledCondition( getWidget(), true ) );

        getWidget().setSelection( value );

        sleep();
    }

    public void setText( String text )
    {
        getWidget().setText( text );
    }

    public int getComboBoxItemCounts( int count )
    {
        return getWidget().itemCount();
    }

}
