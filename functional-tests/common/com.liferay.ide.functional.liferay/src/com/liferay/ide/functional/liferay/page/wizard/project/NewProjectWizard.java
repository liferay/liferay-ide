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
import com.liferay.ide.functional.swtbot.page.ComboBox;
import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class NewProjectWizard extends Wizard {

	public NewProjectWizard(SWTBot bot) {
		super(bot, -1);
	}

	public NewProjectWizard(SWTBot bot, int index) {
		super(bot, index);
	}

	public void deselectUseDefaultLocation() {
		getUseDefaultLocation().deselect();
	}

	public CheckBox getAddToWorkingSet() {
		return new CheckBox(getShell().bot(), ADD_PROJECT_TO_WORKING_SET);
	}

	public ComboBox getBuildTypes() {
		return new ComboBox(getShell().bot(), BUILD_TYPE);
	}

	public ComboBox getLiferayVersions() {
		return new ComboBox(getShell().bot(), LIFERAY_VERSION);
	}

	public Text getLocation() {
		return new Text(getShell().bot(), LOCATION_WITH_COLON);
	}

	public ComboBox getProductVersions() {
		return new ComboBox(getShell().bot(), PRODUCT_VERSION);
	}

	public Text getProjectName() {
		return new Text(getShell().bot(), PROJECT_NAME);
	}

	public CheckBox getUseDefaultLocation() {
		return new CheckBox(getShell().bot(), USE_DEFAULT_LOCATION);
	}

	public ComboBox getWorkingSets() {
		return new ComboBox(getShell().bot(), WORKING_SET);
	}

	public void selectUseDefaultLocation() {
		getUseDefaultLocation().select();
	}

	public void setBuildType(String plugin) {
		getBuildTypes().setSelection(plugin);
	}

	public void setLiferayVersion(String version) {
		getLiferayVersions().setSelection(version);
	}

	public void setLocation(String location) {
		getLocation().setText(location);
	}

	public void setProductVersion(String version) {
		getProductVersions().setSelection(version);
	}

	public void setProjectName(String name) {
		getProjectName().setText(name);
	}

}