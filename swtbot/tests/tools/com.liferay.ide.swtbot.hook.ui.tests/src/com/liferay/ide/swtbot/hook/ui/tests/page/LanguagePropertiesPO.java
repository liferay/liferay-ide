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

package com.liferay.ide.swtbot.hook.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.hook.ui.tests.HookConfigurationWizard;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.TablePO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Vicky Wang
 */
public class LanguagePropertiesPO extends WizardPO implements HookConfigurationWizard, ProjectWizard
{

    private TextPO _contentFolderText;
    private TablePO _languagePropertyFilesText;
    private ButtonPO browseButton;
    private ButtonPO _addButton;
    private ButtonPO _editButton;
    private ButtonPO _removeButton;

    public LanguagePropertiesPO( SWTBot bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public LanguagePropertiesPO( SWTBot bot, String title, int index )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, index );

        _contentFolderText = new TextPO( bot, LABLE_CONTENT_FOLDER );
        _languagePropertyFilesText = new TablePO( bot, LABLE_LANGUAGE_PROPERTY_FILES );

        browseButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT );
        _addButton = new ButtonPO( bot, BUTTON_ADD );
        _editButton = new ButtonPO( bot, BUTTON_EDIT );
        _removeButton = new ButtonPO( bot, BUTTON_REMOVE_WITH_DOT );
    }

    public void clickLanguagePropertyFiles( int row )
    {
        _languagePropertyFilesText.click( row, 0 );
    }

    public ButtonPO getAddButton()
    {
        return _addButton;
    }

    public ButtonPO getEditButton()
    {
        return _editButton;
    }

    public ButtonPO getRemoveButton()
    {
        return _removeButton;
    }

    public ButtonPO getBrowseButton()
    {
        return browseButton;
    }

    public String getContentFolderText()
    {
        return _contentFolderText.getText();
    }

    public void setContentFolderText( String text )
    {
        this._contentFolderText.setText( text );
    }

}