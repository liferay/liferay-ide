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

package com.liferay.ide.swtbot.ui.tests.eclipse.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import com.liferay.ide.swtbot.ui.tests.UIBase;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarButtonWithTooltipPO;
import com.liferay.ide.swtbot.ui.tests.page.ViewPO;

/**
 * @author Terry Jia
 */
public class ErrorLogViewPO extends ViewPO implements UIBase
{

    private ToolbarButtonWithTooltipPO clearLogViewerToolbar;

    public ErrorLogViewPO( SWTWorkbenchBot bot )
    {
        super( bot, LABEL_ERROR_LOG_FULL );

        clearLogViewerToolbar = new ToolbarButtonWithTooltipPO( bot, LABEL_CLEAR_LOG_VIEWER );
    }

    public void clearLogViewer()
    {
        if( hasProblems() )
        {
            clearLogViewerToolbar.click();
        }
    }

    public boolean hasProblems()
    {
        // return( problemTree.getAllItems().length > 0 );
        return true;
    }

}
