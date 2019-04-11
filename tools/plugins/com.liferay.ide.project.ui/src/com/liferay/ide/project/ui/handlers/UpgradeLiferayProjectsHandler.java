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

package com.liferay.ide.project.ui.handlers;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.wizard.UpgradeLiferayProjectsWizard;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Simon Jiang
 */
public class UpgradeLiferayProjectsHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		final ArrayList<IProject> projectList = new ArrayList<>();

		if (selection instanceof IStructuredSelection) {
			Iterator<?> it = ((IStructuredSelection)selection).iterator();

			while (it.hasNext()) {
				Object o = it.next();

				if (o instanceof IJavaProject) {
					final IProject project = ((IJavaProject)o).getProject();

					if (!projectList.contains(project) && SDKUtil.isSDKProject(project)) {
						projectList.add(project);
					}
				}
			}
		}

		final UpgradeLiferayProjectsWizard wizard = new UpgradeLiferayProjectsWizard(
			projectList.toArray(new IProject[projectList.size()]));

		new WizardDialog(
			HandlerUtil.getActiveShellChecked(event), wizard
		).open();

		return null;
	}

	@Override
	public boolean isEnabled() {
		if (ListUtil.isNotEmpty(ProjectUtil.getAllPluginsSDKProjects())) {
			return true;
		}

		return false;
	}

}