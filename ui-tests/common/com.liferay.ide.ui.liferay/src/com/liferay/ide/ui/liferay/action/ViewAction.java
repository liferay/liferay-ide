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
import com.liferay.ide.ui.liferay.page.view.CodeUpgradeView;
import com.liferay.ide.ui.swtbot.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.ui.swtbot.eclipse.page.ErrorLogView;
import com.liferay.ide.ui.swtbot.eclipse.page.PackageExplorerView;
import com.liferay.ide.ui.swtbot.eclipse.page.ProjectExplorerView;
import com.liferay.ide.ui.swtbot.eclipse.page.ServersView;
import com.liferay.ide.ui.swtbot.page.Tree;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Terry Jia
 */
public class ViewAction extends UIAction {

	public ViewAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	public void checkErrorLog() {
		System.out.println(_errorLogView.getLogs().size());
	}

	public void closeAndDeleteProject(String... items) {
		closeProject(items);

		deleteProject(items);
	}

	public void closeAndDeleteProjectWithNoRunningJobs(String... items) {
		closeProject(items);

		_jobAction.waitForNoRunningJobs();

		deleteProject(items);
	}

	public void closeProject(String... items) {
		ide.sleep();

		_getProjects().contextMenu("Close Project", items);

		_jobAction.waitForCloseProject();
	}

	public void deleteProject(String... items) {
		_getProjects().contextMenu(DELETE, items);

		ide.sleep();

		String label = _deleteResourcesDialog.getLabel();

		// _deleteResourcesDialog.getDeleteFromDisk().select();

		_deleteResourcesDialog.confirm();

		_jobAction.waitForShellClosed(label);

		// long origin = SWTBotPreferences.TIMEOUT;

		//

		// SWTBotPreferences.TIMEOUT = 1500;

		//

		// try {
		// _continueDeleteResourcesDialog.confirm();
		// }
		// catch (Exception e) {
		// }

		//

		// try {
		// _dialog.confirm();
		// }
		// catch (Exception e) {
		// }

		//

		// SWTBotPreferences.TIMEOUT = origin;

	}

	public String[] getProjectNames() {
		return _getProjects().getItemLabels();
	}

	public void openAddAndRemoveDialog(String serverLabel) {
		_serversView.getServers().contextMenu(ADD_AND_REMOVE, serverLabel);
	}

	public void openFragmentFilesWizard() {
		_getProjects().contextMenu(LIFERAY_MODULE_FRAGMENT_FILES);
	}

	public void openLiferayPortalHome(String serverLabel) {
		_serversView.getServers().contextMenu(OPEN_LIFERAY_PORTAL_HOME, serverLabel);

		_jobAction.waitForBrowserLoaded();
	}

	public void openProjectFile(String... files) {
		ide.sleep();

		_getProjects().setFocus();

		_getProjects().doubleClick(files);
	}

	public void openServerEditor(String serverLabel) {
		_serversView.getServers().doubleClick(serverLabel);
	}

	public void restartUpgrade() {
		_codeUpgradeView.getRestartUpgradeBtn().click();
	}

	public void runBuildServices(String... projectNames) {
		_getProjects().contextMenu(BUILD_SERVICES, projectNames);
	}

	public void runBuildWSDD(String... projectNames) {
		_getProjects().contextMenu(BUILD_WSDD, projectNames);
	}

	public void serverDebug(String serverLabel) {
		ide.sleep(5000);

		_serversView.getServers().select(serverLabel);

		_serversView.clickDebugBtn();
	}

	public void serverStart(String serverLabel) {
		ide.sleep(5000);

		_serversView.getServers().select(serverLabel);

		_serversView.clickStartBtn();
	}

	public void serverStop(String serverLabel) {
		ide.sleep(2000);

		_serversView.getServers().select(serverLabel);

		_serversView.clickStopBtn();
	}

	public void showAllPages() {
		_codeUpgradeView.getShowAllPagesBtn().click();
	}

	public void showCodeUpgradeView() {
		ide.showCodeUpgradeView();
	}

	public void showServersView() {
		ide.showServersView();
	}

	public void switchGear(int index) {
		_codeUpgradeView.getGear().clickGear(index);
	}

	public void switchLiferayPerspective() {
		ide.getLiferayPerspective().activate();
	}

	public boolean visibleProjectFileTry(String... files) {
		try {
			return _getProjects().isVisible(files);
		}
		catch (Exception e) {
			_getProjects().setFocus();

			try {
				String[] parents = Arrays.copyOfRange(files, 0, files.length - 1);

				_getProjects().expand(parents);

				_getProjects().contextMenu(REFRESH, parents);

				ide.sleep(2000);
			}
			catch (Exception e1) {
			}

			for (int i = files.length - 1; i > 0; i--) {
				String[] parents = Arrays.copyOfRange(files, 0, files.length - i);

				SWTBotTreeItem parent = _getProjects().getTreeItem(parents);

				_getProjects().expand(parents);

				String subnode = files[files.length - i];

				_jobAction.waitForSubnote(parent, subnode, REFRESH);
			}

			return _getProjects().isVisible(files);
		}
	}

	private Tree _getProjects() {
		String perspectiveLabel = ide.getPerspectiveLabel();

		if (perspectiveLabel.equals(LIFERAY_WORKSPACE)) {
			return _projectExplorerView.getProjects();
		}
		else if (perspectiveLabel.equals(LIFERAY_PLUGINS)) {
			return _packageExplorerView.getProjects();
		}

		return null;
	}

	private final CodeUpgradeView _codeUpgradeView = new CodeUpgradeView(bot);
	private final DeleteResourcesDialog _deleteResourcesDialog = new DeleteResourcesDialog(bot);
	private final ErrorLogView _errorLogView = new ErrorLogView(bot);
	private final JobAction _jobAction = new JobAction(bot);
	private final PackageExplorerView _packageExplorerView = new PackageExplorerView(bot);
	private final ProjectExplorerView _projectExplorerView = new ProjectExplorerView(bot);
	private final ServersView _serversView = new ServersView(bot);

}