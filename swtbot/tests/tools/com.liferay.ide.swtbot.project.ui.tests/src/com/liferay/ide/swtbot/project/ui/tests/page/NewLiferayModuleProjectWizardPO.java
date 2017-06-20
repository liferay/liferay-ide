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

import org.eclipse.swtbot.swt.finder.SWTBot;
import com.liferay.ide.swtbot.project.ui.tests.NewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ying Xu
 * @author Sunny Shi
 * @author Ashley Yuan
 */
public class NewLiferayModuleProjectWizardPO extends WizardPO implements NewLiferayModuleProjectWizard
{

    private ComboBoxPO _projectTemplateNameComboBox;
    private ComboBoxPO _buildType;
    private TextPO _projectNameText;
    private CheckBoxPO _useDefaultLocation;
    private TextPO _location;

    public NewLiferayModuleProjectWizardPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public NewLiferayModuleProjectWizardPO( SWTBot bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, validationMessageIndex );
    }

    public NewLiferayModuleProjectWizardPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewLiferayModuleProjectWizardPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );
        _projectTemplateNameComboBox = new ComboBoxPO( bot, LABEL_MODULE_PROJECT_TEMPLATE_NAME );
        _projectNameText = new TextPO( bot, LABEL_PROJECT_NAME );
        _useDefaultLocation = new CheckBoxPO( bot, CHECKBOX_USE_DEFAULT_LOCATION );
        _location = new TextPO( bot, LABEL_LOCATION );
        _buildType = new ComboBoxPO( bot, LABEL_BUILD_TYPE );
    }

    public void createModuleProject( String projectName )
    {
        _projectNameText.setText( projectName );
    }

    public void createModuleProject( String projectName, String projectTemplate )
    {
        _projectTemplateNameComboBox.setSelection( projectTemplate );
        _projectNameText.setText( projectName );
    }

    public void createModuleProject( String projectName, String projectTemplate, String buildType )
    {
        _projectTemplateNameComboBox.setSelection( projectTemplate );
        _projectNameText.setText( projectName );
        _buildType.setSelection( buildType );
    }

    public ComboBoxPO getProjectTemplateNameComboBox()
    {
        return _projectTemplateNameComboBox;
    }

    public TextPO getProjectNameText()
    {
        return _projectNameText;
    }

    public CheckBoxPO get_useDefaultLocation()
    {
        return _useDefaultLocation;
    }

    public TextPO getLocation()
    {
        return _location;
    }

    public void setLocation( String location )
    {
        _location.setText( location );
    }

    public ComboBoxPO getBuildType()
    {
        return _buildType;
    }

    public void setBuildType( String buildType )
    {
        _buildType.setSelection( buildType );
    }

}
