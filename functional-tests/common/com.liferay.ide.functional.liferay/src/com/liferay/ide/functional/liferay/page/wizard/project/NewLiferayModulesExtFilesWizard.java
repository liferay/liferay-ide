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
import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.Tree;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Rui Wang
 */
public class NewLiferayModulesExtFilesWizard extends NewProjectWizard {

	public NewLiferayModulesExtFilesWizard(SWTBot bot) {
		super(bot, 2);
	}

	public ComboBox getModuleExtProjectName() {
		return new ComboBox(getShell().bot(), MODULE_EXT_PROJECT_NAME);
	}

	public Text getOrigialModule() {
		return new Text(getShell().bot(), ORIGINAL_MODULE);
	}

	public Tree getTypes() {
		return new Tree(getShell().bot());
	}

	public void selectType(String category, String type) {
		getTypes().selectTreeItem(category, type);
	}

}