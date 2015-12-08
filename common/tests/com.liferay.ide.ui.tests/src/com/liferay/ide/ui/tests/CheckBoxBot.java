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

package com.liferay.ide.ui.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class CheckBoxBot extends Bot
{

    public CheckBoxBot( SWTWorkbenchBot bot )
    {
        super( bot );
    }

    public void click()
    {
        bot.checkBox().click();
        sleep();
    }

    public void click( int index )
    {
        bot.checkBox( index ).click();
        sleep();
    }

    public void click( String mnemonicText )
    {
        bot.checkBox( mnemonicText ).click();
        sleep();
    }

    public void deSelect( int index )
    {
        bot.checkBox( index ).deselect();
        sleep();
    }

    public void deSelect( String mnemonicText )
    {
        bot.checkBox( mnemonicText ).deselect();
        sleep();
    }

    public boolean isChecked( int index )
    {
        return bot.checkBox( index ).isChecked();
    }

    public boolean isChecked( String mnemonicText )
    {
        return bot.checkBox( mnemonicText ).isChecked();
    }

    public void select( int index )
    {
        bot.checkBox( index ).select();
    }

    public void select( String mnemonicText )
    {
        bot.checkBox( mnemonicText ).select();
    }

}
