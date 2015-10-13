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

package com.liferay.ide.project.ui.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.BeforeClass;

import com.liferay.ide.project.core.tests.ProjectCoreBase;

/**
 * @author Li Lu
 */
public class SWTBotTestBase extends ProjectCoreBase
{
    public static final String BUNDLE_ID = "com.liferay.ide.project.ui.tests";
    protected static final SWTWorkbenchBot bot = new SWTWorkbenchBot();

    @BeforeClass
    public static void closeWelcomeSwitchToLiferay()
    {
        try
        {
            bot.viewByTitle( "Welcome" ).close();
        }
        catch( Exception e1 )
        {
        }

        try
        {
            bot.toolbarButtonWithTooltip( "Open Perspective" ).click();
            bot.table().select( "Liferay" );

            bot.button( "OK" ).click();
        }
        catch( Exception e )
        {
        }
    }
}
