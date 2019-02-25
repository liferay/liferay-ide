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

package com.liferay.ide.project.ui.migration;

import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.animated.FindBreakingChangesPage;
import com.liferay.ide.project.ui.upgrade.animated.LiferayUpgradeDataModel;
import com.liferay.ide.project.ui.upgrade.animated.Page;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView;
import com.liferay.ide.ui.util.UIUtil;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Andy Wu
 * @author Lovett Li
 * @author Terry Jia
 * @author Simon Jiang
 */
public class RunMigrationToolAction extends OpenJavaProjectSelectionDialogAction {

	public RunMigrationToolAction(String text, Shell shell, ISelection selection, LiferayUpgradeDataModel dataModel) {
		super(text, shell, dataModel);

		_selection = selection;
	}

	public RunMigrationToolAction(String text, Shell shell, LiferayUpgradeDataModel dataModel) {
		super(text, shell, dataModel);
	}

	@Override
	public void run() {
		if ((_selection == null) || _selection.isEmpty()) {
			_selection = getSelectionProjects();
		}

		final FindBreakingChangesPage page = UpgradeView.getPage(
			Page.findbreackingchangesPageId, FindBreakingChangesPage.class);

		if (_selection != null) {
			try {
				Map<String, Object> breakingChangeParameters = new HashMap<>();

				breakingChangeParameters.put("CombineExistedProblem", getCombineExistedProjects());

				String upgradeVersion = get(dataModel.getUpgradeVersion());

				breakingChangeParameters.put("UpgradeVersion", upgradeVersion);

				UIUtil.executeCommand(
					"com.liferay.ide.project.ui.migrateProject", _selection, breakingChangeParameters);
			}
			catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
				page.setButtonState(true);
				ProjectUI.createErrorStatus("Error in migrate command", e);
			}
		}
		else {
			page.setButtonState(true);
		}
	}

	private ISelection _selection;

}