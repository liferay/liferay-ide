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

package com.liferay.ide.swtbot.module.fragment.ui.tests.page;

import com.liferay.ide.swtbot.module.fragment.tests.ModuleFragmentProjectWizard;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.WizardPO;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 * @author Sunny Shi
 */
public class CreateModuleFragmentProjectWizardPO extends WizardPO implements ModuleFragmentProjectWizard
{

    private TextPO _projectNameText;
    private TextPO _liferayRuntimeText;
    private ComboBoxPO _buildType;

    public CreateModuleFragmentProjectWizardPO( SWTBot bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public CreateModuleFragmentProjectWizardPO( SWTBot bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public CreateModuleFragmentProjectWizardPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public CreateModuleFragmentProjectWizardPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        _projectNameText = new TextPO( bot, LABEL_PROJECT_NAME );
        _liferayRuntimeText = new TextPO( bot, LABEL_RUNTIME_NAME );
        _buildType = new ComboBoxPO( bot, LABEL_BUILD_TYPE );
    }

    public String getProjectNameText()
    {
        return _projectNameText.getText();
    }

    public boolean isLiferayRuntimeTextEnabled()
    {
        return _liferayRuntimeText.isEnabled();
    }

    public void setProjectName( String projectName ,String buildType)
    {
        this._projectNameText.setText( projectName );
        this._buildType.setSelection( buildType );
    }
    public void setProjectName( String projectName)
    {
        this._projectNameText.setText( projectName );

    }
    public ComboBoxPO getBuildType()
    {
        return _buildType;
    }
}
