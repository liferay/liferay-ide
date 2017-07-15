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
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Vicky Wang
 */
public class ThemeWizard extends Wizard implements WizardUI
{

    ComboBox themeFrameworkTypes;
    ComboBox themeParentTypes;

    public ThemeWizard( SWTBot bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public ThemeWizard( SWTBot bot, int indexThemeValidationMsg )
    {
        this( bot, TEXT_BLANK, indexThemeValidationMsg );
    }

    public ThemeWizard( SWTBot bot, String title, int indexThemeValidationMsg )
    {
        super( bot, title, indexThemeValidationMsg );

        themeParentTypes = new ComboBox( bot, THEME_PARENT_TYPE );
        themeFrameworkTypes = new ComboBox( bot, THEME_FARMEWORK_TYPE );
    }

    public void setParentFramework( String parent, String framework )
    {
        themeParentTypes.setSelection( parent );
        themeFrameworkTypes.setSelection( framework );
    }
}
