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

package com.liferay.ide.swtbot.liferay.ui.page.view;

import com.liferay.ide.swtbot.ui.page.Canvas;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class CodeUpgradeView extends View
{

    public class GearPO extends Canvas
    {

        int step = 64;
        int x = 52;
        int y = 52;

        public GearPO( SWTWorkbenchBot bot, int index )
        {
            super( bot, index );
        }

        public void clickGear( int i )
        {
            click( x + step * i, y );
            sleep();
        }

    }

    private GearPO gear;
    private Canvas navigator;
    private ToolbarButtonWithTooltip restartBtn;
    private Dialog restartDialog;
    private ToolbarButtonWithTooltip showAllPagesBtn;
    private Dialog showAllPagesDialog;

    public CodeUpgradeView( SWTWorkbenchBot bot, String viewIdentifier )
    {
        super( bot, viewIdentifier );

        gear = new GearPO( bot, 3 );
        navigator = new Canvas( bot, 4 );

        showAllPagesBtn = new ToolbarButtonWithTooltip( bot, SHOW_ALL_PAGES );
        restartBtn = new ToolbarButtonWithTooltip( bot, RESTART_UPGRADE );
        restartDialog = new Dialog( bot, RESTART_CODE_UPGRADE, NO, YES );
        showAllPagesDialog = new Dialog( bot, SHOW_ALL_PAGES, NO, YES );
    }

    public GearPO getGear()
    {
        return gear;
    }

    public Canvas getNavigator()
    {
        return navigator;
    }

    public void restartWithConfirm()
    {
        restartBtn.click();
        restartDialog.confirm();
    }

    public void showAllPagesWithConfirm()
    {
        showAllPagesBtn.click();
        showAllPagesDialog.confirm();
    }

}
