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

import com.liferay.ide.ui.tests.swtbot.page.AbstractProjectPageObject;
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class NewProjectPageObject<T extends SWTBot> extends AbstractProjectPageObject<T>
{

    CheckBoxPageObject<SWTBot> useDefaultLocationBox;
    TextPageObject<SWTBot> customLocationText;

    public NewProjectPageObject( T bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, validationMessageIndex );
    }

    public NewProjectPageObject( T bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewProjectPageObject( T bot, String title, int validationMessageIndex )
    {
        super( bot, title, validationMessageIndex );
    }

    public void createJavaProject( String projectName )
    {
        createJavaProject( projectName, true, TEXT_BLANK );
    }

    public void createJavaProject( String projectName, boolean useDefaultLocation, String customLocation )
    {

        super.createProject( projectName );

        if( !useDefaultLocation )
        {
            useDefaultLocationBox.deselect();
            customLocationText.setText( customLocation );
        }
    }

}
