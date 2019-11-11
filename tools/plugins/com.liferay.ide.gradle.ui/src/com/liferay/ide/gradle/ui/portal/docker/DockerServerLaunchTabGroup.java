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

package com.liferay.ide.gradle.ui.portal.docker;

import com.liferay.ide.server.core.portal.docker.PortalDockerServer;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.wst.server.ui.ServerLaunchConfigurationTab;

/**
 * @author Simon Jiang
 */
public class DockerServerLaunchTabGroup extends AbstractLaunchConfigurationTabGroup {

	public DockerServerLaunchTabGroup() {

		// TODO Auto-generated constructor stub

	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ServerLaunchConfigurationTab serverLaunchConfigurationTab = new ServerLaunchConfigurationTab(
			new String[] {PortalDockerServer.id});

		ILaunchConfigurationTab[] tabs = {
			serverLaunchConfigurationTab, new SourceLookupTab(), new EnvironmentTab(), new CommonTab()
		};

		for (ILaunchConfigurationTab tab : tabs) {
			tab.setLaunchConfigurationDialog(dialog);
		}

		setTabs(tabs);
	}

}