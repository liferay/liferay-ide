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
import com.liferay.ide.swtbot.ui.tests.page.TreePO;
import com.liferay.ide.swtbot.ui.tests.page.ViewPO;

/**
 * @author Terry Jia
 */
public class ServersView extends ViewPO implements UIBase
{

    private ToolbarButtonWithTooltipPO _debug;
    private TreePO _servers;
    private ToolbarButtonWithTooltipPO _start;
    private ToolbarButtonWithTooltipPO _stop;

    public ServersView( SWTWorkbenchBot bot )
    {
        super( bot, LABEL_SERVERS );

        _start = new ToolbarButtonWithTooltipPO( bot, LEBAL_SERVER_START_BUTTON );
        _stop = new ToolbarButtonWithTooltipPO( bot, LEBAL_SERVER_STOP_BUTTON );
        _debug = new ToolbarButtonWithTooltipPO( bot, LEBAL_SERVER_DEBUG_BUTTON );

        _servers = new TreePO( bot, 1 );
    }

    public ToolbarButtonWithTooltipPO getDebug()
    {
        return _debug;
    }

    public ToolbarButtonWithTooltipPO getStart()
    {
        return _start;
    }

    public ToolbarButtonWithTooltipPO getStop()
    {
        return _stop;
    }

    public TreePO getServers()
    {
        return _servers;
    }

}
