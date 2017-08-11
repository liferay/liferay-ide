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

import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewServerWizard extends Wizard
{

    private Text serverHostName;
    private Text serverName;
    private Tree serverTypes;

    public NewServerWizard( SWTBot bot )
    {
        super( bot, NEW_SERVER, 3 );

        serverTypes = new Tree( bot );
        serverHostName = new Text( bot, SERVERS_HOST_NAME );
        serverName = new Text( bot, SERVER_NAME );
    }

    public Text getServerHostName()
    {
        return serverHostName;
    }

    public Text getServerName()
    {
        return serverName;
    }

    public Tree getServerTypes()
    {
        return serverTypes;
    }

}
