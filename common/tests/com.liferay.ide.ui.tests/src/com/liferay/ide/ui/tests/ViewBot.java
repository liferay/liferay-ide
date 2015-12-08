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
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class ViewBot extends Bot
{

    public ViewBot( SWTWorkbenchBot bot )
    {
        super( bot );
    }

    public void close( String title )
    {
        bot.viewByTitle( title ).close();
    }

    public void show( String title )
    {
        bot.viewByTitle( title ).show();
    }
    
    public SWTBotView view( String title )
    {
        return bot.viewByTitle( title );
    }
}
