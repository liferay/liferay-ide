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
import com.liferay.ide.ui.swtbot.eclipse.page.ProjectExplorerView;
import com.liferay.ide.ui.swtbot.eclipse.page.ServersView;
import com.liferay.ide.ui.swtbot.eclipse.page.TextDialog;
import com.liferay.ide.ui.swtbot.page.BasePageObject;
import com.liferay.ide.ui.swtbot.page.Browser;
import com.liferay.ide.ui.swtbot.page.Dialog;
import com.liferay.ide.ui.swtbot.page.Editor;
import com.liferay.ide.ui.swtbot.page.Menu;
import com.liferay.ide.ui.swtbot.page.Tree;
import com.liferay.ide.ui.swtbot.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Vicky Wang
 */
public class Eclipse extends BasePageObject {

	public Eclipse(SWTWorkbenchBot bot) {
		super(bot);

		_packageExporerView = new PackageExplorerView(bot);
		_welcomeView = new View(bot, WELCOME);
		_projectTree = new Tree(bot);
		_fileMenu = new Menu(bot, FILE);

		String[] otherLabel = {WINDOW, SHOW_VIEW, OTHER};
		String[] preferencesLabel = {WINDOW, PREFERENCES};

		_preferencesMenu = new Menu(bot, preferencesLabel);

		_otherMenu = new Menu(bot, otherLabel);

		_showViewDialog = new TextDialog(bot);
		_projectExplorerView = new ProjectExplorerView(bot);
		_serversView = new ServersView(bot);
		_browser = new Browser(bot);
	}

	public void closeShell(String title) {
		Dialog shell = new Dialog(bot, title);

		shell.closeIfOpen();
	}

	public Browser getBrowser() {
		return _browser;
	}

	public Editor getEditor(String fileName) {
		return new Editor(bot, fileName);
	}

	public Menu getFileMenu() {
		return _fileMenu;
	}

	public Menu getOtherMenu() {
		return _otherMenu;
	}

	public PackageExplorerView getPackageExporerView() {
		return _packageExporerView;
	}

	public Menu getPreferencesMenu() {
		return _preferencesMenu;
	}

	public ProjectExplorerView getProjectExplorerView() {
		return _projectExplorerView;
	}

	public Tree getProjectTree() {
		return _projectTree;
	}

	public ServersView getServersView() {
		return _serversView;
	}

	public TextDialog getShowViewDialog() {
		return _showViewDialog;
	}

	public View getWelcomeView() {
		return _welcomeView;
	}

	public PackageExplorerView showPackageExporerView() {
		try {
			_packageExporerView.show();
		}
		catch (Exception e) {
			_otherMenu.click();
			_showViewDialog.getText().setText(PACKAGE_EXPLORER);

			_showViewDialog.confirm();

			_packageExporerView.show();
		}

		return _packageExporerView;
	}

	public ServersView showServersView() {
		try {
			_serversView.show();
		}
		catch (Exception e) {
			_otherMenu.click();

			_showViewDialog.getText().setText(SERVERS);

			_showViewDialog.confirm();

			_serversView.show();
		}

		return _serversView;
	}

	private Browser _browser;
	private Menu _fileMenu;
	private Menu _otherMenu;
	private PackageExplorerView _packageExporerView;
	private Menu _preferencesMenu;
	private ProjectExplorerView _projectExplorerView;
	private Tree _projectTree;
	private ServersView _serversView;
	private TextDialog _showViewDialog;
	private View _welcomeView;

}