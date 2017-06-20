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

package com.liferay.ide.swtbot.project.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ashley Yuan
 */
public class SelectTypePO extends WizardPO implements ProjectWizard
{

    private TextPO _filterNameText;

    public SelectTypePO( SWTBot bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, validationMessageIndex );
    }

    public SelectTypePO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        _filterNameText = new TextPO( bot, TEXT_BLANK );
    }

    public void selectItem( String selectTypeTree, String selectTypeNode )
    {
        selectItem( TEXT_BLANK, selectTypeTree, selectTypeNode );
    }

    public void selectItem( String filterText, String selectTypeTree, String selectTypeNode )
    {
        _filterNameText.setText( filterText );

        sleep();

        bot.tree().getTreeItem( selectTypeTree ).getNode( selectTypeNode ).select();
    }
}
