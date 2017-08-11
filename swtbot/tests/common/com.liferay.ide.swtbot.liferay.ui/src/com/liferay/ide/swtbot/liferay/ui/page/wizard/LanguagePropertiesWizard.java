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

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 */
public class LanguagePropertiesWizard extends Wizard
{

    private Button addBtn;
    private Button browseBtn;
    private Text contentFolder;
    private Button editBtn;
    private Table languagePropertyFiles;
    private Button removeBtn;

    public LanguagePropertiesWizard( SWTBot bot )
    {
        super( bot, NEW_LIFERAY_HOOK, 0 );

        contentFolder = new Text( bot, CONTENT_FOLDER );
        languagePropertyFiles = new Table( bot, LANGUAGE_PROPERTY_FILES );
        browseBtn = new Button( bot, BROWSE_WITH_DOT );
        addBtn = new Button( bot, ADD_WITH_DOT );
        editBtn = new Button( bot, EDIT_WITH_DOT );
        removeBtn = new Button( bot, REMOVE_WITH_DOT );
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
