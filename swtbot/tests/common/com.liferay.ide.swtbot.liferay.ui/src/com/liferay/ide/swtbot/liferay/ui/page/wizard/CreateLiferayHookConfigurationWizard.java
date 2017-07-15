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

import com.liferay.ide.swtbot.liferay.ui.DialogUI;
import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Vicky Wang
 */
public class CreateLiferayHookConfigurationWizard extends Wizard implements DialogUI, WizardUI
{

    private CheckBox _customJSPsCheckBox;
    private CheckBox _languagePropertiesCheckBox;
    private CheckBox _portalPropertiesCheckBox;
    private CheckBox _servicesCheckBox;
    private ComboBox _hookPluginProjectComboBox;

    public CreateLiferayHookConfigurationWizard( SWTBot bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public CreateLiferayHookConfigurationWizard( SWTBot bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public CreateLiferayHookConfigurationWizard( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public CreateLiferayHookConfigurationWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );

        _customJSPsCheckBox = new CheckBox( bot, LABLE_CUSTOM_JSPS );
        _portalPropertiesCheckBox = new CheckBox( bot, LABLE_PORTAL_PROPERTIES );
        _servicesCheckBox = new CheckBox( bot, LABLE_SERVICES );
        _languagePropertiesCheckBox = new CheckBox( bot, LABLE_LANGUAGE_PROPERTIES );
        _hookPluginProjectComboBox = new ComboBox( bot, COMBOBOX_HOOK_PLUGIN_PROJECT );
    }

    public CheckBox getCustomJSPs()
    {
        return _customJSPsCheckBox;
    }

    public CheckBox getLanguageProperties()
    {
        return _languagePropertiesCheckBox;
    }

    public CheckBox getPortalProperties()
    {
        return _portalPropertiesCheckBox;
    }

    public CheckBox getServices()
    {
        return _servicesCheckBox;
    }

    public ComboBox getHookPluginProjectComboBox()
    {
        return _hookPluginProjectComboBox;
    }

    public void setHookPluginProjectComboBox( String hookPluginProjectComboBox )
    {
        this._hookPluginProjectComboBox.setText( hookPluginProjectComboBox );
    }

}
