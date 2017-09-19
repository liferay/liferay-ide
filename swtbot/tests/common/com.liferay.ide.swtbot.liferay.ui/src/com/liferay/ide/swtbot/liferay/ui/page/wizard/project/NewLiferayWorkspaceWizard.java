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
import com.liferay.ide.swtbot.ui.page.Text;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewLiferayWorkspaceWizard extends NewProjectWizard {

	public NewLiferayWorkspaceWizard(SWTWorkbenchBot bot) {
		super(bot, 2);

		_serverName = new Text(bot, SERVER_NAME);
		_bundleUrl = new Text(bot, BUNDLE_URL);
		_downloadLiferayBundle = new CheckBox(bot, DOWNLOAD_LIFERAY_BUNDLE);
	}

	public Text getBundleUrl() {
		return _bundleUrl;
	}

	public CheckBox getDownloadLiferayBundle() {
		return _downloadLiferayBundle;
	}

	public Text getServerName() {
		return _serverName;
	}

	private Text _bundleUrl;
	private CheckBox _downloadLiferayBundle;
	private Text _serverName;

}