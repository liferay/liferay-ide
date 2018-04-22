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

package com.liferay.ide.server.tomcat.ui;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatLaunchConfigurationTabGroup;
import org.eclipse.wst.server.ui.ServerLaunchConfigurationTab;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class LiferayTomcatLaunchConfigTabGroup extends TomcatLaunchConfigurationTabGroup {

	public LiferayTomcatLaunchConfigTabGroup() {
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[6];

		tabs[0] = new ServerLaunchConfigurationTab(new String[] {getServerTypeId()});
		tabs[0].setLaunchConfigurationDialog(dialog);

		tabs[1] = new JavaArgumentsTab();
		tabs[1].setLaunchConfigurationDialog(dialog);

		tabs[2] = new JavaClasspathTab();
		tabs[2].setLaunchConfigurationDialog(dialog);

		tabs[3] = new SourceLookupTab();
		tabs[3].setLaunchConfigurationDialog(dialog);

		tabs[4] = new EnvironmentTab();
		tabs[4].setLaunchConfigurationDialog(dialog);

		tabs[5] = new CommonTab();
		tabs[5].setLaunchConfigurationDialog(dialog);

		setTabs(tabs);
	}

	protected String getServerTypeId() {
		return "com.liferay.ide.eclipse.server.tomcat";
	}

}