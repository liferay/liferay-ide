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

package com.liferay.ide.ui.liferay.page.editor;

import com.liferay.ide.ui.swtbot.page.CheckBox;
import com.liferay.ide.ui.swtbot.page.Editor;
import com.liferay.ide.ui.swtbot.page.Radio;
import com.liferay.ide.ui.swtbot.page.Text;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class ServerEditor extends Editor {

	public ServerEditor(SWTWorkbenchBot bot) {
		super(bot);
	}

	public Radio getCustomLaunchSettings() {
		return new Radio(getPart().bot(), "Custom Launch Settings");
	}

	public Radio getDefaultLaunchSettings() {
		return new Radio(getPart().bot(), "Default Launch Settings");
	}

	public Text getHttpPort() {
		return new Text(getPart().bot(), "Http Port:");
	}

	public Text getPassword() {
		return new Text(getPart().bot(), "Password:");
	}

	public CheckBox getUseDeveloperMode() {
		return new CheckBox(getPart().bot(), "Use developer mode");
	}

}