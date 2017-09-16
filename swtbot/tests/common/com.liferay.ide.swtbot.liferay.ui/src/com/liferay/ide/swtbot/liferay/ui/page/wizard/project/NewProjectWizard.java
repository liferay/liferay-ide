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

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class NewProjectWizard extends Wizard {

	public NewProjectWizard(SWTWorkbenchBot bot) {
		this(bot, -1);
	}

	public NewProjectWizard(SWTWorkbenchBot bot, int index) {
		this(bot, StringPool.BLANK, index);
	}

	public NewProjectWizard(SWTWorkbenchBot bot, String title, int index) {
		super(bot, title, index);

		_projectName = new Text(bot, PROJECT_NAME);
		_addToWorkingSet = new CheckBox(bot, ADD_PROJECT_TO_WORKING_SET);
		_workingSets = new ComboBox(bot, WORKING_SET);
		_location = new Text(bot, LOCATION_WITH_COLON);
		_useDefaultLocation = new CheckBox(bot, USE_DEFAULT_LOCATION);
		_buildTypes = new ComboBox(bot, BUILD_TYPE);
	}

	public void createProject(String projectName) {
		createProject(projectName, false, StringPool.BLANK);
	}

	public void createProject(String name, boolean workingSetBox, String set) {
		_projectName.setText(name);

		if (workingSetBox) {
			_addToWorkingSet.select();

			_workingSets.setSelection(set);
		}
	}

	public CheckBox getAddToWorkingSet() {
		return _addToWorkingSet;
	}

	public ComboBox getBuildTypes() {
		return _buildTypes;
	}

	public Text getLocation() {
		return _location;
	}

	public Text getProjectName() {
		return _projectName;
	}

	public CheckBox getUseDefaultLocation() {
		return _useDefaultLocation;
	}

	public ComboBox getWorkingSets() {
		return _workingSets;
	}

	private CheckBox _addToWorkingSet;
	private ComboBox _buildTypes;
	private Text _location;
	private Text _projectName;
	private CheckBox _useDefaultLocation;
	private ComboBox _workingSets;

}