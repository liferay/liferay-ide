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

package com.liferay.ide.project.ui.springmvcportlet;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.springmvcportlet.NewSpringMVCPortletProjectOp;
import com.liferay.ide.project.ui.BaseProjectWizard;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.ValidLiferayWorkspaceChecker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.ui.IWorkbench;

/**
 * @author Simon Jiang
 * @author Seiphon Wang
 */
public class NewSpringMVCPortletProjectWizard extends BaseProjectWizard<NewSpringMVCPortletProjectOp> {

	public NewSpringMVCPortletProjectWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(NewSpringMVCPortletProjectWizard.class).wizard());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		ValidLiferayWorkspaceChecker checker = new ValidLiferayWorkspaceChecker("Liferay Module Ext Project");

		checker.checkValidLiferayWorkspace();
	}

	@Override
	protected void performPostFinish() {
		super.performPostFinish();

		final List<IProject> projects = new ArrayList<>();

		final NewSpringMVCPortletProjectOp op = element().nearest(NewSpringMVCPortletProjectOp.class);

		ElementList<ProjectName> projectNames = op.getProjectNames();

		for (ProjectName projectName : projectNames) {
			final IProject newProject = CoreUtil.getProject(get(projectName.getName()));

			if (newProject != null) {
				projects.add(newProject);
			}
		}

		for (final IProject project : projects) {
			try {
				addToWorkingSets(project);
			}
			catch (Exception ex) {
				ProjectUI.logError("Unable to add project to working set", ex);
			}
		}

		if (ListUtil.isNotEmpty(projects)) {
			IProject finalProject = projects.get(0);

			openLiferayPerspective(finalProject);
		}
	}

	private static NewSpringMVCPortletProjectOp _createDefaultOp() {
		return NewSpringMVCPortletProjectOp.TYPE.instantiate();
	}

}