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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;

/**
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ToolbarButtonWithTooltipPO extends AbstractWidgetPO
{

    public ToolbarButtonWithTooltipPO( SWTBot bot, String label )
    {
        super( bot, label );
    }

    public ToolbarButtonWithTooltipPO( SWTBot bot, String label, int index )
    {
        super( bot, label, index );
    }

    @Override
    protected SWTBotToolbarButton getWidget()
    {
        if( index > 0 )
        {
            return bot.toolbarButtonWithTooltip( label, index );
        }
        else
        {
            return bot.toolbarButtonWithTooltip( label );
        }
    }

    public void click()
    {
        getWidget().click();
    }

}
