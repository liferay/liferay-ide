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

package com.liferay.ide.server.core.tests.swtbot.pages;

import com.liferay.ide.server.core.tests.swtbot.ServerAction;
import com.liferay.ide.ui.tests.swtbot.page.ButtonPageObject;
import com.liferay.ide.ui.tests.swtbot.page.DialogPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TreeItemPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 * @author Terry Jia
 */
public class AddAndRemoveProjectPageObject<T extends SWTBot> extends DialogPageObject<T> implements ServerAction
{

    ButtonPageObject<SWTBot> addButton;
    ButtonPageObject<SWTBot> addAllButton;
    ButtonPageObject<SWTBot> removeButton;
    ButtonPageObject<SWTBot> removeAllButton;

    public AddAndRemoveProjectPageObject( T bot )
    {
        this( bot, ADD_AND_REMOVE, BUTTON_CANCEL, BUTTON_FINISH );
    }

    public AddAndRemoveProjectPageObject( T bot, String title, String cancelButtonText, String confirmButtonText )
    {
        super( bot, title, cancelButtonText, confirmButtonText );

        addButton = new ButtonPageObject<SWTBot>( bot, BUTTON_ADD );
        addAllButton = new ButtonPageObject<SWTBot>( bot, BUTTON_ADD_ALL );
        removeButton = new ButtonPageObject<SWTBot>( bot, BUTTON_REMOVE );
        removeAllButton = new ButtonPageObject<SWTBot>( bot, BUTTON_REMOVE_ALL );
    }

    public void add( String... projectItemNames )
    {
        for( String projectItemName : projectItemNames )
        {
            TreeItemPageObject<SWTBot> projectTree = new TreeItemPageObject<SWTBot>( bot, projectItemName );

            projectTree.select();

            addButton.click();
        }
    }

    public void addAll()
    {
        addAllButton.click();
    }

    public void remove( String... projectItemNames )
    {
        for( String projectItemName : projectItemNames )
        {
            TreeItemPageObject<SWTBot> projectTree = new TreeItemPageObject<SWTBot>( bot, projectItemName );
            projectTree.select();

            removeButton.click();
        }
    }

    public void removeAll()
    {
        removeAllButton.click();
    }

}
