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
 * @author Terry Jia
 */
public class NewRuntimeWizard extends Wizard
{

    private Text search;
    private Tree serverTypes;

    public NewRuntimeWizard( SWTBot bot )
    {
        super( bot, NEW_SERVER_RUNTIME_ENVIRONMENT, 3 );

        search = new Text( bot );
        serverTypes = new Tree( bot );
    }

    public Text getSearch()
    {
        return search;
    }

    public Tree getServerTypes()
    {
        return serverTypes;
    }

    public void selectServerType( String category, String serverType )
    {
        serverTypes.getTreeItem( category ).getTreeItem( serverType ).select();
    }

}
