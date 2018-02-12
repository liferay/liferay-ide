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

package com.liferay.ide.ui.liferay.base;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public abstract class LiferayWorkspaceSupport extends SupportBase {

	public LiferayWorkspaceSupport(SWTWorkbenchBot bot) {
		super(bot);
	}

	@Override
	public void after() {
		viewAction.project.closeAndDeleteFromDisk(getName());
	}

	public String[] getModuleFiles(String... files) {
		String[] projectNames = new String[files.length + 2];

		projectNames[0] = getName();
		projectNames[1] = getModulesDirName();

		for (int i = 0; i < files.length; i++) {
			projectNames[i + 2] = files[i];
		}

		return projectNames;
	}

	public abstract String getModulesDirName();

	public String getName() {
		return "workspace" + timestamp;
	}

	public abstract String getThemesDirName();

	public abstract String getWarsDirName();

}