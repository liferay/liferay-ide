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

package com.liferay.ide.swtbot.liferay.ui.page.wizard.project;

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class NewSdkProjectWizard extends NewProjectWizard
{

    private Text displayName;
    private CheckBox includeSimpleCode;
    private CheckBox launchNewPortletWizard;
    private ComboBox pluginTypes;

    public NewSdkProjectWizard( SWTWorkbenchBot bot )
    {
        this( bot, NEW_LIFERAY_PLUGIN_PROJECT, 2 );
    }

    public NewSdkProjectWizard( SWTWorkbenchBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );

        displayName = new Text( bot, DISPLAY_NAME );
        pluginTypes = new ComboBox( bot, PLUGIN_TYPE );
        includeSimpleCode = new CheckBox( bot, INCLUDE_SAMPLE_CODE );
        launchNewPortletWizard = new CheckBox( bot, LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT );
    }

    public void createSDKPortletProject( String projectName )
    {
        createSDKProject( projectName, PORTLET );
    }

    public void createSDKProject( String projectName, String pluginType )
    {
        createSDKProject( projectName, StringPool.BLANK, pluginType );
    }

    public void createSDKProject( String projectName, String pluginType, boolean includeSimpleCode )
    {
        createSDKProject( projectName, pluginType, includeSimpleCode, false );
    }

    public void createSDKProject(
        String projectName, String pluginType, boolean includeSimpleCode, boolean launchNewPortletWizard )
    {
        createSDKProject( projectName, StringPool.BLANK, pluginType, includeSimpleCode, launchNewPortletWizard );
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

        if( displayNameValue != null && !displayNameValue.equals( StringPool.BLANK ) )
        {
            displayName.setText( displayNameValue );
        }

        getBuildTypes().setSelection( ANT_LIFERAY_PLUGINS_SDK );

        pluginTypes.setSelection( pluginType );

        if( pluginType.equals( PORTLET ) || pluginType.equals( SERVICE_BUILDER_PORTLET ) )
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

        if( pluginType.equals( PORTLET ) )
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
