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

package com.liferay.ide.functional.liferay.page.wizard;

import com.liferay.ide.functional.swtbot.page.CheckBox;
import com.liferay.ide.functional.swtbot.page.ComboBox;
import com.liferay.ide.functional.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 */
public class CreateLiferayHookConfigurationWizard extends Wizard {

	public CreateLiferayHookConfigurationWizard(SWTBot bot) {
		super(bot, 0);
	}

	public CheckBox getCustomJsps() {
		return new CheckBox(getShell().bot(), CUSTOM_JSPS);
	}

	public ComboBox getHookPluginProjectComboBox() {
		return new ComboBox(getShell().bot(), HOOK_PLUGIN_PROJECT);
	}

	public ComboBox getHookProjects() {
		return new ComboBox(getShell().bot(), HOOK_PLUGIN_PROJECT);
	}

	public CheckBox getLanguageProperties() {
		return new CheckBox(getShell().bot(), LANGUAGE_PROPERTIES);
	}

	public CheckBox getPortalProperties() {
		return new CheckBox(getShell().bot(), PORTAL_PROPERTIES);
	}

	public CheckBox getServices() {
		return new CheckBox(getShell().bot(), SERVICES);
	}

}