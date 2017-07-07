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

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.UIBase;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewServerWizard extends WizardPO implements UIBase
{

    private TreePO _serverTypes;

    private TextPO _serverHostName;
    private TextPO _serverName;

    public NewServerWizard( SWTBot bot )
    {
        this( bot, CHOOSE_SERVER_TYPE_INDEX );
    }

    public NewServerWizard( SWTBot bot, int validationMessageIndex )
    {
        super( bot, TITLE_NEW_SERVER, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        _serverTypes = new TreePO( bot );

        _serverHostName = new TextPO( bot, LABEL_SERVER_HOST_NAME );
        _serverName = new TextPO( bot, LABEL_SERVER_NAME );
    }

    public TreePO getServerTypes()
    {
        return _serverTypes;
    }

    public void selectServerType( String categroy, String item )
    {
        _serverTypes.selectTreeItem( categroy, item );
    }

    public TextPO getServerHostName()
    {
        return _serverHostName;
    }

    public void setServerHostName( TextPO serverHostName )
    {
        _serverHostName = serverHostName;
    }

    public TextPO getServerName()
    {
        return _serverName;
    }

    public void setServerName( TextPO serverName )
    {
        _serverName = serverName;
    }

}
