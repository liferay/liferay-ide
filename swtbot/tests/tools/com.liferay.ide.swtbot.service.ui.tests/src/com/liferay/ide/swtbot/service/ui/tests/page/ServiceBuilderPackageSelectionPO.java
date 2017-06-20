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

package com.liferay.ide.swtbot.service.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.service.ui.tests.ServiceBuilderWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Ying Xu
 */
public class ServiceBuilderPackageSelectionPO extends DialogPO implements ServiceBuilderWizard
{

    private TextPO _packageSelectionText;
    private ButtonPO _okButton;

    public ServiceBuilderPackageSelectionPO( SWTBot bot, String cancelButtonText, String confirmButtonText )
    {
        this( bot, TEXT_BLANK, cancelButtonText, confirmButtonText );

    }

    public ServiceBuilderPackageSelectionPO(
        SWTBot bot, String title, String cancelButtonText, String confirmButtonText )
    {
        super( bot, title, cancelButtonText, confirmButtonText );
        _packageSelectionText = new TextPO( bot, LABEL_CHOOSE_PACKAGE );
        _okButton = new ButtonPO( bot, BUTTON_OK );

    }

    public ButtonPO getOkButton()
    {
        return _okButton;
    }

    public void selectPackage( String packageText )
    {
        _packageSelectionText.setText( packageText );
    }

}
