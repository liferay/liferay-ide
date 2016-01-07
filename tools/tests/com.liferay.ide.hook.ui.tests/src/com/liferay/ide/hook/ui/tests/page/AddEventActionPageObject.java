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
public class AddEventActionPageObject<T extends SWTBot> extends DialogPageObject<T>
    implements HookConfigurationWizard, ProjectWizard
{

    TextPageObject<SWTBot> event;
    TextPageObject<SWTBot> eventActionclass;

    ButtonPageObject<SWTBot> selectEvent;
    ButtonPageObject<SWTBot> selectClass;
    ButtonPageObject<SWTBot> buttonnew;

    public AddEventActionPageObject( T bot )
    {
        this( bot, TEXT_BLANK );
    }

    public AddEventActionPageObject( T bot, String title )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK );

        event = new TextPageObject<SWTBot>( bot, LABLE_EVENT );
        eventActionclass = new TextPageObject<SWTBot>( bot, LABLE_CLASS );

        selectEvent = new ButtonPageObject<SWTBot>( bot, BUTTON_SELECT, 0 );
        selectClass = new ButtonPageObject<SWTBot>( bot, BUTTON_SELECT, 1 );
        buttonnew = new ButtonPageObject<SWTBot>( bot, BUTTON_NEW );
    }

    public ButtonPageObject<SWTBot> getNew()
    {
        return buttonnew;
    }

    public ButtonPageObject<SWTBot> getSelectEvent()
    {
        return selectEvent;
    }

    public ButtonPageObject<SWTBot> getSelectClass()
    {
        return selectClass;
    }

    public void setEvent( String text )
    {
        event.setText( text );
    }

    public void setEventActionclass( String text )
    {
        eventActionclass.setText( text );
    }

}
