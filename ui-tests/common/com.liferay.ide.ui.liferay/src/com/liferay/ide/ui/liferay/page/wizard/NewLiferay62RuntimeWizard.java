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

package com.liferay.ide.ui.liferay.page.wizard;

import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class NewLiferay62RuntimeWizard extends Wizard {

	public NewLiferay62RuntimeWizard(SWTWorkbenchBot bot) {
		super(bot, 3);

		_location = new Text(bot, LIFERAY_TOMCAT_DIRECTORY);
		_name = new Text(bot, NAME);
	}

	public Text getLocation() {
		return _location;
	}

	public Text getName() {
		return _name;
	}

	private Text _location;
	private Text _name;

}