/**
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
 */

package com.liferay.ide.swtbot.liferay.ui.page.editor;

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Text;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class ServerEditor extends Editor {

	public ServerEditor(SWTWorkbenchBot bot) {
		super(bot);

		_httpPort = new Text(bot, "Http Port:");

		_defaultLaunchSettings = new Radio(bot, "Default Launch Settings");
		_customLaunchSettings = new Radio(bot, "Custom Launch Settings");
		_useDeveloperMode = new CheckBox(bot, "Use developer mode");
	}

	public ServerEditor(SWTWorkbenchBot bot, String editorName) {
		super(bot, editorName);

		_httpPort = new Text(bot, "Http Port:");

		_defaultLaunchSettings = new Radio(bot, "Default Launch Settings");
		_customLaunchSettings = new Radio(bot, "Custom Launch Settings");
		_useDeveloperMode = new CheckBox(bot, "Use developer mode");
	}

	public Radio getCustomLaunchSettings() {
		return _customLaunchSettings;
	}

	public Radio getDefaultLaunchSettings() {
		return _defaultLaunchSettings;
	}

	public Text getHttpPort() {
		return _httpPort;
	}

	public CheckBox getUseDeveloperMode() {
		return _useDeveloperMode;
	}

	private Radio _customLaunchSettings;
	private Radio _defaultLaunchSettings;
	private Text _httpPort;
	private CheckBox _useDeveloperMode;

}