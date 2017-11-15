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

package com.liferay.ide.ui.liferay.action;

import com.liferay.ide.ui.liferay.UIAction;
import com.liferay.ide.ui.swtbot.eclipse.page.ConsoleView;
import com.liferay.ide.ui.swtbot.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.ui.swtbot.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.ui.swtbot.eclipse.page.PackageExplorerView;
import com.liferay.ide.ui.swtbot.eclipse.page.ProjectExplorerView;
import com.liferay.ide.ui.swtbot.eclipse.page.ServersView;
import com.liferay.ide.ui.swtbot.page.Dialog;
import com.liferay.ide.ui.swtbot.page.Tree;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

/**
 * @author Terry Jia
 */
public class ViewAction extends UIAction {

	public ViewAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	public void deleteProject(String... items) {
		ide.sleep();

		_getProjects().contextMenu(DELETE, items);

		ide.sleep(500);

		_deleteResourcesDialog.getDeleteFromDisk().select();

		_deleteResourcesDialog.confirm();

		long origin = SWTBotPreferences.TIMEOUT;

		SWTBotPreferences.TIMEOUT = 1500;

		try {
			_continueDeleteResourcesDialog.confirm();
		}
		catch (Exception e) {
		}

		try {
			_dialog.confirm();
		}
		catch (Exception e) {
		}

		SWTBotPreferences.TIMEOUT = origin;
	}

	public String[] getProjectNames() {
		return _getProjects().getItemLabels();
	}

	public void openAddAndRemoveDialog(String serverLabel) {
		_serversView.getServers().contextMenu(ADD_AND_REMOVE, serverLabel);
	}

	public void openLiferayPortalHome(String serverLabel) {
		_serversView.getServers().contextMenu(OPEN_LIFERAY_PORTAL_HOME, serverLabel);

		ide.sleep(10000);
	}

	public void openProjectFile(String... files) {
		ide.sleep();

		_getProjects().setFocus();

		_getProjects().doubleClick(files);
	}

	public void openServerEditor(String serverLabel) {
		_serversView.getServers().doubleClick(serverLabel);
	}

	public void serverDebug(String serverLabel) {
		_serversView.getServers().select(serverLabel);

		_serversView.clickDebugBtn();
	}

	public void serverDeployWait(String projectName) {
		_serverWaitInConsole(10000, 1000, 1000, "STARTED " + projectName + "_");
	}

	public void serverStart(String serverLabel) {
		_serversView.getServers().select(serverLabel);

		_serversView.clickStartBtn();
	}

	public void serverStartWait() throws TimeoutException {
		_serverWaitInConsole(600000, 25000, 10000, "Server startup in");
	}

	public void serverStop(String serverLabel) {
		_serversView.getServers().select(serverLabel);

		_serversView.clickStopBtn();
	}

	public void serverStopWait() {
		_serverWaitInConsole(
			300000, 5000, 1000, "org.apache.coyote.AbstractProtocol.destroy Destroying ProtocolHandler [\"ajp-nio");
	}

	public void serverStopWait62() {
		_serverWaitInConsole(300000, 5000, 1000, "Destroying ProtocolHandler [\"ajp-bio-");
	}

	public void showServersView() {
		ide.showServersView();
	}

	public boolean visibleProjectFileTry(String... files) {
		try {
			return _getProjects().isVisible(files);
		}
		catch (Exception e) {
			_getProjects().setFocus();

			String[] parents = Arrays.copyOfRange(files, 0, files.length - 1);

			_getProjects().expand(parents);

			_getProjects().contextMenu(REFRESH, parents);

			ide.sleep(2000);

			return _getProjects().isVisible(files);
		}
	}

	private Tree _getProjects() {
		try {
			return _projectExplorerView.getProjects();
		}
		catch (Exception e) {
			return _packageExplorerView.getProjects();
		}
	}

	private boolean _hasConsoleLog(String content) {
		String consoleLog = _consoleView.getLog().getText();

		return consoleLog.contains(content);
	}

	private void _serverWaitInConsole(long timeout, long startTime, long endTime, String consoleLog)
		throws TimeoutException {

		ide.sleep(startTime);

		long limit = System.currentTimeMillis() + timeout;

		while (true) {
			if (_hasConsoleLog(consoleLog)) {
				ide.sleep(endTime);

				return;
			}

			ide.sleep(500);

			if (System.currentTimeMillis() > limit) {
				throw new TimeoutException("Timeout after: " + timeout + " ms.");
			}
		}
	}

	private final ConsoleView _consoleView = new ConsoleView(bot);
	private final DeleteResourcesContinueDialog _continueDeleteResourcesDialog = new DeleteResourcesContinueDialog(bot);
	private final DeleteResourcesDialog _deleteResourcesDialog = new DeleteResourcesDialog(bot);
	private final Dialog _dialog = new Dialog(bot);
	private final PackageExplorerView _packageExplorerView = new PackageExplorerView(bot);
	private final ProjectExplorerView _projectExplorerView = new ProjectExplorerView(bot);
	private final ServersView _serversView = new ServersView(bot);

}