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

package com.liferay.ide.project.ui.tests.page;

import com.liferay.ide.project.ui.tests.swtbot.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.ComboBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 */
public class ThemeWizardPageObject<T extends SWTBot> extends WizardPageObject<T> implements ProjectWizard
{

    ComboBoxPageObject<SWTBot> themeFrameworkTypeComboBox;
    ComboBoxPageObject<SWTBot> themeParentTypeComboBox;

    public ComboBoxPageObject<SWTBot> getThemeFrameworkTypeComboBox()
    {
        return themeFrameworkTypeComboBox;
    }

    public ComboBoxPageObject<SWTBot> getThemeParentTypeComboBox()
    {
        return themeParentTypeComboBox;
    }

    public ThemeWizardPageObject( T bot )
    {
        this( bot, 0 );
    }

    public ThemeWizardPageObject( T bot, int indexThemeValidationMessage )
    {
        this( bot, TEXT_BLANK, indexThemeValidationMessage );
    }

    public ThemeWizardPageObject( T bot, String title, int indexThemeValidationMessage )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, indexThemeValidationMessage );

        themeParentTypeComboBox = new ComboBoxPageObject<SWTBot>( bot, THEME_PARENT_TYPE );
        themeFrameworkTypeComboBox = new ComboBoxPageObject<SWTBot>( bot, THEME_FARMEWORK_TYPE );
    }

    public void setParentFramework( String parent, String framework )
    {
        themeParentTypeComboBox.setSelection( parent );
        themeFrameworkTypeComboBox.setSelection( framework );
    }
}
