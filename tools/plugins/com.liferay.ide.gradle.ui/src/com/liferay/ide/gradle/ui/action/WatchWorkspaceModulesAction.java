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

package com.liferay.ide.gradle.ui.action;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.wst.server.core.IServer;

import org.gradle.tooling.model.GradleProject;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class WatchWorkspaceModulesAction extends SelectionProviderAction {

	public WatchWorkspaceModulesAction(ISelectionProvider provider) {
		this(provider, "Start watching project", "watch");
	}

	public WatchWorkspaceModulesAction(ISelectionProvider provider, String text, String action) {
		super(provider, text);

		_action = action;
	}

	@Override
	public void run() {
		ISelectionProvider selectionProvider = getSelectionProvider();

		ISelection selection = selectionProvider.getSelection();

		if (selection instanceof TreeSelection) {
			TreeSelection tSelection = (TreeSelection)selection;

			TreePath treePath = tSelection.getPaths()[0];

			IServer server = (IServer)treePath.getFirstSegment();

			if (_action.equals("watch") && (server.getServerState() == IServer.STATE_STOPPED)) {
				MessageDialog dialog = new MessageDialog(
					UIUtil.getActiveShell(), "Watch Task.", null,
					"In order to watch this project, the server must be started. Do you want to start the server?",
					MessageDialog.QUESTION_WITH_CANCEL, new String[] {"debug", "start", "No"}, 0);

				int result = dialog.open();

				try {
					if (result == 0) {
						server.start("debug", new NullProgressMonitor());

						return;
					}
					else if (result == 1) {
						server.start("run", new NullProgressMonitor());

						return;
					}
				}
				catch (CoreException ce) {
					LiferayGradleUI.logError(ce);
				}
			}

			try {
				_gogoBundleDeployer = ServerUtil.createBundleDeployer(server);
			}
			catch (Exception exception) {
				LiferayGradleUI.logError(exception);

				return;
			}
		}

		IProject project = LiferayWorkspaceUtil.getWorkspaceProject();

		IWorkspaceProject workspaceProject = LiferayCore.create(IWorkspaceProject.class, project);

		Set<IProject> watching = workspaceProject.watching();

		Set<IProject> projectsToWatch = new HashSet<>(watching);

		List<?> selections = getStructuredSelection().toList();

		Stream<?> selectionsStream = selections.stream();

		Set<IProject> selectionProjects = selectionsStream.map(
			selectedItem -> Adapters.adapt(selectedItem, IProject.class)
		).filter(
			Objects::nonNull
		).collect(
			Collectors.toSet()
		);

		if (_action.equals("stop")) {
			if (selectionProjects.contains(LiferayWorkspaceUtil.getWorkspaceProject())) {
				IWorkspaceProject liferayGradleWorkspaceProject = LiferayWorkspaceUtil.getGradleWorkspaceProject();

				new StopWatchBundleJob(
					liferayGradleWorkspaceProject.getChildProjects()
				).schedule();

				projectsToWatch.clear();
			}
			else {
				new StopWatchBundleJob(
					selectionProjects
				).schedule();

				projectsToWatch.removeAll(selectionProjects);
			}
		}
		else if (_action.equals("watch")) {
			projectsToWatch.addAll(selectionProjects);
		}

		workspaceProject.watch(projectsToWatch);

		IDecoratorManager decoratorManager = UIUtil.getDecoratorManager();

		UIUtil.async(() -> decoratorManager.update(LiferayGradleUI.LIFERAY_WATCH_DECORATOR_ID));
	}

	private void _stopBundleProject(GogoBundleDeployer gogoBundleDeployer, IBundleProject bundleProject) {
		try {
			if (!StringUtil.equals(bundleProject.getBundleShape(), "war")) {
				gogoBundleDeployer.uninstall(bundleProject);
			}

			GradleProject gradleProject = GradleUtil.getGradleProject(bundleProject.getProject());

			if (gradleProject != null) {
				File installedBundleIdFile = new File(gradleProject.getBuildDirectory(), "installedBundleId");

				FileUtil.delete(installedBundleIdFile);
			}
		}
		catch (Exception e) {
		}

		UIUtil.async(
			() -> {
				IDecoratorManager decoratorManager = UIUtil.getDecoratorManager();

				decoratorManager.update(LiferayGradleUI.LIFERAY_WATCH_DECORATOR_ID);
			});
	}

	private String _action;
	private GogoBundleDeployer _gogoBundleDeployer;

	private class StopWatchBundleJob extends Job {

		public StopWatchBundleJob(Set<IProject> stopWatchBundleProjects) {
			super("Stop watch bundle project ");

			_stopWatchBundleProjects = stopWatchBundleProjects;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			_stopWatchBundleProjects.parallelStream(
			).map(
				bundleProject -> LiferayCore.create(IBundleProject.class, bundleProject)
			).filter(
				Objects::nonNull
			).forEach(
				bundleProject -> _stopBundleProject(_gogoBundleDeployer, bundleProject)
			);

			return Status.OK_STATUS;
		}

		private Set<IProject> _stopWatchBundleProjects;

	}

}