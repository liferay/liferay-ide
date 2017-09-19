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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewLiferay7RuntimeWizard extends Wizard {

	public NewLiferay7RuntimeWizard(SWTWorkbenchBot bot) {
		super(bot, 3);

		_location = new Text(bot, LIFERAY_PORTAL_BUNDLE_DIRECTORY);
		_type = new Text(bot, DETECTED_PORTAL_BUNDLE_TYPE);
		_name = new Text(bot, NAME);
	}

	public Text getLocation() {
		return _location;
	}

	public Text getName() {
		return _name;
	}

	public Text getPortalBundleType() {
		return _type;
	}

	private Text _location;
	private Text _name;
	private Text _type;

}