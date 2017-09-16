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

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ashley Yuan
 */
public class NewSourceFolderWizard extends Wizard {

	public NewSourceFolderWizard(SWTWorkbenchBot bot) {
		super(bot, 2);

		_projectName = new Text(bot, PROJECT_NAME);
		_folderName = new Text(bot, FOLDER_NAME);
		_updateExclusionFilters = new CheckBox(bot, UPDATE_EXCLUSION_FILTERS);
		_ignoreOptionCompileProblems = new CheckBox(bot, IGNORE_OPTIONAL_COMPILE_PROBLEMS);
	}

	public Text getFolderName() {
		return _folderName;
	}

	public CheckBox getIgnoreOptionCompileProblems() {
		return _ignoreOptionCompileProblems;
	}

	public Text getProjectName() {
		return _projectName;
	}

	public CheckBox getUpdateExclusionFilters() {
		return _updateExclusionFilters;
	}

	public void newSourceFolder(String folderName) {
		newSourceFolder(StringPool.BLANK, folderName, false, false);
	}

	public void newSourceFolder(
		String projectNameValue, String folderNameValue, boolean updateExclusionFiltersValue,
		boolean ignoreOptionCompileProblemsValue) {

		if ((_projectName != null) && _projectName.equals(StringPool.BLANK)) {
			_projectName.setText(projectNameValue);
		}

		_folderName.setText(folderNameValue);

		if (updateExclusionFiltersValue) {
			_updateExclusionFilters.select();
		}

		if (ignoreOptionCompileProblemsValue) {
			_ignoreOptionCompileProblems.select();
		}
	}

	private Text _folderName;
	private CheckBox _ignoreOptionCompileProblems;
	private Text _projectName;
	private CheckBox _updateExclusionFilters;

}