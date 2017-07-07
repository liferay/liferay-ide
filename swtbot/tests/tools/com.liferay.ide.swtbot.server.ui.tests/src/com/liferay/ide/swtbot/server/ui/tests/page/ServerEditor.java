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

package com.liferay.ide.swtbot.server.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.EditorPO;
import com.liferay.ide.swtbot.ui.tests.page.RadioPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Terry Jia
 */
public class ServerEditor extends EditorPO
{

    private TextPO _httpPort;
    private RadioPO _customLaunchSettings;
    private RadioPO _defaultLaunchSettings;
    private CheckBoxPO _useDeveloperMode;

    public ServerEditor( SWTBot bot, String editorName )
    {
        super( bot, editorName );

        _httpPort = new TextPO( bot, "Http Port:" );

        _defaultLaunchSettings = new RadioPO( bot, "Default Launch Settings" );
        _customLaunchSettings = new RadioPO( bot, "Custom Launch Settings" );
        _useDeveloperMode = new CheckBoxPO( bot, "Use developer mode" );
    }

    public TextPO getHttpPort()
    {
        return _httpPort;
    }

    public RadioPO getCustomLaunchSettings()
    {
        return _customLaunchSettings;
    }

    public RadioPO getDefaultLaunchSettings()
    {
        return _defaultLaunchSettings;
    }

    public CheckBoxPO getUseDeveloperMode()
    {
        return _useDeveloperMode;
    }

}
