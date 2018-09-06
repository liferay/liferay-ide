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

import com.liferay.blade.api.MigrationConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.project.core.upgrade.MigrationProblems;
import com.liferay.ide.project.ui.upgrade.animated.FindBreakingChangesPage;
import com.liferay.ide.project.ui.upgrade.animated.Page;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Lovett Li
 */
public class RemoveAction extends SelectionProviderAction implements IAction {

	public RemoveAction(ISelectionProvider provider) {
		super(provider, "Remove");
	}

	@Override
	public void run() {
		final FindBreakingChangesPage page = UpgradeView.getPage(
			Page.findbreackingchangesPageId, FindBreakingChangesPage.class);

		final TreeViewer treeViewer = page.getTreeViewer();

		Object selection = treeViewer.getStructuredSelection().getFirstElement();

		if (selection instanceof MigrationProblems) {
			MigrationProblems migrationProblem = (MigrationProblems)selection;

			MigrationUtil.removeMigrationProblems(migrationProblem);

			IResource project = MigrationUtil.getResourceFromMigrationProblems(migrationProblem);

			if (project != null) {
				MarkerUtil.clearMarkers(project, MigrationConstants.MARKER_TYPE, null);
			}
		}

		treeViewer.setInput(CoreUtil.getWorkspaceRoot());
		treeViewer.expandToLevel(2);
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		Object element = selection.getFirstElement();

		if (element instanceof MigrationProblems) {
			setEnabled(true);

			return;
		}

		setEnabled(false);
	}

}