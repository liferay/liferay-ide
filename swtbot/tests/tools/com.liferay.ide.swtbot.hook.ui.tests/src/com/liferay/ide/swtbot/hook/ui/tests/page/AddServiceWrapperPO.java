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
public class AddServiceWrapperPO extends DialogPO implements HookConfigurationWizard, ProjectWizard
{

    private ButtonPO _newButton;
    private ButtonPO _selectImplClassButton;
    private ButtonPO _selectServiceTypeButton;
    private TextPO _implClassText;
    private TextPO _serviceTypeText;

    public AddServiceWrapperPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public AddServiceWrapperPO( SWTBot bot, String title )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK );

        _serviceTypeText = new TextPO( bot, LABLE_SERVICE_TYPE );
        _implClassText = new TextPO( bot, LABLE_IMPL_CLASS );
        _selectServiceTypeButton = new ButtonPO( bot, BUTTON_SELECT );
        _selectImplClassButton = new ButtonPO( bot, BUTTON_SELECT );
        _newButton = new ButtonPO( bot, BUTTON_NEW );
    }

    public ButtonPO getNewButton()
    {
        return _newButton;
    }

    public ButtonPO getSelectImplClass( int index )
    {
        return _selectImplClassButton;
    }

    public ButtonPO getSelectServiceType()
    {
        return _selectServiceTypeButton;
    }

    public void setImplClassText( String text )
    {
        _implClassText.setText( text );
    }

    public void setServiceTypeText( String text )
    {
        _serviceTypeText.setText( text );
    }

    public TextPO getImplClass()
    {
        return _implClassText;
    }

}