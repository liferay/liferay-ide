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

package com.liferay.ide.project.ui.tests;

import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.ComboBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class CreateProjectWizardPageObject<T extends SWTBot> extends WizardPageObject<T> implements ProjectWizard
{

    TextPageObject<SWTBot> projectNameText;
    TextPageObject<SWTBot> displayNameText;
    ComboBoxPageObject<SWTBot> buildTypeComboBox;
    TextPageObject<SWTBot> artifactVersionText;
    TextPageObject<SWTBot> groupIdText;
    TextPageObject<SWTBot> activeProfilesText;
    CheckBoxPageObject<SWTBot> useDefaultLoactionCheckBox;
    TextPageObject<SWTBot> location;
    ComboBoxPageObject<SWTBot> pluginTypeComboBox;
    CheckBoxPageObject<SWTBot> includeSimpleCodeCheckBox;
    CheckBoxPageObject<SWTBot> launchNewPortletWizardCheckBox;

    public CreateProjectWizardPageObject( T bot )
    {
        this( bot, TEXT_BLANK, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public CreateProjectWizardPageObject( T bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public CreateProjectWizardPageObject( T bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        projectNameText = new TextPageObject<>( bot, LABEL_PROJECT_NAME );
        displayNameText = new TextPageObject<>( bot, LABEL_DISPLAY_NAME );
        buildTypeComboBox = new ComboBoxPageObject<>( bot, LABEL_BUILD_TYPE );
        artifactVersionText = new TextPageObject<>( bot, LABEL_ACTIVE_PROFILES );
        groupIdText = new TextPageObject<>( bot, LABEL_GROUP_ID );
        activeProfilesText = new TextPageObject<>( bot, LABEL_ACTIVE_PROFILES );
        useDefaultLoactionCheckBox = new CheckBoxPageObject<>( bot, LABEL_USE_DEFAULT_LOCATION );
        location = new TextPageObject<>( bot, LABEL_LOCATION );
        pluginTypeComboBox = new ComboBoxPageObject<>( bot, LABEL_PLUGIN_TYPE );
        includeSimpleCodeCheckBox = new CheckBoxPageObject<>( bot, LABEL_INCLUDE_SAMPLE_CODE );
        launchNewPortletWizardCheckBox = new CheckBoxPageObject<>( bot, LABEL_LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT );
    }

    public void createSDKProject( String projectName )
    {
        createSDKProject( projectName, MENU_PORTLET );
    }

    public void createSDKProject( String projectName, String pluginType )
    {
        createSDKProject( projectName, "", pluginType );
    }

    public void createSDKProject( String projectName, String displayName, String pluginType )
    {
        createSDKProject( projectName, displayName, pluginType, false, false );
    }

    public void createSDKProject( String projectName, String pluginType, boolean includeSimpleCode )
    {
        createSDKProject( projectName, pluginType, includeSimpleCode, false );
    }

    public void createSDKProject(
        String projectName, String pluginType, boolean includeSimpleCode, boolean launchNewPortletWizard )
    {
        createSDKProject( projectName, "", pluginType, includeSimpleCode, launchNewPortletWizard );
    }

    public void createSDKProject(
        String projectName, String displayName, String pluginType, boolean includeSimpleCode,
        boolean launchNewPortletWizard )
    {
        projectNameText.setText( projectName );

        if( displayName != null && !displayName.equals( "" ) )
        {
            displayNameText.setText( displayName );
        }

        buildTypeComboBox.setSelection( MENU_BUILD_TYPE_ANT );

        pluginTypeComboBox.setSelection( pluginType );

        if( pluginType.equals( MENU_PORTLET ) || pluginType.equals( MENU_SERVICE_BUILDER_PORTLET ) )
        {
            if( includeSimpleCode )
            {
                includeSimpleCodeCheckBox.select();
            }
            else
            {
                includeSimpleCodeCheckBox.deselect();
            }
        }

        if( pluginType.equals( MENU_PORTLET ) )
        {
            if( launchNewPortletWizard )
            {
                launchNewPortletWizardCheckBox.select();
            }
            else
            {
                launchNewPortletWizardCheckBox.deselect();
            }
        }

    }

}
