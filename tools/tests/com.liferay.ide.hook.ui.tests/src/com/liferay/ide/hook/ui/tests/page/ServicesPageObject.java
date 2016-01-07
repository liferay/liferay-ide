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
import com.liferay.ide.ui.tests.swtbot.page.ButtonPageObject;
import com.liferay.ide.ui.tests.swtbot.page.DialogPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TablePageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

/**
 * @author Vicky Wang
 */
public class ServicesPageObject<T extends SWTBot> extends WizardPageObject<T>
    implements HookConfigurationWizard, ProjectWizard
{

    ButtonPageObject<SWTBot> add;
    DialogPageObject<SWTBot> addService;
    DialogPageObject<SWTBot> addServiceWrapper;
    TablePageObject<SWTBot> definePortalServices;
    ButtonPageObject<SWTBot> edit;
    ButtonPageObject<SWTBot> remove;
    TextPageObject<SWTBot> serviceType;

    public ServicesPageObject( T bot, String title, int indexPortalPropertiesValidationMessage )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, indexPortalPropertiesValidationMessage );

        addService = new DialogPageObject<SWTBot>( bot, WINDOW_ADD_SERVICE, BUTTON_CANCEL, BUTTON_OK );
        addServiceWrapper = new DialogPageObject<SWTBot>( bot, WINDOW_ADD_SERVICE_WRAPPER, BUTTON_CANCEL, BUTTON_OK );
        definePortalServices = new TablePageObject<SWTBot>( bot, LABLE_DEFINE_PORTAL_SERVICES );
        serviceType = new TextPageObject<SWTBot>( bot, LABLE_SERVICE_TYPE );

        add = new ButtonPageObject<SWTBot>( bot, BUTTON_ADD );
        edit = new ButtonPageObject<SWTBot>( bot, BUTTON_EDIT );
        remove = new ButtonPageObject<SWTBot>( bot, BUTTON_REMOVE );
    }

    public ButtonPageObject<SWTBot> getAdd()
    {
        return add;
    }

    public DialogPageObject<SWTBot> getAddServiceWrapper()
    {
        return addServiceWrapper;
    }

    public TablePageObject<SWTBot> getDefinePortalServices()
    {
        return definePortalServices;
    }

    public ButtonPageObject<SWTBot> getEdit()
    {
        return edit;
    }

    public ButtonPageObject<SWTBot> getRemove()
    {
        return remove;
    }

    public DialogPageObject<SWTBot> getAddService()
    {
        return addService;
    }

}
