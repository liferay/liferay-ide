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

package com.liferay.ide.swtbot.ui.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class Menu extends AbstractWidget
{

    private String[] labels;

    public Menu( final SWTWorkbenchBot bot, final String label )
    {
        super( bot, label );
    }

    public Menu( final SWTWorkbenchBot bot, final String[] labels )
    {
        super( bot );

        this.labels = labels;
    }

    @Override
    protected SWTBotMenu getWidget()
    {
        if( !isLabelNull() )
        {
            return bot.menu( label );
        }

        SWTBotMenu menu = bot.menu( labels[0] );

        for( int i = 1; i < labels.length; i++ )
        {
            menu = menu.menu( labels[i] );
        }

        return menu;
    }

    public void click()
    {
        getWidget().click();
    }

    public void clickMenu( final String... menus )
    {
        SWTBotMenu menu = getWidget();

        for( String menuItemLabel : menus )
        {
            menu = menu.menu( menuItemLabel ).click();
        }
    }

}
