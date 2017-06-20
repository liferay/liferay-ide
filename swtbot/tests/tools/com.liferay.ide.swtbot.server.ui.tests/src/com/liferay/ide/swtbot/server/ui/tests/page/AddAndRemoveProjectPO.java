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

import com.liferay.ide.swtbot.server.ui.tests.ServerAction;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TreeItemPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Li Lu
 * @author Terry Jia
 */
public class AddAndRemoveProjectPO extends DialogPO implements ServerAction
{

    private ButtonPO _addAllButton;
    private ButtonPO _addButton;
    private ButtonPO _removeAllButton;
    private ButtonPO _removeButton;

    public AddAndRemoveProjectPO( SWTBot bot )
    {
        this( bot, MENU_ADD_AND_REMOVE, BUTTON_CANCEL, BUTTON_FINISH );
    }

    public AddAndRemoveProjectPO( SWTBot bot, String title, String cancelButtonText, String confirmButtonText )
    {
        super( bot, title, cancelButtonText, confirmButtonText );

        _addButton = new ButtonPO( bot, BUTTON_ADD_PROJECT );
        _addAllButton = new ButtonPO( bot, BUTTON_ADD_ALL );
        _removeButton = new ButtonPO( bot, BUTTON_REMOVE_PROJECT );
        _removeAllButton = new ButtonPO( bot, BUTTON_REMOVE_ALL );
    }

    public void add( String... projectItemNames )
    {
        for( String projectItemName : projectItemNames )
        {
            TreeItemPO projectTree = new TreeItemPO( bot, new TreePO( bot ), projectItemName );

            projectTree.select();

            _addButton.click();
        }
    }

    public void addAll()
    {
        _addAllButton.click();
    }

    public void remove( String... projectItemNames )
    {
        for( String projectItemName : projectItemNames )
        {
            TreeItemPO projectTree = new TreeItemPO( bot, new TreePO( bot ), projectItemName );
            projectTree.select();

            _removeButton.click();
        }
    }

    public void removeAll()
    {
        _removeAllButton.click();
    }

}
