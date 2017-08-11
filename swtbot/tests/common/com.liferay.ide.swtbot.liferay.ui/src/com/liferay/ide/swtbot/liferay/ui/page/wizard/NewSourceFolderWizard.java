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

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class NewSourceFolderWizard extends Wizard
{

    private Text folderName;
    private CheckBox ignoreOptionCompileProblems;
    private Text projectName;
    private CheckBox updateExclusionFilters;

    public NewSourceFolderWizard( SWTBot bot )
    {
        super( bot, 2);

        projectName = new Text( bot, PROJECT_NAME );
        folderName = new Text( bot, FOLDER_NAME );
        updateExclusionFilters = new CheckBox( bot, UPDATE_EXCLUSION_FILTERS );
        ignoreOptionCompileProblems = new CheckBox( bot, IGNORE_OPTIONAL_COMPILE_PROBLEMS );
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
        newSourceFolder( StringPool.BLANK, folderName, false, false );
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
