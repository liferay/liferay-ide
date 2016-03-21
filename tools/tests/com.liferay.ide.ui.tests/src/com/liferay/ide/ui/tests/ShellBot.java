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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class ShellBot extends Bot
{

    public ShellBot( SWTWorkbenchBot bot )
    {
        super( bot );
    }

    public void close()
    {
        bot.activeShell().close();
    }

    public SWTBotShell shell( String shellName )
    {
        sleep();
        return bot.shell( shellName );
    }
}
