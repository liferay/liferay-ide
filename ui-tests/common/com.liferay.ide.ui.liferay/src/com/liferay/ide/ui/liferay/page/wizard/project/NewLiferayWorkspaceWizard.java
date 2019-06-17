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
import com.liferay.ide.ui.swtbot.page.ComboBox;
import com.liferay.ide.ui.swtbot.page.Text;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewLiferayWorkspaceWizard extends NewProjectWizard {

	public NewLiferayWorkspaceWizard(SWTBot bot) {
		super(bot, 2);
	}

	public Text getBundleUrl() {
		return new Text(getShell().bot(), BUNDLE_URL);
	}

	public CheckBox getDownloadLiferayBundle() {
		return new CheckBox(getShell().bot(), DOWNLOAD_LIFERAY_BUNDLE);
	}

	public CheckBox getIndexSources() {
		return new CheckBox(getShell().bot(), INDEX_SOURCES);
	}

	public Text getServerName() {
		return new Text(getShell().bot(), SERVER_NAME);
	}

	public ComboBox getTargetPlatform() {
		return new ComboBox(getShell().bot(), TARGET_PLATFORM);
	}

	public void selectIndexSources() {
		getIndexSources().select();
	}

	public void setBundleUrl(String url) {
		getBundleUrl().setText(url);
	}

	public void setServerName(String serverName) {
		getServerName().setText(serverName);
	}

}