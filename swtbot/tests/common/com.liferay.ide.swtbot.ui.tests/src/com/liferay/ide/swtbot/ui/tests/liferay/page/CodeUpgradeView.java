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

package com.liferay.ide.swtbot.ui.tests.liferay.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.UIBase;
import com.liferay.ide.swtbot.ui.tests.page.CanvasPO;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarButtonWithTooltipPO;
import com.liferay.ide.swtbot.ui.tests.page.ViewPO;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class CodeUpgradeView extends ViewPO implements UIBase
{

	public class GearPO extends CanvasPO
	{

	    int x = 52;
	    int y = 52;
	    int step = 64;

	    public GearPO( SWTBot bot, int index )
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
    private CanvasPO navigator;

    private ToolbarButtonWithTooltipPO showAllPagesButton;
    private ToolbarButtonWithTooltipPO restartButton;
    private DialogPO restartDialog;
    private DialogPO showAllPagesDialog;

    public CodeUpgradeView( SWTWorkbenchBot bot, String viewIdentifier )
    {
        super( bot, viewIdentifier );

        gear = new GearPO( bot, 3 );
        navigator = new CanvasPO( bot, 4 );

        showAllPagesButton = new ToolbarButtonWithTooltipPO( bot, BUTTON_SHOW_ALL_PAGES );
        restartButton = new ToolbarButtonWithTooltipPO( bot, BUTTON_RESTART_UPGRADE );
        restartDialog = new DialogPO( bot, LABEL_RESTART_CODE_UPGRADE , BUTTON_NO, BUTTON_YES );
        showAllPagesDialog = new DialogPO( bot, BUTTON_SHOW_ALL_PAGES, BUTTON_NO, BUTTON_YES );
    }

    public GearPO getGear()
    {
        return gear;
    }

    public CanvasPO getNavigator()
    {
        return navigator;
    }

    public void restartWithConfirm()
    {
        restartButton.click();
        restartDialog.confirm();
    }

    public void showAllPagesWithConfirm()
    {
        showAllPagesButton.click();
        showAllPagesDialog.confirm();
    }

}
