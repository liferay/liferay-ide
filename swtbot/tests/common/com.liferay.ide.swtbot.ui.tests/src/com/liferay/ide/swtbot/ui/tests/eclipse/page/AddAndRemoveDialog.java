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
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TreeItemPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Li Lu
 * @author Terry Jia
 */
public class AddAndRemoveDialog extends DialogPO implements UIBase
{

    private ButtonPO _addAllButton;
    private ButtonPO _addButton;
    private ButtonPO _removeAllButton;
    private ButtonPO _removeButton;

    private TreePO _availableTree;
    private TreePO _configuredTree;

    public AddAndRemoveDialog( SWTBot bot )
    {
        super( bot, MENU_ADD_AND_REMOVE, BUTTON_CANCEL, BUTTON_FINISH );

        _addButton = new ButtonPO( bot, BUTTON_ADD_PROJECT );
        _addAllButton = new ButtonPO( bot, BUTTON_ADD_ALL );
        _removeButton = new ButtonPO( bot, BUTTON_REMOVE_PROJECT );
        _removeAllButton = new ButtonPO( bot, BUTTON_REMOVE_ALL );

        _availableTree = new TreePO( bot, 0 );
        _configuredTree = new TreePO( bot, 1 );
    }

    public TreePO getAvailableTree()
    {
        return _availableTree;
    }

    public TreePO getConfiguredTree()
    {
        return _configuredTree;
    }

    public void add( String... projectItemNames )
    {
        for( String projectItemName : projectItemNames )
        {
            TreeItemPO projectTree = new TreeItemPO( bot, _availableTree, projectItemName );

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
            TreeItemPO projectTree = new TreeItemPO( bot, _configuredTree, projectItemName );
            projectTree.select();

            _removeButton.click();
        }
    }

    public void removeAll()
    {
        _removeAllButton.click();
    }

}
