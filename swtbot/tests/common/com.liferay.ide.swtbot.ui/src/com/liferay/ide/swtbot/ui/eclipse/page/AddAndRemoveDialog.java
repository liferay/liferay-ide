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

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.TreeItem;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 * @author Terry Jia
 */
public class AddAndRemoveDialog extends Dialog
{

    private Button addAllBtn;
    private Button addBtn;
    private Tree availables;
    private Tree configureds;
    private Button removeAllBtn;
    private Button removeBtn;

    public AddAndRemoveDialog( SWTBot bot )
    {
        super( bot, ADD_AND_REMOVE, CANCEL, FINISH );

        addBtn = new Button( bot, ADD_WITH_BRACKET );
        addAllBtn = new Button( bot, ADD_ALL );
        removeBtn = new Button( bot, REMOVE_PROJECT );
        removeAllBtn = new Button( bot, REMOVE_ALL );
        availables = new Tree( bot, 0 );
        configureds = new Tree( bot, 1 );
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

    public Button getAddAllBtn()
    {
        return addAllBtn;
    }

    public Button getAddBtn()
    {
        return addBtn;
    }

    public Tree getAvailables()
    {
        return availables;
    }

    public Tree getConfigureds()
    {
        return configureds;
    }

    public Button getRemoveAllBtn()
    {
        return removeAllBtn;
    }

    public Button getRemoveBtn()
    {
        return removeBtn;
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

}
