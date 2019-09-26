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

import com.liferay.ide.functional.swtbot.page.CheckBox;
import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.ToolbarButtonWithTooltip;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Rui Wang
 */
public class NewLiferayModulesExtWizard extends NewProjectWizard {

	public NewLiferayModulesExtWizard(SWTBot bot) {
		super(bot, 2);
	}

	public ToolbarButtonWithTooltip getAddFilesFromOriginalModuleBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), ADD_FILES_FROM_ORIGINAL_MODULE_TO_OVERRIDE);
	}

	public ToolbarButtonWithTooltip getBrowseBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), BROWSE, 1);
	}

	public CheckBox getLaunchModulesExtFiles() {
		return new CheckBox(getShell().bot(), LAUCH_LIFERAY_MODULE_EXT_FILES_WIZARD_AFTER_PROJECT_IS_CREATED);
	}

	public Text getOrigialModuleName() {
		return new Text(getShell().bot(), ORIGINAL_MODULE_NAME);
	}

	public Text getOrigialModuleVersion() {
		return new Text(getShell().bot(), ORIGINAL_MODULE_VERSION);
	}

}