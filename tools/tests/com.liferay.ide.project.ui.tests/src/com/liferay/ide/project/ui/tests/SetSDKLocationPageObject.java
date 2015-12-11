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

package com.liferay.ide.project.ui.tests;

import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Ashley Yuan
 */
public class SetSDKLocationPageObject<T extends SWTBot> extends WizardPageObject<T> implements ProjectWizard
{

    TextPageObject<SWTBot> sdkLocationText;

    public SetSDKLocationPageObject( T bot )
    {
        this( bot, TEXT_BLANK );
    }

    public SetSDKLocationPageObject( T bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public SetSDKLocationPageObject( T bot, String title, int index )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, index );

        sdkLocationText = new TextPageObject<SWTBot>( bot, "SDK Location:" );
    }

    public void setSdkLocation( String sdkLocation )
    {
        sdkLocationText.setText( sdkLocation );
    }
}
