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

import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.functional.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class ImportLiferayModuleWizard extends Wizard {

	public ImportLiferayModuleWizard(SWTBot bot) {
		super(bot, 2);
	}

	public ToolbarButtonWithTooltip getBrowseBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), BROWSE);
	}

	public Text getBuildType() {
		return new Text(getShell().bot(), BUILD_TYPE);
	}

	public Text getLocation() {
		return new Text(getShell().bot(), LOCATION_WITH_COLON);
	}

}