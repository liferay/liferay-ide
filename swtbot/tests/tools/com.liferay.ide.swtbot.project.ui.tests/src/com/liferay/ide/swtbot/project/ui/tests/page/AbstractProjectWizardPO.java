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

package com.liferay.ide.swtbot.project.ui.tests.page;

import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public abstract class AbstractProjectWizardPO extends WizardPO implements ProjectWizard
{

    CheckBoxPO _addToWorkingSet;
    TextPO _projectName;
    ComboBoxPO _workingSet;

    public AbstractProjectWizardPO( SWTBot bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public AbstractProjectWizardPO( SWTBot bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public AbstractProjectWizardPO( SWTBot bot, String title, int index )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, index );

        _projectName = new TextPO( bot, LABEL_PROJECT_NAME );
        _addToWorkingSet = new CheckBoxPO( bot, CHECKBOX_ADD_PROJECT_TO_WORKING_SET );
        _workingSet = new ComboBoxPO( bot, COMBOBOX_WORKING_SET );
    }

    public void createProject( String projectName )
    {
        createProject( projectName, false, TEXT_BLANK );
    }

    public void createProject( String projectName, boolean workingSetBox, String workingSet )
    {
        _projectName.setText( projectName );

        if( workingSetBox )
        {
            _addToWorkingSet.select();

            _workingSet.setSelection( workingSet );
        }
    }

}
