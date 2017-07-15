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
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class BaseProjectWizard extends Wizard implements WizardUI
{

    private CheckBox addToWorkingSet;

    private Text projectName;

    private ComboBox workingSets;

    public BaseProjectWizard( SWTBot bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public BaseProjectWizard( SWTBot bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public BaseProjectWizard( SWTBot bot, String title, int index )
    {
        super( bot, title, index );

        projectName = new Text( bot, LABEL_PROJECT_NAME );
        addToWorkingSet = new CheckBox( bot, CHECKBOX_ADD_PROJECT_TO_WORKING_SET );
        workingSets = new ComboBox( bot, COMBOBOX_WORKING_SET );
    }

    public void createProject( String projectName )
    {
        createProject( projectName, false, TEXT_BLANK );
    }

    public void createProject( String name, boolean workingSetBox, String set )
    {
        projectName.setText( name );

        if( workingSetBox )
        {
            addToWorkingSet.select();

            workingSets.setSelection( set );
        }
    }

    public CheckBox getAddToWorkingSet()
    {
        return addToWorkingSet;
    }

    public Text getProjectName()
    {
        return projectName;
    }

    public ComboBox getWorkingSets()
    {
        return workingSets;
    }

}
