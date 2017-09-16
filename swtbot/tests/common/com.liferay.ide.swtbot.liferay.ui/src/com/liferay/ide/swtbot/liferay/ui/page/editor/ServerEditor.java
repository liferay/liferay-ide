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

package com.liferay.ide.swtbot.liferay.ui.page.editor;

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Text;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class ServerEditor extends Editor
{

    private Text httpPort;
    private Radio customLaunchSettings;
    private Radio defaultLaunchSettings;
    private CheckBox useDeveloperMode;

    public ServerEditor( SWTWorkbenchBot bot )
    {
        super( bot );

        httpPort = new Text( bot, "Http Port:" );

        defaultLaunchSettings = new Radio( bot, "Default Launch Settings" );
        customLaunchSettings = new Radio( bot, "Custom Launch Settings" );
        useDeveloperMode = new CheckBox( bot, "Use developer mode" );
    }

    public ServerEditor( SWTWorkbenchBot bot, String editorName )
    {
        super( bot, editorName );

        httpPort = new Text( bot, "Http Port:" );

        defaultLaunchSettings = new Radio( bot, "Default Launch Settings" );
        customLaunchSettings = new Radio( bot, "Custom Launch Settings" );
        useDeveloperMode = new CheckBox( bot, "Use developer mode" );
    }

    public Text getHttpPort()
    {
        return httpPort;
    }

    public Radio getCustomLaunchSettings()
    {
        return customLaunchSettings;
    }

    public Radio getDefaultLaunchSettings()
    {
        return defaultLaunchSettings;
    }

    public CheckBox getUseDeveloperMode()
    {
        return useDeveloperMode;
    }

}
