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

package com.liferay.ide.swtbot.portlet.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ashley Yuan
 */
public class NewSourceFolderPO extends WizardPO implements ProjectWizard
{

    private TextPO _projectNameText;
    private TextPO _folderNameText;
    private CheckBoxPO _updateExclusionFiltersCheckBox;
    private CheckBoxPO _ignoreOptionCompileProblemsCheckBox;

    public NewSourceFolderPO( SWTBot bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewSourceFolderPO( SWTBot bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, validationMessageIndex );
    }

    public NewSourceFolderPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewSourceFolderPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        _projectNameText = new TextPO( bot, LABEL_PROJECT_NAME );
        _folderNameText = new TextPO( bot, LABEL_FOLDER_NAME );
        _updateExclusionFiltersCheckBox = new CheckBoxPO( bot, LABEL_UPDATE_EXCLUSION_FILTERS );
        _ignoreOptionCompileProblemsCheckBox = new CheckBoxPO( bot, LABEL_IGNORE_OPTIONAL_COMPILE_PROBLEMS );
    }

    public String getFolderNameText()
    {
        return _folderNameText.getText();
    }

    public String getProjectNameText()
    {
        return _projectNameText.getText();
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
            _projectNameText.setText( projectName );
        }

        _folderNameText.setText( folderName );

        if( updateExclusionFilters )
        {
            _updateExclusionFiltersCheckBox.select();
        }

        if( IgnoreOptionCompileProblems )
        {
            _ignoreOptionCompileProblemsCheckBox.select();
        }
    }

}
