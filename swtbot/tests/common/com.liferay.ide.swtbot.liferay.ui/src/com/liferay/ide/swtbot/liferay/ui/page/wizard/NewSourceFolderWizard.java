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
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ashley Yuan
 */
public class NewSourceFolderWizard extends Wizard implements WizardUI
{

    private Text folderName;
    private CheckBox ignoreOptionCompileProblems;
    private Text projectName;
    private CheckBox updateExclusionFilters;

    public NewSourceFolderWizard( SWTBot bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewSourceFolderWizard( SWTBot bot, int validationMsgIndex )
    {
        this( bot, TEXT_BLANK, validationMsgIndex );
    }

    public NewSourceFolderWizard( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewSourceFolderWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );

        projectName = new Text( bot, LABEL_PROJECT_NAME );
        folderName = new Text( bot, LABEL_FOLDER_NAME );
        updateExclusionFilters = new CheckBox( bot, LABEL_UPDATE_EXCLUSION_FILTERS );
        ignoreOptionCompileProblems = new CheckBox( bot, LABEL_IGNORE_OPTIONAL_COMPILE_PROBLEMS );
    }

    public Text getFolderName()
    {
        return folderName;
    }

    public CheckBox getIgnoreOptionCompileProblems()
    {
        return ignoreOptionCompileProblems;
    }

    public Text getProjectName()
    {
        return projectName;
    }

    public CheckBox getUpdateExclusionFilters()
    {
        return updateExclusionFilters;
    }

    public void newSourceFolder( String folderName )
    {
        newSourceFolder( TEXT_BLANK, folderName, false, false );
    }

    public void newSourceFolder(
        String projectNameValue, String folderNameValue, boolean updateExclusionFiltersValue,
        boolean IgnoreOptionCompileProblemsValue )
    {
        if( projectName != null && projectName.equals( "" ) )
        {
            projectName.setText( projectNameValue );
        }

        folderName.setText( folderNameValue );

        if( updateExclusionFiltersValue )
        {
            updateExclusionFilters.select();
        }

        if( IgnoreOptionCompileProblemsValue )
        {
            ignoreOptionCompileProblems.select();
        }
    }

}
