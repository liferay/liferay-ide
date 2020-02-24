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

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Rui Wang
 */
public class NewLiferaySpringMvcPortletWizard extends NewProjectWizard {

	public NewLiferaySpringMvcPortletWizard(SWTBot bot) {
		super(bot, 2);
	}

	public ComboBox getFramework() {
		return new ComboBox(getShell().bot(), FRAMEWORK);
	}

	public ComboBox getFrameworkDependencies() {
		return new ComboBox(bot, FRAMEWORK_DEPENDENCIES);
	}

	public Text getProjectName() {
		return new Text(getShell().bot(), PROJECT_NAME);
	}

	public ComboBox getViewType() {
		return new ComboBox(bot, VIEW_TYPE);
	}

	public void setFramework(String framework) {
		getFramework().setSelection(framework);
	}

	public void setFrameworkDependencies(String frameworkDependencies) {
		getFrameworkDependencies().setSelection(frameworkDependencies);
	}

	public void setProjectName(String name) {
		getProjectName().setText(name);
	}

	public void setViewType(String viewType) {
		getViewType().setSelection(viewType);
	}

}