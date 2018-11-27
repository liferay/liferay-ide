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

package com.liferay.ide.ui.liferay.support.workspace;

import com.liferay.ide.ui.liferay.support.SupportBase;

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
		return _getFiles(getModulesDirName(), files);
	}

	public abstract String getModulesDirName();

	public String getName() {
		return "workspace" + timestamp;
	}

	public String getServerName() {
		return "Liferay Community Edition Portal 7.1.0 CE GA1";
	}

	public String getStartedLabel() {
		return "Liferay Community Edition Portal 7.1.0 CE GA1  [Started]";
	}

	public String getStoppedLabel() {
		return "Liferay Community Edition Portal 7.1.0 CE GA1  [Stopped]";
	}

	public abstract String getThemesDirName();

	public String[] getWarFiles(String... files) {
		return _getFiles(getWarsDirName(), files);
	}

	public abstract String getWarsDirName();

	public abstract void initBundle();

	private String[] _getFiles(String dirName, String... files) {
		String[] fileNames = new String[files.length + 2];

		fileNames[0] = getName();
		fileNames[1] = dirName;

		for (int i = 0; i < files.length; i++) {
			fileNames[i + 2] = files[i];
		}

		return fileNames;
	}

}