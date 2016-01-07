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

package com.liferay.ide.hook.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.hook.ui.tests.HookConfigurationWizard;
import com.liferay.ide.project.ui.tests.page.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.ButtonPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TablePageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

/**
 * @author Vicky Wang
 */
public class LanguagePropertiesPageObject<T extends SWTBot> extends WizardPageObject<T>
    implements HookConfigurationWizard, ProjectWizard
{

    TextPageObject<SWTBot> contentFolder;

    TablePageObject<SWTBot> languagePropertyFiles;

    ButtonPageObject<SWTBot> browse;
    ButtonPageObject<SWTBot> add;
    ButtonPageObject<SWTBot> edit;
    ButtonPageObject<SWTBot> remove;

    public LanguagePropertiesPageObject( T bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public LanguagePropertiesPageObject( T bot, String title, int index )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, index );

        contentFolder = new TextPageObject<SWTBot>( bot, LABLE_CONTENT_FOLDER );
        languagePropertyFiles = new TablePageObject<SWTBot>( bot, LABLE_LANGUAGE_PROPERTY_FILES );

        browse = new ButtonPageObject<SWTBot>( bot, BUTTON_BROWSE );
        add = new ButtonPageObject<SWTBot>( bot, BUTTON_ADD );
        edit = new ButtonPageObject<SWTBot>( bot, BUTTON_EDIT );
        remove = new ButtonPageObject<SWTBot>( bot, BUTTON_REMOVE );
    }

    public void clickLanguagePropertyFiles( int row )
    {
        languagePropertyFiles.click( row, 0 );
    }

    public ButtonPageObject<SWTBot> getAdd()
    {
        return add;
    }

    public ButtonPageObject<SWTBot> getEdit()
    {
        return edit;
    }

    public ButtonPageObject<SWTBot> getRemove()
    {
        return remove;
    }

    public ButtonPageObject<SWTBot> getBrowse()
    {
        return browse;
    }

    public String getContentFolderText()
    {
        return contentFolder.getText();
    }

    public void setContentFolderText( String text )
    {
        this.contentFolder.setText( text );
    }

}
