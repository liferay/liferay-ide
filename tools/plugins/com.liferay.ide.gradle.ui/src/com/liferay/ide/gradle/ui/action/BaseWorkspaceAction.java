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
import com.liferay.ide.gradle.core.WatchingProjects;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Terry Jia
 */
public class BaseWorkspaceAction extends SelectionProviderAction {

	public void run() {
		Iterator<?> iterator = getStructuredSelection().iterator();

		WatchingProjects watchingProjects = WatchingProjects.getInstance();

		boolean changed = false;

		while (iterator.hasNext()) {
			Object obj = iterator.next();

			if (obj instanceof IProject) {
				IProject project = (IProject)obj;

				if ("watch".equals(_action)) {
					watchingProjects.addProject(project);
				}
				else if ("stop".equals(_action)) {
					watchingProjects.removeProject(project);
				}

				changed = true;
			}
		}

		if (changed) {
			IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			IWorkspaceProject project = LiferayCore.create(IWorkspaceProject.class, workspaceProject);

			project.watch(watchingProjects.getProjects());

			IDecoratorManager decoratorManager = UIUtil.getDecoratorManager();

			UIUtil.async(() -> decoratorManager.update("com.liferay.ide.gradle.ui.workspaceLabelProvider"));
		}
	}

	protected BaseWorkspaceAction(ISelectionProvider provider, String text, String action) {
		super(provider, text);

		_action = action;
	}

	private String _action;

}