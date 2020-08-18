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

package com.liferay.ide.functional.swtbot;

import com.liferay.ide.functional.swtbot.eclipse.page.TextDialog;
import com.liferay.ide.functional.swtbot.page.BasePageObject;
import com.liferay.ide.functional.swtbot.page.Editor;
import com.liferay.ide.functional.swtbot.page.Menu;
import com.liferay.ide.functional.swtbot.page.Shell;
import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.View;

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

		SWTBotShell activeShell = bot.activeShell();

		label = activeShell.getText();

		_shell = new Shell(bot, label);

		_welcomeView = new View(bot, WELCOME);
	}

	public void clickFileMenu(String menu) {
		Menu fileMenu = getFileMenu();

		fileMenu.clickMenu(menu);
	}

	public void clickFileMenu(String menu, String submenu) {
		Menu fileMenu = getFileMenu();

		fileMenu.clickMenu(menu, submenu);
	}

	public void clickInstallMenu() {
		Menu installMenu = getInstallMenu();

		installMenu.click();
	}

	public Editor getEditor(String fileName) {
		return new Editor((SWTWorkbenchBot)bot, fileName);
	}

	public Menu getFileMenu() {
		SWTBotShell swtBotShell = _getShell(label);

		return new Menu(swtBotShell.bot(), FILE);
	}

	public Menu getInstallMenu() {
		String[] installLabel = {HELP, INSTALL_NEW_SOFTWARE};

		SWTBotShell swtBotShell = _getShell(label);

		return new Menu(swtBotShell.bot(), installLabel);
	}

	public String getLabel() {
		return label;
	}

	public Menu getOtherMenu() {
		String[] otherLabel = {WINDOW, SHOW_VIEW, OTHER};

		SWTBotShell swtBotShell = _getShell(label);

		return new Menu(swtBotShell.bot(), otherLabel);
	}

	public String getPerspectiveLabel() {
		SWTWorkbenchBot swtBot = (SWTWorkbenchBot)bot;

		SWTBotPerspective botActivePerspective = swtBot.activePerspective();

		return botActivePerspective.getLabel();
	}

	public Menu getPreferencesMenu() {
		String[] preferencesLabel = {WINDOW, PREFERENCES};

		SWTBotShell swtBotShell = _getShell(label);

		return new Menu(swtBotShell.bot(), preferencesLabel);
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

	public void maximize() {
		Shell shell = new Shell(bot);

		SWTBotShell activeShell = _getShell(shell.getLabel());

		activeShell.maximize(true);
	}

	public void showConsoleView() {
		showView(CONSOLE);
	}

	public void showErrorLogView() {
		showView(ERROR_LOG);
	}

	public void showProgressView() {
		showView(PROGRESS);
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

		showViewDialog.setDialog(viewName);

		sleep(3000);

		showViewDialog.confirm(OPEN);

		sleep(3000);
	}

	private SWTBotShell _getShell(String label) {
		return bot.shell(label);
	}

	private TextDialog _getShowViewDialog() {
		return new TextDialog(bot);
	}

	private Shell _shell;
	private View _welcomeView;

}