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
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;

/**
 * @author Vicky Wang
 */
public class AddServiceWrapperPageObject<T extends SWTBot> extends DialogPageObject<T>
    implements HookConfigurationWizard, ProjectWizard
{

    ButtonPageObject<SWTBot> buttonnew;
    ButtonPageObject<SWTBot> selectImplClass;
    ButtonPageObject<SWTBot> selectServiceType;

    TextPageObject<SWTBot> className;
    TextPageObject<SWTBot> implClass;
    TextPageObject<SWTBot> javaPackage;
    TextPageObject<SWTBot> serviceType;

    public AddServiceWrapperPageObject( T bot )
    {
        this( bot, TEXT_BLANK );
    }

    public AddServiceWrapperPageObject( T bot, String title )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK );

        serviceType = new TextPageObject<SWTBot>( bot, LABLE_SERVICE_TYPE );
        implClass = new TextPageObject<SWTBot>( bot, LABLE_IMPL_CLASS );
        selectServiceType = new ButtonPageObject<SWTBot>( bot, BUTTON_SELECT );
        selectImplClass = new ButtonPageObject<SWTBot>( bot, BUTTON_SELECT );
        buttonnew = new ButtonPageObject<SWTBot>( bot, BUTTON_NEW );
    }

    public ButtonPageObject<SWTBot> getNew()
    {
        return buttonnew;
    }

    public ButtonPageObject<SWTBot> getSelectImplClass( int index )
    {
        return selectImplClass;
    }

    public ButtonPageObject<SWTBot> getSelectServiceType()
    {
        return selectServiceType;
    }

    public void setImplClassText( String text )
    {
        implClass.setText( text );
    }

    public void setServiceTypeText( String text )
    {
        serviceType.setText( text );
    }

    public TextPageObject<SWTBot> getImplClass()
    {
        return implClass;
    }

}
