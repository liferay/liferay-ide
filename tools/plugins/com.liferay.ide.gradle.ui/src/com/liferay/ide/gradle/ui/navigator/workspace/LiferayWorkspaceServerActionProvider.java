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
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.gradle.ui.action.StopWorkspaceModulesAction;
import com.liferay.ide.gradle.ui.action.WatchWorkspaceModulesAction;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
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

		ICommonViewerSite viewerSite = actionSite.getViewSite();

		if (viewerSite instanceof ICommonViewerWorkbenchSite) {
			StructuredViewer structuredViewer = actionSite.getStructuredViewer();

			if (structuredViewer instanceof CommonViewer) {
				ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite)viewerSite;

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

		_watchAction = new WatchWorkspaceModulesAction(provider);
		_stopAction = new StopWorkspaceModulesAction(provider);

		if (selection instanceof TreeSelection) {
			TreeSelection watchSelection = (TreeSelection)selection;

			List<?> selectProjects = watchSelection.toList();

			if (ListUtil.hasMultipleValues(selectProjects)) {
				_watchAction.setEnabled(true);
				_stopAction.setEnabled(true);

				menu.add(_watchAction);
				menu.add(_stopAction);

				return;
			}

			Object selectedProject = watchSelection.getFirstElement();

			IWorkspaceProject workspaceProject = LiferayCore.create(IWorkspaceProject.class, selectedProject);

			if (workspaceProject != null) {
				IProject project = workspaceProject.getProject();

				Set<IProject> watchingProject = workspaceProject.watching();

				String jobName = project.getName() + ":watch";

				IJobManager jobManager = Job.getJobManager();

				Job[] jobs = jobManager.find(jobName);

				if (ListUtil.isNotEmpty(jobs) && ListUtil.contains(watchingProject, project)) {
					_watchAction.setEnabled(false);
					_stopAction.setEnabled(true);
				}
				else {
					_watchAction.setEnabled(true);
					_stopAction.setEnabled(false);
				}

				menu.add(_watchAction);
				menu.add(_stopAction);
			}
			else {
				TreePath[] selectionPaths = watchSelection.getPathsFor(selectedProject);

				Object parentObject = Stream.of(
					selectionPaths
				).filter(
					selectionPath -> selectedProject.equals(selectionPath.getLastSegment())
				).map(
					selectPath -> {
						TreePath parentPath = selectPath.getParentPath();

						return parentPath.getLastSegment();
					}
				).findFirst(
				).get();

				IWorkspaceProject parentWorkspaceProject = LiferayCore.create(IWorkspaceProject.class, parentObject);

				Set<IProject> watchingProjects = parentWorkspaceProject.watching();

				if (ListUtil.notContains(watchingProjects, parentWorkspaceProject.getProject())) {
					if (ListUtil.isEmpty(watchingProjects)) {
						_watchAction.setEnabled(true);
						_stopAction.setEnabled(false);
					}
					else {
						if (ListUtil.notContains(watchingProjects, selectedProject)) {
							_watchAction.setEnabled(true);
							_stopAction.setEnabled(false);
						}
						else {
							_watchAction.setEnabled(false);
							_stopAction.setEnabled(true);
						}
					}

					menu.add(_watchAction);
					menu.add(_stopAction);
				}
				else {
					if (ListUtil.contains(watchingProjects, selectedProject)) {
						_watchAction.setEnabled(false);
						_stopAction.setEnabled(true);

						menu.add(_watchAction);
						menu.add(_stopAction);
					}
				}
			}
		}
	}

	private StopWorkspaceModulesAction _stopAction;
	private WatchWorkspaceModulesAction _watchAction;

}