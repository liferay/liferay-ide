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
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Vicky Wang
 */
public class EventSelectionPO extends DialogPO implements HookConfigurationWizard, ProjectWizard
{

    private TextPO _eventActionText;

    public EventSelectionPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public EventSelectionPO( SWTBot bot, String title )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK );

        _eventActionText = new TextPO( bot, LABLE_SELECT_AN_EVENT_ACTION );
    }

    public void setEventAction( String text )
    {
        this._eventActionText.setText( text );
    }

}