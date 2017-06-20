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

package com.liferay.ide.swtbot.layouttpl.ui.tests.pages;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.page.AbstractSelectionPO;

/**
 * @author Li Lu
 */
public class TempalteSelectionDialogPO extends AbstractSelectionPO
{

    public TempalteSelectionDialogPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public TempalteSelectionDialogPO( SWTBot bot, String title )
    {
        super( bot, title );
    }

    public boolean canFinish()
    {
        return confirmButton().isEnabled();
    }

    public boolean containsItem( String... items )
    {
        return getSelcetFileTree().hasTreeItem( items );
    }

    public String getValidationMessage()
    {
        return bot.clabel().getText();
    }

    public void select( String... items )
    {
        getSelcetFileTree().selectTreeItem( items );
    }

}
