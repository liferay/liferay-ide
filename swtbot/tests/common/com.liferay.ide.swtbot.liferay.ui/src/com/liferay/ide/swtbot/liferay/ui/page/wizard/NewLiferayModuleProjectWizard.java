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

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ying Xu
 * @author Sunny Shi
 * @author Ashley Yuan
 */
public class NewLiferayModuleProjectWizard extends Wizard
{

    private ComboBox buildTypes;
    private Text location;
    private Text projectName;
    private ComboBox projectTemplateNames;
    private CheckBox useDefaultLocation;

    public NewLiferayModuleProjectWizard( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public NewLiferayModuleProjectWizard( SWTBot bot, int validationMsgIndex )
    {
        this( bot, TEXT_BLANK, validationMsgIndex );
    }

    public NewLiferayModuleProjectWizard( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewLiferayModuleProjectWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );

        projectTemplateNames = new ComboBox( bot, "Project Template Name:" );
        projectName = new Text( bot, LABEL_PROJECT_NAME );
        useDefaultLocation = new CheckBox( bot, "Use default location" );
        location = new Text( bot, LABEL_LOCATION );
        buildTypes = new ComboBox( bot, LABEL_BUILD_TYPE );
    }

    public void createModuleProject( String text )
    {
        projectName.setText( text );
    }

    public void createModuleProject( String text, String projectTemplate )
    {
        projectTemplateNames.setSelection( projectTemplate );
        projectName.setText( text );
    }

    public void createModuleProject( String text, String projectTemplate, String buildType )
    {
        projectTemplateNames.setSelection( projectTemplate );
        projectName.setText( text );
        buildTypes.setSelection( buildType );
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

    public ComboBox getProjectTemplateNames()
    {
        return projectTemplateNames;
    }

    public CheckBox getUseDefaultLocation()
    {
        return useDefaultLocation;
    }

}
