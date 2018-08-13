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

package com.liferay.ide.ui.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class LaunchWorkspaceMenu extends ExtensionContributionFactory {

	@Override
	public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {
		MenuManager menu = new MenuManager("Launch Workspace");

		IContributionItem[] workspaceHistoryItems = _buildWorkspaceHistory(serviceLocator);

		for (IContributionItem workspaceHistoryItem : workspaceHistoryItems) {
			if (workspaceHistoryItem instanceof CommandContributionItem) {
				CommandContributionItem commandItem = (CommandContributionItem)workspaceHistoryItem;

				CommandContributionItemParameter parameter = commandItem.getData();

				if ("Other...".equals(parameter.label)) {
					menu.add(new Separator());
				}
			}

			menu.add(workspaceHistoryItem);
		}

		additions.addContributionItem(menu, null);
	}

	private IContributionItem[] _buildWorkspaceHistory(IServiceLocator serviceLocator) {
		List<IContributionItem> retval = new ArrayList<>();

		Location location = Platform.getInstanceLocation();

		ChooseWorkspaceData chooseWorkspaceData = new ChooseWorkspaceData(location.getURL());

		if (chooseWorkspaceData.readPersistedData()) {
			String currentWorkspace = chooseWorkspaceData.getInitialDefault();
			String[] recentWorkspaces = chooseWorkspaceData.getRecentWorkspaces();

			for (String recentWorkspace : recentWorkspaces) {
				if ((recentWorkspace != null) && !recentWorkspace.equals(currentWorkspace)) {
					retval.add(_newLaunchWorkspaceCommand(serviceLocator, recentWorkspace, recentWorkspace));
				}
			}

			retval.add(_newLaunchWorkspaceCommand(serviceLocator, "New Workspace...", null));
		}

		return retval.toArray(new IContributionItem[0]);
	}

	@SuppressWarnings("unchecked")
	private IContributionItem _newLaunchWorkspaceCommand(
		IServiceLocator serviceLocator, String label, String workspaceLocation) {

		CommandContributionItemParameter parameter = new CommandContributionItemParameter(
			serviceLocator, "", LaunchWorkspaceHandler.COMMAND_ID, CommandContributionItem.STYLE_PUSH);

		if (workspaceLocation != null) {
			parameter.parameters = new HashMap<>();
			parameter.parameters.put(LaunchWorkspaceHandler.PARAM_WORKSPACE_LOCATION, workspaceLocation);
		}

		parameter.label = label;

		CommandContributionItem launchWorkspaceCommand = new CommandContributionItem(parameter);

		launchWorkspaceCommand.setVisible(true);

		return launchWorkspaceCommand;
	}

}