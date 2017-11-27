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

import com.liferay.ide.ui.swtbot.eclipse.page.PackageExplorerView;
import com.liferay.ide.ui.swtbot.eclipse.page.TextDialog;
import com.liferay.ide.ui.swtbot.page.BasePageObject;
import com.liferay.ide.ui.swtbot.page.Editor;
import com.liferay.ide.ui.swtbot.page.Menu;
import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Vicky Wang
 */
public class Eclipse extends BasePageObject {

	public Eclipse(SWTWorkbenchBot bot) {
		super(bot);

		label = bot.activeShell().getText();

		_packageExporerView = new PackageExplorerView(bot);
		_welcomeView = new View(bot, WELCOME);
	}

	public Editor getEditor(String fileName) {
		return new Editor((SWTWorkbenchBot)bot, fileName);
	}

	public Menu getFileMenu() {
		return new Menu(bot.shell(label).bot(), FILE);
	}

	public String getLabel() {
		return label;
	}

	public Menu getOtherMenu() {
		String[] otherLabel = {WINDOW, SHOW_VIEW, OTHER};

		return new Menu(bot.shell(label).bot(), otherLabel);
	}

	public PackageExplorerView getPackageExporerView() {
		return _packageExporerView;
	}

	public String getPerspectiveLabel() {
		return ((SWTWorkbenchBot)bot).activePerspective().getLabel();
	}

	public Menu getPreferencesMenu() {
		String[] preferencesLabel = {WINDOW, PREFERENCES};

		return new Menu(bot.shell(label).bot(), preferencesLabel);
	}

	public TextDialog getShowViewDialog() {
		return new TextDialog(bot);
	}

	public View getWelcomeView() {
		return _welcomeView;
	}

	public void showServersView() {
		getOtherMenu().click();

		Text text = getShowViewDialog().getText();

		text.setText(SERVERS);

		getShowViewDialog().confirm(OPEN);
	}

	public void waitUntil(ICondition condition) {
		waitUntil(condition, 60 * 1000);
	}

	public void waitUntil(ICondition condition, long timeout) {
		bot.waitUntil(condition, timeout);
	}

	private PackageExplorerView _packageExporerView;
	private View _welcomeView;

}