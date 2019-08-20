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

package com.liferay.ide.functional.liferay.page.dialog;

import com.liferay.ide.functional.swtbot.page.Dialog;
import com.liferay.ide.functional.swtbot.page.Table;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;

/**
 * @author Lily Li
 */
public class ProjectSelectionDialog extends Dialog {

	public ProjectSelectionDialog(SWTBot bot) {
		super(bot);

		_projectSelection = new Table(bot);
	}

	public SWTBotTableItem getProject(String projectName) {
		return _projectSelection.getTableItem(projectName);
	}

	public void selectProject(String projectName) {
		SWTBotTableItem selectedProject = getProject(projectName);

		selectedProject.toggleCheck();
	}

	private Table _projectSelection;

}