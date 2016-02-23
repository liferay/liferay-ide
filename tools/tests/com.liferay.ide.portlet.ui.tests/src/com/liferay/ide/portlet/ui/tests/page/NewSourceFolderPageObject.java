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
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;

import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class NewSourceFolderPageObject<T extends SWTBot> extends WizardPageObject<T> implements ProjectWizard
{

    TextPageObject<SWTBot> projectNameText;
    TextPageObject<SWTBot> folderNameText;

    CheckBoxPageObject<SWTBot> updateExclusionFiltersBox;
    CheckBoxPageObject<SWTBot> ignoreOptionCompileProblemsBox;

    public NewSourceFolderPageObject( T bot )
    {
        this( bot, TEXT_BLANK, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewSourceFolderPageObject( T bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, validationMessageIndex );
    }

    public NewSourceFolderPageObject( T bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewSourceFolderPageObject( T bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        projectNameText = new TextPageObject<>( bot, LABEL_PROJECT_NAME );
        folderNameText = new TextPageObject<>( bot, LABEL_FOLDER_NAME );
        updateExclusionFiltersBox = new CheckBoxPageObject<>( bot, LABEL_UPDATE_EXCLUSION_FILTERS );
        ignoreOptionCompileProblemsBox = new CheckBoxPageObject<>( bot, LABEL_IGNORE_OPTIONAL_COMPILE_PROBLEMS );
    }

    public String getFolderNameText()
    {
        return folderNameText.getText();
    }

    public String getProjectNameText()
    {
        return projectNameText.getText();
    }

    public void newSourceFolder( String folderName )
    {
        newSourceFolder( TEXT_BLANK, folderName, false, false );
    }

    public void newSourceFolder(
        String projectName, String folderName, boolean updateExclusionFilters, boolean IgnoreOptionCompileProblems )
    {
        if( projectName != null && !projectName.equals( "" ) )
        {
            projectNameText.setText( projectName );
        }
        folderNameText.setText( folderName );
        if( updateExclusionFilters )
        {
            updateExclusionFiltersBox.select();
        }
        if( IgnoreOptionCompileProblems )
        {
            ignoreOptionCompileProblemsBox.select();
        }
    }
}
