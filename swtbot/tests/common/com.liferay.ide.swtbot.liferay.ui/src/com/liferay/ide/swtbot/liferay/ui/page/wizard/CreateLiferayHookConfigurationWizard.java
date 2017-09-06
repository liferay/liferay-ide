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

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 */
public class CreateLiferayHookConfigurationWizard extends Wizard
{

    private final CheckBox customJsps;
    private final ComboBox hookPluginProject;
    private final CheckBox languageProperties;
    private final CheckBox portalProperties;
    private final CheckBox services;

    public CreateLiferayHookConfigurationWizard( final SWTWorkbenchBot bot )
    {
        super( bot, NEW_LIFERAY_HOOK, 0 );

        customJsps = new CheckBox( bot, CUSTOM_JSPS );
        portalProperties = new CheckBox( bot, PORTAL_PROPERTIES );
        services = new CheckBox( bot, SERVICES );
        languageProperties = new CheckBox( bot, LANGUAGE_PROPERTIES );
        hookPluginProject = new ComboBox( bot, HOOK_PLUGIN_PROJECT );
    }

    public CheckBox getCustomJsps()
    {
        return customJsps;
    }

    public ComboBox getHookPluginProjectComboBox()
    {
        return hookPluginProject;
    }

    public CheckBox getLanguageProperties()
    {
        return languageProperties;
    }

    public CheckBox getPortalProperties()
    {
        return portalProperties;
    }

    public CheckBox getServices()
    {
        return services;
    }

    public void setHookPluginProjectComboBox( String hookPluginProjectComboBox )
    {
        this.hookPluginProject.setText( hookPluginProjectComboBox );
    }

}
