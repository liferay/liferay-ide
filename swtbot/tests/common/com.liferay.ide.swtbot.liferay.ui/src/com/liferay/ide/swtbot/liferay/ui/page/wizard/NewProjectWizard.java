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

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Text;

/**
 * @author Ashley Yuan
 */
public class NewProjectWizard extends BaseProjectWizard
{

    private Text location;
    private CheckBox useDefaultLocation;

    public NewProjectWizard( SWTBot bot, int validationMsgIndex )
    {
        this( bot, TEXT_BLANK, validationMsgIndex );
    }

    public NewProjectWizard( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public NewProjectWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );
    }

    public void createJavaProject( String projectName )
    {
        createJavaProject( projectName, true, TEXT_BLANK );
    }

    public void createJavaProject( String projectName, boolean useDefaultLocationValue, String customLocationValue )
    {
        super.createProject( projectName );

        if( !useDefaultLocationValue )
        {
            useDefaultLocation.deselect();
            location.setText( customLocationValue );
        }
    }

    public Text getLocation()
    {
        return location;
    }

    public CheckBox getUseDefaultLocation()
    {
        return useDefaultLocation;
    }

}
