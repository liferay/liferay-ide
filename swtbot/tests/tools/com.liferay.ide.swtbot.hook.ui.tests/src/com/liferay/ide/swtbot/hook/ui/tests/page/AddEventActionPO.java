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

package com.liferay.ide.swtbot.hook.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.hook.ui.tests.HookConfigurationWizard;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Vicky Wang
 */
public class AddEventActionPO extends DialogPO implements HookConfigurationWizard, ProjectWizard
{

    private TextPO _eventText;
    private TextPO _eventActionclassText;
    private ButtonPO _selectEventButton;
    private ButtonPO _selectClassButton;
    private ButtonPO _newButton;

    public AddEventActionPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public AddEventActionPO( SWTBot bot, String title )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK );

        _eventText = new TextPO( bot, LABLE_EVENT );
        _eventActionclassText = new TextPO( bot, LABLE_CLASS );

        _selectEventButton = new ButtonPO( bot, BUTTON_SELECT, 0 );
        _selectClassButton = new ButtonPO( bot, BUTTON_SELECT, 1 );
        _newButton = new ButtonPO( bot, BUTTON_NEW );
    }

    public ButtonPO getNewButton()
    {
        return _newButton;
    }

    public ButtonPO getSelectEventButton()
    {
        return _selectEventButton;
    }

    public ButtonPO getSelectClass()
    {
        return _selectClassButton;
    }

    public void setEvent( String text )
    {
        _eventText.setText( text );
    }

    public void setEventActionclass( String text )
    {
        _eventActionclassText.setText( text );
    }

}