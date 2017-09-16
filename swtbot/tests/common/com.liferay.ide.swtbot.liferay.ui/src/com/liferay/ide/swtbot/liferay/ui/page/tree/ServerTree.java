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

package com.liferay.ide.swtbot.liferay.ui.page.tree;

import com.liferay.ide.swtbot.ui.eclipse.page.AddAndRemoveDialog;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.TreeItem;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Li Lu
 * @author Ying Xu
 */
public class ServerTree extends TreeItem {

	public ServerTree(SWTWorkbenchBot bot) {
		this(bot, StringPool.BLANK);
	}

	public ServerTree(SWTWorkbenchBot bot, String serverNameValue) {
		super(bot);

		_serverName = serverNameValue;

		_addAndRemoveDialog = new AddAndRemoveDialog(bot);
	}

	public void addALL() {
		doAction(ADD_AND_REMOVE);

		_addAndRemoveDialog.getAddAllBtn().click();

		_addAndRemoveDialog.confirm();
	}

	public boolean checkConsoleHasMsg(String expectedMsg, int timeout) {
		long timeoutExpiredMs = System.currentTimeMillis() + timeout;

		while (true) {
			sleep();

			String content = bot.styledText().getText();

			if (content.contains(expectedMsg) || content.matches(expectedMsg)) {
				return true;
			}

			if (System.currentTimeMillis() >= timeoutExpiredMs) {
				return false;
			}
		}
	}

	public void debugServer() {
		doAction(DEBUG);
	}

	public void deleteServer() {
		doAction(DELETE);

		Dialog deleteDialog = new Dialog(bot, DELETE_SERVER);

		deleteDialog.confirm();
	}

	public void deployProject(String... projectItemNames) {
		doAction(ADD_AND_REMOVE);

		_addAndRemoveDialog.add(projectItemNames);
		_addAndRemoveDialog.confirm();
	}

	public void removeALL() {
		doAction(ADD_AND_REMOVE);

		_addAndRemoveDialog.getRemoveAllBtn().click();
		_addAndRemoveDialog.confirm();
	}

	public void removeProject(String... projectItemNames) {
		doAction(ADD_AND_REMOVE);
		_addAndRemoveDialog.remove(projectItemNames);
		_addAndRemoveDialog.confirm();
	}

	public void startServer() {
		doAction(START);
	}

	public void stopServer() {
		doAction(STOP);
		checkConsoleHasMsg(DESTROYING_PROTOCALHANDLER, 10000);
	}

	@Override
	protected SWTBotTreeItem getWidget() {
		Tree tree = new Tree(bot, _serverTreeIndex);

		String[] servers = tree.getAllItems();

		for (String server : servers) {
			if (server.contains(_serverName) || server.equals(_serverName)) {
				index = _serverTreeIndex;
				nodeText = new String[] {server};

				return super.getWidget();
			}
		}

		return null;
	}

	private AddAndRemoveDialog _addAndRemoveDialog;
	private String _serverName;
	private int _serverTreeIndex = 1;

}