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

import com.liferay.ide.swtbot.ui.tests.condition.WidgetEnabledCondition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class TextPO extends AbstractWidgetPO
{

    public TextPO( SWTBot bot )
    {
        this( bot, 0 );
    }

    public TextPO( SWTBot bot, int index )
    {
        super( bot, index );
    }

    public TextPO( SWTBot bot, String label )
    {
        super( bot, label );
    }

    @Override
    protected SWTBotText getWidget()
    {
        if( label == null )
        {
            return bot.text( index );
        }

        return bot.textWithLabel( label );
    }

    public void setText( String text )
    {
        bot.waitUntil( new WidgetEnabledCondition( getWidget(), true ) );

        getWidget().setText( text );
    }

    public void setTextWithoutLabel( String text )
    {
        bot.text().setText( text );
    }

}