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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ashley Yuan
 */
public class SelectTypeWizard extends Wizard implements WizardUI
{

    private Text filterName;

    public SelectTypeWizard( SWTBot bot, int validationMsgIndex )
    {
        this( bot, TEXT_BLANK, validationMsgIndex );
    }

    public SelectTypeWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );

        filterName = new Text( bot, TEXT_BLANK );
    }

    public void selectItem( String selectTypeTree, String selectTypeNode )
    {
        selectItem( TEXT_BLANK, selectTypeTree, selectTypeNode );
    }

    public void selectItem( String filterText, String selectTypeTree, String selectTypeNode )
    {
        filterName.setText( filterText );

        sleep();

        bot.tree().getTreeItem( selectTypeTree ).getNode( selectTypeNode ).select();
    }
}
