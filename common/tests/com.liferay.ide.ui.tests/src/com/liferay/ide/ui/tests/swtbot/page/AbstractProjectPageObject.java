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

package com.liferay.ide.ui.tests.swtbot.page;

import com.liferay.ide.ui.tests.ProjectWizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public abstract class AbstractProjectPageObject<T extends SWTBot> extends WizardPageObject<T> implements ProjectWizard
{

    CheckBoxPageObject<SWTBot> addToWorkingSetCheckbox;
    TextPageObject<SWTBot> projectNameText;
    ComboBoxPageObject<SWTBot> workingSetCombobox;

    public AbstractProjectPageObject( T bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public AbstractProjectPageObject( T bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public AbstractProjectPageObject( T bot, String title, int index )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, index );

        projectNameText = new TextPageObject<SWTBot>( bot, LABEL_PROJECT_NAME );
        addToWorkingSetCheckbox = new CheckBoxPageObject<SWTBot>( bot, TEXT_ADD_PROJECT_TO_WORKING_SET );
        workingSetCombobox = new ComboBoxPageObject<SWTBot>( bot, TEXT_WORKING_SET );
    }

    public void createProject( String projectName )
    {
        createProject( projectName, false, TEXT_BLANK );
    }

    public void createProject( String projectName, boolean workingSetBox, String workingSet )
    {

        projectNameText.setText( projectName );

        if( workingSetBox )
        {
            addToWorkingSetCheckbox.select();

            workingSetCombobox.setSelection( workingSet );
        }

    }

}
