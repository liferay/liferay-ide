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

import com.liferay.ide.swtbot.liferay.ui.DialogUI;
import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Vicky Wang
 */
public class LanguagePropertiesWizard extends Wizard implements DialogUI, WizardUI
{

    private Button addBtn;
    private Button browseBtn;
    private Text contentFolder;
    private Button editBtn;
    private Table languagePropertyFiles;

    private Button removeBtn;

    public LanguagePropertiesWizard( SWTBot bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public LanguagePropertiesWizard( SWTBot bot, String title, int index )
    {
        super( bot, title, index );

        contentFolder = new Text( bot, LABLE_CONTENT_FOLDER );
        languagePropertyFiles = new Table( bot, LABLE_LANGUAGE_PROPERTY_FILES );

        browseBtn = new Button( bot, BROWSE_WITH_THREE_DOT );
        addBtn = new Button( bot, ADD_WITH_THREE_DOT );
        editBtn = new Button( bot, BUTTON_EDIT );
        removeBtn = new Button( bot, REMOVE_WITH_THREE_DOT );
    }

    public Button getAddBtn()
    {
        return addBtn;
    }

    public Button getBrowseBtn()
    {
        return browseBtn;
    }

    public Text getContentFolder()
    {
        return contentFolder;
    }

    public Button getEditBtn()
    {
        return editBtn;
    }

    public Table getLanguagePropertyFiles()
    {
        return languagePropertyFiles;
    }

    public Button getRemoveBtn()
    {
        return removeBtn;
    }

}
