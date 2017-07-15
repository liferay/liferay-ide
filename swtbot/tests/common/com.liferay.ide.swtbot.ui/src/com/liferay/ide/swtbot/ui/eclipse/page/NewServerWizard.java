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

package com.liferay.ide.swtbot.ui.eclipse.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.UI;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewServerWizard extends Wizard implements UI
{

    private Tree _serverTypes;

    private Text _serverHostName;
    private Text _serverName;

    public NewServerWizard( SWTBot bot )
    {
        this( bot, CHOOSE_SERVER_TYPE_INDEX );
    }

    public NewServerWizard( SWTBot bot, int validationMsgIndex )
    {
        super( bot, TITLE_NEW_SERVER, validationMsgIndex );

        _serverTypes = new Tree( bot );

        _serverHostName = new Text( bot, LABEL_SERVER_HOST_NAME );
        _serverName = new Text( bot, LABEL_SERVER_NAME );
    }

    public Tree getServerTypes()
    {
        return _serverTypes;
    }

    public void selectServerType( String categroy, String item )
    {
        _serverTypes.selectTreeItem( categroy, item );
    }

    public Text getServerHostName()
    {
        return _serverHostName;
    }

    public void setServerHostName( Text serverHostName )
    {
        _serverHostName = serverHostName;
    }

    public Text getServerName()
    {
        return _serverName;
    }

    public void setServerName( Text serverName )
    {
        _serverName = serverName;
    }

}
