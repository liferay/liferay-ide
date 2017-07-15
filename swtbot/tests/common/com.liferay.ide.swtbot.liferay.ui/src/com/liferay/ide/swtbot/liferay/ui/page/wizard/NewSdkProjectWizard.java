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

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class NewSdkProjectWizard extends NewProjectWizard implements WizardUI
{

    private ComboBox buildTypes;
    private Text displayName;
    private CheckBox includeSimpleCode;
    private CheckBox launchNewPortletWizard;
    private ComboBox pluginTypes;

    public NewSdkProjectWizard( SWTBot bot )
    {
        this( bot, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewSdkProjectWizard( SWTBot bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public NewSdkProjectWizard( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewSdkProjectWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );

        displayName = new Text( bot, LABEL_DISPLAY_NAME );
        buildTypes = new ComboBox( bot, LABEL_BUILD_TYPE );
        pluginTypes = new ComboBox( bot, LABEL_PLUGIN_TYPE );
        includeSimpleCode = new CheckBox( bot, CHECKBOX_INCLUDE_SAMPLE_CODE );
        launchNewPortletWizard = new CheckBox( bot, LABEL_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT );
    }

    public void createSDKPortletProject( String projectName )
    {
        createSDKProject( projectName, MENU_PORTLET );
    }

    public void createSDKProject( String projectName, String pluginType )
    {
        createSDKProject( projectName, TEXT_BLANK, pluginType );
    }

    public void createSDKProject( String projectName, String pluginType, boolean includeSimpleCode )
    {
        createSDKProject( projectName, pluginType, includeSimpleCode, false );
    }

    public void createSDKProject(
        String projectName, String pluginType, boolean includeSimpleCode, boolean launchNewPortletWizard )
    {
        createSDKProject( projectName, TEXT_BLANK, pluginType, includeSimpleCode, launchNewPortletWizard );
    }

    public void createSDKProject( String projectName, String displayName, String pluginType )
    {
        createSDKProject( projectName, displayName, pluginType, false, false );
    }

    public void createSDKProject(
        String projectNameValue, String displayNameValue, String pluginType, boolean checkIncludeSimpleCode,
        boolean checkLaunchNewPortletWizard )
    {
        getProjectName().setText( projectNameValue );

        if( displayNameValue != null && !displayNameValue.equals( TEXT_BLANK ) )
        {
            displayName.setText( displayNameValue );
        }

        buildTypes.setSelection( MENU_BUILD_TYPE_ANT );

        pluginTypes.setSelection( pluginType );

        if( pluginType.equals( MENU_PORTLET ) || pluginType.equals( MENU_SERVICE_BUILDER_PORTLET ) )
        {
            if( checkIncludeSimpleCode )
            {
                includeSimpleCode.select();
            }
            else
            {
                includeSimpleCode.deselect();
            }
        }

        if( pluginType.equals( MENU_PORTLET ) )
        {
            if( checkLaunchNewPortletWizard )
            {
                launchNewPortletWizard.select();
            }
            else
            {
                launchNewPortletWizard.deselect();
            }
        }
    }

    public ComboBox getBuildTypes()
    {
        return buildTypes;
    }

    public Text getDisplayName()
    {
        return displayName;
    }

    public CheckBox getIncludeSimpleCode()
    {
        return includeSimpleCode;
    }

    public CheckBox getLaunchNewPortletWizard()
    {
        return launchNewPortletWizard;
    }

    public ComboBox getPluginTypes()
    {
        return pluginTypes;
    }

}
