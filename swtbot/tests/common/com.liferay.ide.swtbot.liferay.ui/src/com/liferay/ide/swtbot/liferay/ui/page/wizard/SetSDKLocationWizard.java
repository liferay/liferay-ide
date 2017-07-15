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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Ashley Yuan
 */
public class SetSDKLocationWizard extends Wizard implements WizardUI
{

    private Text sdkLocation;

    public SetSDKLocationWizard( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public SetSDKLocationWizard( SWTBot bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public SetSDKLocationWizard( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public SetSDKLocationWizard( SWTBot bot, String title, int index )
    {
        super( bot, title, index );

        sdkLocation = new Text( bot, LABEL_SDK_LOCATION );
    }

    public Text getSdkLocation()
    {
        return sdkLocation;
    }

}
