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
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 */
public class ThemeWizardPO extends WizardPO implements ProjectWizard
{

    ComboBoxPO _themeFrameworkTypeComboBox;
    ComboBoxPO _themeParentTypeComboBox;

    public ThemeWizardPO( SWTBot bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public ThemeWizardPO( SWTBot bot, int indexThemeValidationMessage )
    {
        this( bot, TEXT_BLANK, indexThemeValidationMessage );
    }

    public ThemeWizardPO( SWTBot bot, String title, int indexThemeValidationMessage )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, indexThemeValidationMessage );

        _themeParentTypeComboBox = new ComboBoxPO( bot, THEME_PARENT_TYPE );
        _themeFrameworkTypeComboBox = new ComboBoxPO( bot, THEME_FARMEWORK_TYPE );
    }

    public void setParentFramework( String parent, String framework )
    {
        _themeParentTypeComboBox.setSelection( parent );
        _themeFrameworkTypeComboBox.setSelection( framework );
    }
}
