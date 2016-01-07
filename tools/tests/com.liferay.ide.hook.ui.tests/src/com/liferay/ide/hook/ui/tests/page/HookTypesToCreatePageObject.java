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

package com.liferay.ide.hook.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.hook.ui.tests.HookConfigurationWizard;
import com.liferay.ide.project.ui.tests.page.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

/**
 * @author Vicky Wang
 */
public class HookTypesToCreatePageObject<T extends SWTBot> extends WizardPageObject<T>
    implements HookConfigurationWizard, ProjectWizard
{

    CheckBoxPageObject<SWTBot> customJSPs;
    CheckBoxPageObject<SWTBot> languageProperties;
    CheckBoxPageObject<SWTBot> portalProperties;
    CheckBoxPageObject<SWTBot> services;

    public HookTypesToCreatePageObject( T bot, String title )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT );

        customJSPs = new CheckBoxPageObject<SWTBot>( bot, LABLE_CUSTOM_JSPS );
        portalProperties = new CheckBoxPageObject<SWTBot>( bot, LABLE_PORTAL_PROPERTIES );
        services = new CheckBoxPageObject<SWTBot>( bot, LABLE_SERVICES );
        languageProperties = new CheckBoxPageObject<SWTBot>( bot, LABLE_LANGUAGE_PROPERTIES );
    }

    public CheckBoxPageObject<SWTBot> getCustomJSPs()
    {
        return customJSPs;
    }

    public CheckBoxPageObject<SWTBot> getLanguageProperties()
    {
        return languageProperties;
    }
    
    public CheckBoxPageObject<SWTBot> getPortalProperties()
    {
        return portalProperties;
    }
    
    public CheckBoxPageObject<SWTBot> getServices()
    {
        return services;
    }
}
