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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class ViewPO extends AbstractPO
{

    private final String identifier;

    private final boolean isId;

    private ToolbarButtonWithTooltipPO maximizeButton;
    private ToolbarButtonWithTooltipPO minimizeButton;
    private ToolbarButtonWithTooltipPO restoreButton;

    public ViewPO( SWTWorkbenchBot bot, String identifier, boolean isId )
    {
        super( bot );

        this.identifier = identifier;

        this.isId = isId;

        maximizeButton = new ToolbarButtonWithTooltipPO( bot, "Maximize" );
        minimizeButton = new ToolbarButtonWithTooltipPO( bot, "Minimize" );
        restoreButton = new ToolbarButtonWithTooltipPO( bot, "Restore" );
    }

    public ViewPO( SWTWorkbenchBot bot, String viewIdentifier )
    {
        this( bot, viewIdentifier, false );
    }

    public void close()
    {
        getView().close();
    }

    protected SWTBotView getView()
    {
        if( isId )
        {
            return ( (SWTWorkbenchBot) bot ).viewById( identifier );
        }
        else
        {
            return ( (SWTWorkbenchBot) bot ).viewByTitle( identifier );
        }
    }

    public void maximize()
    {
        getView().setFocus();
        maximizeButton.click();
    }

    public void minimize()
    {
        getView().setFocus();
        minimizeButton.click();
    }

    public void restore()
    {
        getView().setFocus();
        restoreButton.click();
    }

    public void show()
    {
        getView().show();
    }

    public boolean isActive()
    {
        return getView().isActive();
    }

}
