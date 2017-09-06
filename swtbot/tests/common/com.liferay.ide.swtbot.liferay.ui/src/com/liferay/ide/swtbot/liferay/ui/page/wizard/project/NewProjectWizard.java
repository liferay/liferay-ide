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

package com.liferay.ide.swtbot.liferay.ui.page.wizard.project;

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class NewProjectWizard extends Wizard
{

    private final CheckBox addToWorkingSet;
    private final ComboBox buildTypes;
    private final Text location;
    private final Text projectName;
    private final CheckBox useDefaultLocation;
    private final ComboBox workingSets;

    public NewProjectWizard( final SWTWorkbenchBot bot )
    {
        this( bot, -1 );
    }

    public NewProjectWizard( final SWTWorkbenchBot bot, int index )
    {
        this( bot, StringPool.BLANK, index );
    }

    public NewProjectWizard( final SWTWorkbenchBot bot, String title, int index )
    {
        super( bot, title, index );

        projectName = new Text( bot, PROJECT_NAME );
        addToWorkingSet = new CheckBox( bot, ADD_PROJECT_TO_WORKING_SET );
        workingSets = new ComboBox( bot, WORKING_SET );
        location = new Text( bot, LOCATION_WITH_COLON );
        useDefaultLocation = new CheckBox( bot, USE_DEFAULT_LOCATION );
        buildTypes = new ComboBox( bot, BUILD_TYPE );
    }

    public void createProject( String projectName )
    {
        createProject( projectName, false, StringPool.BLANK );
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

    public ComboBox getBuildTypes()
    {
        return buildTypes;
    }

    public Text getLocation()
    {
        return location;
    }

    public Text getProjectName()
    {
        return projectName;
    }

    public CheckBox getUseDefaultLocation()
    {
        return useDefaultLocation;
    }

    public ComboBox getWorkingSets()
    {
        return workingSets;
    }

}
