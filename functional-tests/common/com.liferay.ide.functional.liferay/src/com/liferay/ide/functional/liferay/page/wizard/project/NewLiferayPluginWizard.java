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

package com.liferay.ide.functional.liferay.page.wizard.project;

import com.liferay.ide.functional.swtbot.page.ComboBox;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 */
public class NewLiferayPluginWizard extends NewProjectWizard {

	public NewLiferayPluginWizard(SWTBot bot) {
		super(bot);
	}

	public ComboBox getPluginTypes() {
		return new ComboBox(getShell().bot(), PLUGIN_TYPE);
	}

	public void setPluginType(String type) {
		getPluginTypes().setSelection(type);
	}

}