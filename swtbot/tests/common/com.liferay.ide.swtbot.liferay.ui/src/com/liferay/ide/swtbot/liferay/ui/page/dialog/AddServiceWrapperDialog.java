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
public class AddServiceWrapperDialog extends Dialog implements DialogUI, WizardUI
{

    private Text implClass;
    private Button newBtn;
    private Button selectImplClassBtn;
    private Button selectServiceTypeBtn;
    private Text serviceType;

    public AddServiceWrapperDialog( SWTBot bot )
    {
        super( bot );

        serviceType = new Text( bot, LABLE_SERVICE_TYPE );
        implClass = new Text( bot, LABLE_IMPL_CLASS );
        selectServiceTypeBtn = new Button( bot, BUTTON_SELECT );
        selectImplClassBtn = new Button( bot, BUTTON_SELECT );
        newBtn = new Button( bot, BUTTON_NEW );
    }

    public Text getImplClass()
    {
        return implClass;
    }

    public Button getNewBtn()
    {
        return newBtn;
    }

    public Button getSelectImplClass( int index )
    {
        return selectImplClassBtn;
    }

    public Button getSelectServiceType()
    {
        return selectServiceTypeBtn;
    }

    public Text getServiceType()
    {
        return serviceType;
    }

}
