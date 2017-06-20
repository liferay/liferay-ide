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

import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Ashley Yuan
 */
public class SetSDKLocationPO extends WizardPO implements ProjectWizard
{

    TextPO _sdkLocation;

    public SetSDKLocationPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public SetSDKLocationPO( SWTBot bot, int index )
    {
        this( bot, TEXT_BLANK, index );
    }

    public SetSDKLocationPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public SetSDKLocationPO( SWTBot bot, String title, int index )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, index );

        _sdkLocation = new TextPO( bot, LABEL_SDK_LOCATION );
    }

    public void setSdkLocation( String sdkLocation )
    {
        _sdkLocation.setText( sdkLocation );
    }

}