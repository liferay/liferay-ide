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
import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.TreeItem;

/**
 * @author Li Lu
 * @author Terry Jia
 */
public class AddAndRemoveDialog extends Dialog implements UI
{

    private Button addAllBtn;
    private Button addBtn;
    private Button removeAllBtn;
    private Button removeBtn;

    private Tree availables;
    private Tree configureds;

    public AddAndRemoveDialog( SWTBot bot )
    {
        super( bot, MENU_ADD_AND_REMOVE, CANCEL, FINISH );

        addBtn = new Button( bot, ADD_WITH_BRACKET );
        addAllBtn = new Button( bot, ADD_ALL_WITH_TWO_BRACKETS );
        removeBtn = new Button( bot, REMOVE_PROJECT );
        removeAllBtn = new Button( bot, REMOVE_ALL );

        availables = new Tree( bot, 0 );
        configureds = new Tree( bot, 1 );
    }

    public Tree getAvailableTree()
    {
        return availables;
    }

    public Tree getConfiguredTree()
    {
        return configureds;
    }

    public void add( String... projectItemNames )
    {
        for( String projectItemName : projectItemNames )
        {
            TreeItem projectTree = new TreeItem( bot, availables, projectItemName );

            projectTree.select();

            addBtn.click();
        }
    }

    public void addAll()
    {
        addAllBtn.click();
    }

    public void remove( String... projectItemNames )
    {
        for( String projectItemName : projectItemNames )
        {
            TreeItem projectTree = new TreeItem( bot, configureds, projectItemName );
            projectTree.select();

            removeBtn.click();
        }
    }

    public void removeAll()
    {
        removeAllBtn.click();
    }

}
