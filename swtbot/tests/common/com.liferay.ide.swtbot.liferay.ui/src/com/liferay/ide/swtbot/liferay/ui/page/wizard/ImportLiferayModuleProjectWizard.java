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
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ashley Yuan
 */
public class ImportLiferayModuleProjectWizard extends Wizard {

	public ImportLiferayModuleProjectWizard(SWTWorkbenchBot bot) {
		super(bot, IMPORT_LIFERAY_MODULE_PROJECT, 2);

		_location = new Text(bot, LOCATION_WITH_COLON);
		_buildType = new Text(bot, BUILD_TYPE);
		_browseBtn = new ToolbarButtonWithTooltip(bot, BROWSE);
	}

	public ToolbarButtonWithTooltip getBrowseBtn() {
		return _browseBtn;
	}

	public Text getBuildType() {
		return _buildType;
	}

	public Text getLocation() {
		return _location;
	}

	private ToolbarButtonWithTooltip _browseBtn;
	private Text _buildType;
	private Text _location;

}