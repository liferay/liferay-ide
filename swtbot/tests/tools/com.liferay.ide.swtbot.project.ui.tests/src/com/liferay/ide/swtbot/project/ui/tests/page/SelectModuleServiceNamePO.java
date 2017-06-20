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

package com.liferay.ide.swtbot.project.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.project.ui.tests.NewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Ying Xu
 */
public class SelectModuleServiceNamePO extends DialogPO implements NewLiferayModuleProjectWizard
{

    private TextPO _serviceNameText;

    public SelectModuleServiceNamePO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public SelectModuleServiceNamePO( SWTBot bot, String title )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK );
        _serviceNameText = new TextPO( bot, LABEL_MODULE_SELECT_SERVICE_NAME );
    }

    public TextPO getServiceNameText()
    {
        return _serviceNameText;
    }

    public void selectServiceName( String text )
    {
        _serviceNameText.setText( text );
    }

}
