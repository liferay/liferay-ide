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

package com.liferay.ide.swtbot.liferay.ui.page.dialog;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.liferay.ui.DialogUI;
import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Text;

/**
 * @author Vicky Wang
 */
public class AddEventActionDialog extends Dialog implements DialogUI, WizardUI
{

    private Text event;
    private Text eventActionClass;
    private Button newBtn;
    private Button selectClassBtn;
    private Button selectEventBtn;

    public AddEventActionDialog( SWTBot bot )
    {
        super( bot );

        event = new Text( bot, LABLE_EVENT );
        eventActionClass = new Text( bot, LABLE_CLASS );
        selectEventBtn = new Button( bot, BUTTON_SELECT, 0 );
        selectClassBtn = new Button( bot, BUTTON_SELECT, 1 );
        newBtn = new Button( bot, BUTTON_NEW );
    }

    public Text getEvent()
    {
        return event;
    }

    public Text getEventActionClass()
    {
        return eventActionClass;
    }

    public Button getNewBtn()
    {
        return newBtn;
    }

    public Button getSelectClassBtn()
    {
        return selectClassBtn;
    }

    public Button getSelectEventBtn()
    {
        return selectEventBtn;
    }

}
