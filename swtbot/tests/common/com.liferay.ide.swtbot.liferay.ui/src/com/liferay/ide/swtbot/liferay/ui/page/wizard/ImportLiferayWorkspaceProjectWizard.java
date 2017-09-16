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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class ImportLiferayWorkspaceProjectWizard extends Wizard {

	public ImportLiferayWorkspaceProjectWizard(SWTWorkbenchBot bot) {
		super(bot, IMPORT_LIFERAY_WORKSPACE, 2);

		_buildType = new Text(bot, BUILD_TYPE);
		_location = new Text(bot, WORKSPACE_LOCATION);
		_downloadLiferaybundle = new CheckBox(bot, DOWNLOAD_LIFERAY_BUNDLE);
		_serverName = new Text(bot, SERVER_NAME);
		_bundleUrl = new Text(bot, BUNDLE_URL);
		_addProjectToWorkingSet = new CheckBox(bot, ADD_PROJECT_TO_WORKING_SET);
	}

	public CheckBox getAddProjectToWorkingSet() {
		return _addProjectToWorkingSet;
	}

	public Text getBuildTypeText() {
		return _buildType;
	}

	public Text getBundleUrl() {
		return _bundleUrl;
	}

	public CheckBox getDownloadLiferaybundle() {
		return _downloadLiferaybundle;
	}

	public Text getServerName() {
		return _serverName;
	}

	public Text getWorkspaceLocation() {
		return _location;
	}

	private CheckBox _addProjectToWorkingSet;
	private Text _buildType;
	private Text _bundleUrl;
	private CheckBox _downloadLiferaybundle;
	private Text _location;
	private Text _serverName;

}