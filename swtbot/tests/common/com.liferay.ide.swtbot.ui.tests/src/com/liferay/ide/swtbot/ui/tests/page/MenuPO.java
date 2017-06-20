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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class MenuPO extends AbstractWidgetPO
{

    private String[] _labels;

    public MenuPO( SWTBot bot, String label )
    {
        super( bot, label );
    }

    public MenuPO( SWTBot bot, String[] label )
    {
        super( bot );

        _labels = label;
    }

    @Override
    protected SWTBotMenu getWidget()
    {
        if( label != null )
        {
            return bot.menu( label );
        }

        SWTBotMenu menu = bot.menu( _labels[0] );

        for( int i = 1; i < _labels.length; i++ )
        {
            menu = menu.menu( _labels[i] );
        }

        return menu;
    }

    public void click()
    {
        getWidget().click();
    }

    public void clickMenu( String... menuItemLabels )
    {
        SWTBotMenu menu = getWidget();

        for( String menuItemLabel : menuItemLabels )
        {
            menu = menu.menu( menuItemLabel ).click();
        }
    }

}
