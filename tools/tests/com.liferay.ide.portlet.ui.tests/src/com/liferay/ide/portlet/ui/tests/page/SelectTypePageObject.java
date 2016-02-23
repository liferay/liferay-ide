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

package com.liferay.ide.portlet.ui.tests.page;

import com.liferay.ide.ui.tests.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TreeItemPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class SelectTypePageObject<T extends SWTBot> extends WizardPageObject<T> implements ProjectWizard
{

    TextPageObject<SWTBot> filterNameText;

    TreeItemPageObject<SWTBot> selectTypeText;

    public SelectTypePageObject( T bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, validationMessageIndex );
    }

    public SelectTypePageObject( T bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        filterNameText = new TextPageObject<>( bot, TEXT_BLANK );
        selectTypeText = new TreeItemPageObject<>( bot );
    }

    public void createProject( String selectTypeTree, String selectTypeNode )
    {
        createProject( TEXT_BLANK, selectTypeTree, selectTypeNode );
    }

    public void createProject( String filterText, String selectTypeTree, String selectTypeNode )
    {
        filterNameText.setText( filterText );

        sleep();

        bot.tree().getTreeItem( selectTypeTree ).getNode( selectTypeNode ).select();
    }
}
