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

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Li Lu
 * @author Ying Xu
 */
public class LiferayProjectFromExistSourceWizard extends Wizard {

	public LiferayProjectFromExistSourceWizard(SWTWorkbenchBot bot) {
		super(bot, 2);

		_sdkDirectory = new Text(bot, SDK_DIRECTORY);
		_sdkVersion = new Text(bot, SDK_VERSION);
		_browseSdkDirectoryBtn = new ToolbarButtonWithTooltip(bot, BROWSE);
		_selectAllBtn = new Button(bot, SELECT_ALL);
		_deselectAllBtn = new Button(bot, DESELECT_ALL);
		_refreshBtn = new Button(bot, REFRESH);
	}

	public ToolbarButtonWithTooltip getBrowseSdkDirectoryBtn() {
		return _browseSdkDirectoryBtn;
	}

	public Button getDeselectAllBtn() {
		return _deselectAllBtn;
	}

	public Button getRefreshBtn() {
		return _refreshBtn;
	}

	public Text getSdkDirectory() {
		return _sdkDirectory;
	}

	public Text getSdkVersion() {
		return _sdkVersion;
	}

	public Button getSelectAllBtn() {
		return _selectAllBtn;
	}

	public void importProject(String path) {
		_sdkDirectory.setText(path);

		finish();
	}

	private ToolbarButtonWithTooltip _browseSdkDirectoryBtn;
	private Button _deselectAllBtn;
	private Button _refreshBtn;
	private Text _sdkDirectory;
	private Text _sdkVersion;
	private Button _selectAllBtn;

}