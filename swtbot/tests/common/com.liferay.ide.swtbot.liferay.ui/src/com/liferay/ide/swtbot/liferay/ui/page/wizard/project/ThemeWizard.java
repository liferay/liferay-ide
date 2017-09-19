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

package com.liferay.ide.swtbot.liferay.ui.page.wizard.project;

import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 */
public class ThemeWizard extends Wizard {

	public ThemeWizard(SWTWorkbenchBot bot) {
		super(bot, 0);

		_themeParentTypes = new ComboBox(bot, THEME_PARENT);
		_themeFrameworkTypes = new ComboBox(bot, FARMEWORK_TYPE);
	}

	public void setParentFramework(String parent, String framework) {
		_themeParentTypes.setSelection(parent);
		_themeFrameworkTypes.setSelection(framework);
	}

	private ComboBox _themeFrameworkTypes;
	private ComboBox _themeParentTypes;

}