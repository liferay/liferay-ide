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

package com.liferay.ide.gradle.ui.navigator.workspace;

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.gradle.ui.action.RefreshWorkspaceModulesAction;
import com.liferay.ide.gradle.ui.action.RemoveWorkspaceModulesAction;
import com.liferay.ide.gradle.ui.action.StopWorkspaceModulesAction;
import com.liferay.ide.gradle.ui.action.WatchWorkspaceModulesAction;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class LiferayWorkspaceServerActionProvider extends CommonActionProvider {

	public static final String NEW_MENU_ID = "org.eclipse.wst.server.ui.internal.cnf.newMenuId";

	public static final String TOP_SECTION_END_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionEnd";

	public static final String TOP_SECTION_START_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionStart";

	public LiferayWorkspaceServerActionProvider() {
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		menu.removeAll();

		menu.add(_invisibleSeparator(TOP_SECTION_START_SEPARATOR));
		ICommonActionExtensionSite actionSite = getActionSite();

		ICommonViewerSite viewSite = actionSite.getViewSite();

		if (viewSite instanceof ICommonViewerWorkbenchSite) {
			StructuredViewer structuredViewer = actionSite.getStructuredViewer();

			if (structuredViewer instanceof CommonViewer) {
				ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite)viewSite;

				_makeActions(menu, wsSite.getSelectionProvider());
			}
		}

		menu.add(_invisibleSeparator(TOP_SECTION_END_SEPARATOR));
		menu.add(new Separator());
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end"));
	}

	private Separator _invisibleSeparator(String s) {
		Separator separator = new Separator(s);

		separator.setVisible(false);

		return separator;
	}

	private void _makeActions(IMenuManager menu, ISelectionProvider provider) {
		ISelection selection = provider.getSelection();

		if (selection == null) {
			return;
		}

		StructuredSelection treeSelection = (StructuredSelection)selection;

		boolean watchEnabled = false;
		boolean stopEnabled = false;

		if (ListUtil.isMultiple(treeSelection.toList())) {
			watchEnabled = true;
			stopEnabled = true;
		}
		else {
			IProject selectedProject = (IProject)treeSelection.getFirstElement();

			if (LiferayWorkspaceUtil.isValidGradleWorkspaceProject(selectedProject)) {
				IJobManager jobManager = Job.getJobManager();

				Job[] jobs = jobManager.find(
					selectedProject.getName() + ":" + LiferayGradleCore.LIFERAY_WATCH + ":" +
						LiferayGradleCore.LIFERAY_WORKSPACE_WATCH_JOB_FAMILY);

				if (ListUtil.isNotEmpty(jobs)) {
					stopEnabled = true;
				}
				else {
					watchEnabled = true;
				}
			}
			else {
				IWorkspaceProject workspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

				Set<IProject> watchingProjects = workspaceProject.watching();

				if (!watchingProjects.contains(workspaceProject.getProject())) {
					if (ListUtil.isEmpty(watchingProjects)) {
						watchEnabled = true;
					}

					if (ListUtil.contains(watchingProjects, selectedProject)) {
						stopEnabled = true;
					}
					else {
						watchEnabled = true;
					}
				}
			}
		}

		_watchAction = new WatchWorkspaceModulesAction(provider);

		_watchAction.setEnabled(watchEnabled);

		_stopAction = new StopWorkspaceModulesAction(provider);

		_stopAction.setEnabled(stopEnabled);

		_refreshAction = new RefreshWorkspaceModulesAction(provider);
		_removeAction = new RemoveWorkspaceModulesAction(provider);

		menu.add(_watchAction);
		menu.add(_stopAction);
		menu.add(_refreshAction);
		menu.add(_removeAction);
	}

	private RefreshWorkspaceModulesAction _refreshAction;
	private RemoveWorkspaceModulesAction _removeAction;
	private StopWorkspaceModulesAction _stopAction;
	private WatchWorkspaceModulesAction _watchAction;

}