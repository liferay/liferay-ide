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

package com.liferay.ide.ui.swtbot;

import com.liferay.ide.ui.swtbot.eclipse.page.TextDialog;
import com.liferay.ide.ui.swtbot.page.BasePageObject;
import com.liferay.ide.ui.swtbot.page.Editor;
import com.liferay.ide.ui.swtbot.page.Menu;
import com.liferay.ide.ui.swtbot.page.Shell;
import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Vicky Wang
 * @author Lily Li
 */
public class Eclipse extends BasePageObject {

	public Eclipse(SWTWorkbenchBot bot) {
		super(bot);

		SWTBotShell botActiveShell = bot.activeShell();

		label = botActiveShell.getText();

		_shell = new Shell(bot, label);

		_welcomeView = new View(bot, WELCOME);
	}

	public Editor getEditor(String fileName) {
		return new Editor((SWTWorkbenchBot)bot, fileName);
	}

	public Menu getFileMenu() {
		return new Menu(_botShell.bot(), FILE);
	}
	
	public void clickFileMenu(String menu, String subMenu) {
		getFileMenu().clickMenu(menu,subMenu);
	}

	public void clickFileMenu(String menu) {
		getFileMenu().clickMenu(menu);
	}

	public Menu getInstallMenu() {
		String[] installLabel = {HELP, INSTALL_NEW_SOFTWARE};

		return new Menu(_botShell.bot(), installLabel);
	}
	
	public void clickInstallMenu() {
		getInstallMenu().click();
	}

	public String getLabel() {
		return label;
	}

	public Menu getOtherMenu() {
		String[] otherLabel = {WINDOW, SHOW_VIEW, OTHER};

		return new Menu(_botShell.bot(), otherLabel);
	}

	public String getPerspectiveLabel() {
		SWTBotPerspective botActivePerspective = ((SWTWorkbenchBot)bot).activePerspective();

		return botActivePerspective.getLabel();
	}

	public Menu getPreferencesMenu() {
		String[] preferencesLabel = {WINDOW, PREFERENCES};

		return new Menu(_botShell.bot(), preferencesLabel);
	}

	public Text getQuickAccess() {
		return new Text(bot, "Quick Access", true);
	}

	public Shell getShell() {
		return _shell;
	}

	public View getWelcomeView() {
		return _welcomeView;
	}

	public void showErrorLogView() {
		showView(ERROR_LOG);
	}

	public void showServersView() {
		showView(SERVERS);
	}

	public void waitUntil(ICondition condition) {
		waitUntil(condition, 60 * 1000);
	}

	public void waitUntil(ICondition condition, long timeout) {
		bot.waitUntil(condition, timeout);
	}

	protected void showView(String viewName) {
		getOtherMenu().click();

		TextDialog showViewDialog = _getShowViewDialog();

		Text showViewText = showViewDialog.getText();

		showViewText.setText(viewName);

		sleep(3000);

		showViewDialog.confirm(OPEN);

		sleep(3000);
	}

	private TextDialog _getShowViewDialog() {
		return new TextDialog(bot);
	}

	private SWTBotShell _botShell = bot.shell(label);
	private Shell _shell;
	private View _welcomeView;

}