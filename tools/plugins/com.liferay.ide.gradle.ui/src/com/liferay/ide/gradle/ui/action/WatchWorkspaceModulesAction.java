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
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.LiferayGradleWorkspaceProject;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
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

		if ("watch".equals(_action) && (selection instanceof TreeSelection)) {
			TreePath treePath = ((TreeSelection)selection).getPaths()[0];

			IServer server = (IServer)treePath.getFirstSegment();

			if (server.getServerState() == IServer.STATE_STOPPED) {
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
		}

		Iterator<?> iterator = getStructuredSelection().iterator();

		IProject project = LiferayWorkspaceUtil.getWorkspaceProject();

		IWorkspaceProject workspaceProject = LiferayCore.create(IWorkspaceProject.class, project);

		Set<IProject> watching = workspaceProject.watching();

		Set<IProject> projectsToWatch = new HashSet<>(watching);

		while (iterator.hasNext()) {
			Object obj = iterator.next();

			if (obj instanceof IProject) {
				IProject selectedProject = (IProject)obj;

				if ("watch".equals(_action)) {
					projectsToWatch.add(selectedProject);
				}
				else if ("stop".equals(_action)) {
					if (selectedProject.equals(LiferayWorkspaceUtil.getWorkspaceProject())) {
						LiferayGradleWorkspaceProject liferayGradleWorkspaceProject = new LiferayGradleWorkspaceProject(
							selectedProject);

						Set<IProject> childProjects = liferayGradleWorkspaceProject.getChildProjects();

						for (IProject childProject : childProjects) {
							_undeployModuleFromServer(childProject);
						}

						projectsToWatch.clear();

						break;
					}
					else {
						projectsToWatch.remove(selectedProject);
						_undeployModuleFromServer(selectedProject);
					}
				}
			}
		}

		workspaceProject.watch(projectsToWatch);

		IDecoratorManager decoratorManager = UIUtil.getDecoratorManager();

		UIUtil.async(() -> decoratorManager.update(LiferayGradleUI.LIFERAY_WATCH_DECORATOR_ID));
	}

	private void _undeployModuleFromServer(IProject selectedProject) {
		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, selectedProject);

		if (bundleProject == null) {
			return;
		}

		try {
			GogoBundleDeployer gogoBundleDeployer = new GogoBundleDeployer();

			gogoBundleDeployer.uninstall(bundleProject);

			GradleProject gradleProject = GradleUtil.getGradleProjectModel(selectedProject);

			if (gradleProject != null) {
				File installedBundleIdFile = new File(gradleProject.getBuildDirectory(), "installedBundleId");

				FileUtil.delete(installedBundleIdFile);
			}
		}
		catch (Exception e) {
			LiferayGradleUI.logError(e);
		}
	}

	private String _action;

}