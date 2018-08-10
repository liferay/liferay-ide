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

package com.liferay.ide.ui.liferay.page.wizard.project;

import com.liferay.ide.ui.swtbot.page.CheckBox;
import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.ui.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 */
public class ImportLiferayWorkspaceWizard extends Wizard {

	public ImportLiferayWorkspaceWizard(SWTBot bot) {
		super(bot, 2);
	}

	public void deselectDownloadLiferayBundle() {
		getDownloadLiferayBundle().deselect();
	}

	public CheckBox getAddProjectToWorkingSet() {
		return new CheckBox(getShell().bot(), ADD_PROJECT_TO_WORKING_SET);
	}

	public ToolbarButtonWithTooltip getBrowseLocationBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), BROWSE);
	}

	public Text getBuildTypeText() {
		return new Text(getShell().bot(), BUILD_TYPE);
	}

	public Text getBundleUrl() {
		return new Text(getShell().bot(), BUNDLE_URL);
	}

	public CheckBox getDownloadLiferayBundle() {
		return new CheckBox(getShell().bot(), DOWNLOAD_LIFERAY_BUNDLE);
	}

	public Text getServerName() {
		return new Text(getShell().bot(), SERVER_NAME);
	}

	public Text getWorkspaceLocation() {
		return new Text(getShell().bot(), WORKSPACE_LOCATION);
	}

	public void selectDownloadLiferayBundle() {
		getDownloadLiferayBundle().select();
	}

	public void setBundleUrl(String url) {
		getBundleUrl().setText(url);
	}

}