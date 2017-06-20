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
public class AddPortalPropertiesOverridePO extends DialogPO implements HookConfigurationWizard, ProjectWizard
{

    private TextPO _valueText;
    private TextPO _propertyText;
    private ButtonPO _selectPropertyButton;

    public AddPortalPropertiesOverridePO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public AddPortalPropertiesOverridePO( SWTBot bot, String title )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK );

        _valueText = new TextPO( bot, LABLE_VALUE );
        _propertyText = new TextPO( bot, LABLE_PROPERTY );
        _selectPropertyButton = new ButtonPO( bot, BUTTON_SELECT );
    }

    public ButtonPO getSelectProperty()
    {
        return _selectPropertyButton;
    }

    public void setProperty( String text )
    {
        _propertyText.setText( text );
    }

    public void setValue( String text )
    {
        _valueText.setText( text );
    }

}