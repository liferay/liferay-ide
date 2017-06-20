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

package com.liferay.ide.swtbot.server.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.server.ui.tests.ServerRuntimeWizard;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarButtonWithTooltipPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewServerPO extends WizardPO implements ServerRuntimeWizard
{

    TreePO _serverTypeTree;
    TextPO _serverHostName;
    TextPO _serverName;
    ToolbarButtonWithTooltipPO _resetDefault;

    public NewServerPO( SWTBot bot )
    {
        this( bot, CHOOSE_SERVER_TYPE_INDEX );
    }

    public NewServerPO( SWTBot bot, int validationMessageIndex )
    {
        super( bot, TITLE_NEW_SERVER, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        _serverTypeTree = new TreePO( bot );
        _serverHostName = new TextPO( bot, LABEL_SERVER_HOST_NAME );
        _serverName = new TextPO( bot, LABEL_SERVER_NAME );
        _resetDefault = new ToolbarButtonWithTooltipPO( bot, BUTTON_RESET_DEFAULT );
    }

    public TreePO getServerTypeTree()
    {
        return _serverTypeTree;
    }

    public TextPO getServerHostName()
    {
        return _serverHostName;
    }

    public void setServerHostName( TextPO serverHostName )
    {
        this._serverHostName = serverHostName;
    }

    public TextPO getServerName()
    {
        return _serverName;
    }

    public void setServerName( TextPO serverName )
    {
        this._serverName = serverName;
    }

    public ToolbarButtonWithTooltipPO getResetDefault()
    {
        return _resetDefault;
    }

}
