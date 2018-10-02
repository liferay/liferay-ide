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

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Terry Jia
 */
public class WatchWorkspaceModulesAction extends SelectionProviderAction {

	@Override
	public void run() {
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
					projectsToWatch.remove(selectedProject);
				}
			}
		}

		workspaceProject.watch(projectsToWatch);

		IDecoratorManager decoratorManager = UIUtil.getDecoratorManager();

		UIUtil.async(() -> decoratorManager.update("com.liferay.ide.gradle.ui.workspaceLabelProvider"));
	}

	public WatchWorkspaceModulesAction(ISelectionProvider provider) {
		this(provider, "Start watching project", "start");
	}

	public WatchWorkspaceModulesAction(ISelectionProvider provider, String text, String action) {
		super(provider, text);

		_action = action;
	}

	private String _action;

}