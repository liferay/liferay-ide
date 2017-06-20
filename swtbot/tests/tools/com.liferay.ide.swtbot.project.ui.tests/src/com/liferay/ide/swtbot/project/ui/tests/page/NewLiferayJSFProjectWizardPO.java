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

import com.liferay.ide.swtbot.project.ui.tests.NewLiferayJSFProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ying Xu
 */
public class NewLiferayJSFProjectWizardPO extends WizardPO implements NewLiferayJSFProjectWizard
{

    private TextPO _projectName;
    private TextPO _location;
    private ComboBoxPO _buildFramework;
    private ComboBoxPO _componentSuite;
    private CheckBoxPO _useDefaultLocation;

    public NewLiferayJSFProjectWizardPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public NewLiferayJSFProjectWizardPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_NEW_JSF_PROJECT_VALIDATION_MESSAGE );
    }

    public NewLiferayJSFProjectWizardPO( SWTBot bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, validationMessageIndex );
    }

    public NewLiferayJSFProjectWizardPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );
        _projectName = new TextPO( bot, LABEL_JSF_PROJECT_NAME );
        _location = new TextPO( bot, LABEL_JSF_LOCATION );
        _buildFramework = new ComboBoxPO( bot, LABEL_BUILD_FRAMEWORK );
        _componentSuite = new ComboBoxPO( bot, LABEL_COMPONENT_SUITE );
        _useDefaultLocation = new CheckBoxPO( bot, CHECKBOX_USE_DEFAULT_LOCATION );
    }

    public void createJSFProject( String projectName )
    {
        _projectName.setText( projectName );
    }

    public void createJSFProject( String projectName, String buildFrameworkspace )
    {
        _projectName.setText( projectName );
        _buildFramework.setSelection( buildFrameworkspace );
    }

    public void createJSFProject( String projectName, String buildFramework, String componentSuite )
    {
        _projectName.setText( projectName );
        _buildFramework.setSelection( buildFramework );
        _componentSuite.setSelection( componentSuite );
    }

    public TextPO getProjectName()
    {
        return _projectName;
    }

    public TextPO getLocation()
    {
        return _location;
    }

    public void setLocation( TextPO _location )
    {
        this._location = _location;
    }

    public ComboBoxPO getBuildFramework()
    {
        return _buildFramework;
    }

    public ComboBoxPO getComponentSuite()
    {
        return _componentSuite;
    }

    public CheckBoxPO getUseDefaultLocation()
    {
        return _useDefaultLocation;
    }

}
