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

package com.liferay.ide.ui.liferay.page.view;

import com.liferay.ide.ui.swtbot.page.Button;
import com.liferay.ide.ui.swtbot.page.TooltipButton;
import com.liferay.ide.ui.swtbot.page.Canvas;
import com.liferay.ide.ui.swtbot.page.CheckBox;
import com.liferay.ide.ui.swtbot.page.ComboBox;
import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.ui.swtbot.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class CodeUpgradeView extends View {

	public CodeUpgradeView(SWTWorkbenchBot bot) {
		super(bot, LIFERAY_CODE_UPGRADE);

		_gear = new Gear(bot);
	}

	public Button getAddServerBtn() {
		return new Button(bot, ADD_SERVER_WITH_DOT);
	}

	public TooltipButton getAutomaticallyCorrectProblemsBtn() {
		return new TooltipButton(bot, AUTOMATICALLY_CORRECT_PROBLEMS);
	}

	public Button getBrowseBtn() {
		return new Button(bot, BROWSE_WITH_DOT);
	}

	public Button getBuildBtn() {
		return new Button(bot, BUILD_WITH_DOT);
	}

	public Button getBuildServicesBtn() {
		return new Button(bot, BUILD_SERVICES);
	}

	public Text getBundleUrl() {
		return new Text(bot, BUNDLE_URL_UPCASE);
	}

	public Button getClearResultsBtn() {
		return new Button(bot, CLEAR_RESULTS);
	}

	public TooltipButton getCollapseAllBtn() {
		return new TooltipButton(bot, COLLAPSE_ALL);
	}

	public Text getConvertedProjectLocation() {
		return new Text(bot, CONVERTED_PROJECT_LOCATION);
	}

	public Button getDeselectAllBtn() {
		return new Button(bot, DESELECT_ALL);
	}

	public CheckBox getDownloadLiferayBundleRecommended() {
		return new CheckBox(bot, DOWNLOAD_LIFERAY_BUNDLE_RECOMMENDED);
	}

	public TooltipButton getExpandAllBtn() {
		return new TooltipButton(bot, EXPAND_ALL);
	}

	public TooltipButton getFindBreakingChangesBtn() {
		return new TooltipButton(bot, FIND_BREAKING_CHANGES);
	}

	public Gear getGear() {
		return _gear;
	}

	public Button getImportProjectsBtn() {
		return new Button(bot, IMPORT_PROJECTS);
	}

	public ComboBox getLiferayServerName() {
		return new ComboBox(bot, LIFERAY_SERVER_NAME);
	}

	public TooltipButton getOpenIgnoredListBtn() {
		return new TooltipButton(bot, OPEN_IGNORED_LIST);
	}

	public Text getPluginsSdkOrMavenProjectRootLocation() {
		return new Text(bot, PLUGINS_SDK_OR_MAVEN_PROJECT_ROOT_LOCATION);
	}

	public Button getRefreshResultsBtn() {
		return new Button(bot, REFRESH_RESULTS);
	}

	public ToolbarButtonWithTooltip getRestartUpgradeBtn() {
		return new ToolbarButtonWithTooltip(bot, RESTART_UPGRADE);
	}

	public Button getSelectAllBtn() {
		return new Button(bot, SELECT_ALL);
	}

	public ComboBox getSelectMigrateLayouts() {
		return new ComboBox(bot, SELECT_MIGRATE_LAYOUT);
	}

	public Button getSelectProjectsBtn() {
		return new Button(bot, SELECT_PROJECTS);
	}

	public Text getServerName() {
		return new Text(bot, SERVER_NAME_UPCASE);
	}

	public ToolbarButtonWithTooltip getShowAllPagesBtn() {
		return new ToolbarButtonWithTooltip(bot, SHOW_ALL_PAGES);
	}

	public Button getUpgradeBtn() {
		return new Button(bot, UPGRADE_WITH_DOT);
	}

	public Button getUpgradeSelectedBtn() {
		return new Button(bot, UPGRADE_SELECTED);
	}

	public class Gear extends Canvas {

		public Gear(SWTWorkbenchBot bot) {
			super(bot, "GearControl");
		}

		public void clickGear(int i) {
			click(_x + _step * i, _y);
		}

		private int _step = 64;
		private int _x = 52;
		private int _y = 52;

	}

	private Gear _gear;

}