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

import com.liferay.ide.ui.swtbot.page.ComboBox;
import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Lily Li
 */
public class NewLiferayUpgradePlanWizard extends Wizard {

	public NewLiferayUpgradePlanWizard(SWTBot bot) {
		super(bot);
	}

	public ComboBox getCurrentLiferayVersion() {
		return new ComboBox(getShell().bot(), CURRENT_LIFERAY_VERSION);
	}

	public Text getName() {
		return new Text(getShell().bot(), NAME_WITH_COLON);
	}

	public ComboBox getTargetLiferayVersion() {
		return new ComboBox(getShell().bot(), TARGET_LIFERAY_VERSION);
	}

	public ComboBox getUpgradePlanOutline() {
		return new ComboBox(getShell().bot(), UPGRADE_PLAN_OUTLINE);
	}

	public void setCurrentLiferayVersion(String version) {
		getCurrentLiferayVersion().setSelection(version);
	}

	public void setName(String name) {
		getName().setText(name);
	}

	public void setTargetLiferayVersion(String version) {
		getTargetLiferayVersion().setSelection(version);
	}

	public void setUpgradePlanOutline(String planOutline) {
		getUpgradePlanOutline().setSelection(planOutline);
	}

}