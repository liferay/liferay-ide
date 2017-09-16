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

package com.liferay.ide.swtbot.liferay.ui.action;

import com.liferay.ide.swtbot.liferay.ui.UIAction;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.PackageExplorerView;
import com.liferay.ide.swtbot.ui.eclipse.page.ProjectExplorerView;
import com.liferay.ide.swtbot.ui.eclipse.page.ServersView;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.TreeItem;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;

/**
 * @author Terry Jia
 */
public class ViewAction extends UIAction {

	public ViewAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	public void deleteProject(String name) {
		TreeItem item = getProjects().getTreeItem(name);

		item.doAction(DELETE);

		_deleteResourcesDialog.getDeleteFromDisk().select();

		_deleteResourcesDialog.confirm();

		try {
			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 1500;

			_continueDeleteResourcesDialog.confirm();

			SWTBotPreferences.TIMEOUT = origin;
		}
		catch (Exception e) {
		}
	}

	public void deleteProject(String... nodes) {
		TreeItem nodesItem = getProjects().expandNode(nodes);

		nodesItem.doAction(DELETE);

		_deleteResourcesDialog.getDeleteFromDisk().select();

		_deleteResourcesDialog.confirm();

		try {
			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 1500;

			_continueDeleteResourcesDialog.confirm();

			SWTBotPreferences.TIMEOUT = origin;
		}
		catch (Exception e) {
		}
	}

	public void deleteProjects() {
		Tree projects = getProjects();

		String[] names = projects.getAllItems();

		for (String name : names) {
			deleteProject(name);
		}
	}

	public void deleteProjects(String[] names) {
		for (String name : names) {
			deleteProject(name);
		}
	}

	public void deleteProjectsExcludeNames(String... names) {
		String[] projectNames = getProjects().getAllItems();

		for (String projectName : projectNames) {
			boolean include = false;

			for (String name : names) {
				if (name.equals(projectName)) {
					include = true;

					break;
				}

				TreeItem projectItem = getProjects().getTreeItem(projectName);

				projectItem.collapse();
			}

			if (!include) {
				deleteProject(projectName);
			}
		}
	}

	public TreeItem fetchProjectFile(String... files) {
		return getProjects().expandNode(files);
	}

	public TreeItem getProject(String name) {
		return getProjects().getTreeItem(name);
	}

	public Tree getProjects() {
		try {
			return _projectExplorerView.getProjects();
		}
		catch (Exception e) {
			return _packageExplorerView.getProjects();
		}
	}

	public void openAddAndRemoveDialog(String serverLabel) {
		TreeItem item = _serversView.getServers().getTreeItem(serverLabel);

		item.contextMenu(ADD_AND_REMOVE);
	}

	public void openLiferayPortalHome(String serverLabel) {
		TreeItem item = _serversView.getServers().getTreeItem(serverLabel);

		item.contextMenu(OPEN_LIFERAY_PORTAL_HOME);
	}

	public void openServerEditor(String serverLabel) {
		TreeItem item = _serversView.getServers().getTreeItem(serverLabel);

		item.doubleClick();
	}

	public void serverDebug(String serverLabel) {
		TreeItem item = _serversView.getServers().getTreeItem(serverLabel);

		item.select();

		_serversView.clickDebugBtn();
	}

	public void serverStart(String serverLabel) {
		TreeItem item = _serversView.getServers().getTreeItem(serverLabel);

		item.select();

		_serversView.clickStartBtn();
	}

	public void serverStop(String serverLabel) {
		TreeItem item = _serversView.getServers().getTreeItem(serverLabel);

		item.select();

		_serversView.clickStopBtn();
	}

	public void showServersView() {
		ide.showServersView();
	}

	private DeleteResourcesContinueDialog _continueDeleteResourcesDialog = new DeleteResourcesContinueDialog(bot);
	private DeleteResourcesDialog _deleteResourcesDialog = new DeleteResourcesDialog(bot);
	private PackageExplorerView _packageExplorerView = new PackageExplorerView(bot);
	private ProjectExplorerView _projectExplorerView = new ProjectExplorerView(bot);
	private ServersView _serversView = new ServersView(bot);

}