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

package com.liferay.ide.upgrade.tasks.ui.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.modules.BaseProjectWizard;
import com.liferay.ide.ui.util.ProjectExplorerLayoutUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.IWorkbench;

/**
 * @author Andy Wu
 * @author Terry Jia
 */
public class NewLiferayWorkspaceWizard extends BaseProjectWizard<NewLiferayWorkspaceOp> {

	public NewLiferayWorkspaceWizard() {
		this(_createDefaultOp());
	}

	public NewLiferayWorkspaceWizard(NewLiferayWorkspaceOp newLiferayWorkspaceOp) {
		super(newLiferayWorkspaceOp, DefinitionLoader.sdef(NewLiferayWorkspaceWizard.class).wizard());
	}

	@Override
	public IWizardPage[] getPages() {
		IWizardPage[] wizardPages = super.getPages();

		if (!_firstErrorMessageRemoved && (wizardPages != null)) {
			SapphireWizardPage wizardPage = (SapphireWizardPage)wizardPages[0];

			try {
				if (LiferayWorkspaceUtil.hasWorkspace()) {
					wizardPage.setMessage(LiferayWorkspaceUtil.hasLiferayWorkspaceMsg, SapphireWizardPage.ERROR);
				}
				else {
					wizardPage.setMessage("Please enter the workspace project name.", SapphireWizardPage.NONE);
				}
			}
			catch (CoreException ce) {
				wizardPage.setMessage(LiferayWorkspaceUtil.multiWorkspaceErrorMsg, SapphireWizardPage.ERROR);
			}

			_firstErrorMessageRemoved = true;
		}

		return wizardPages;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	protected void performPostFinish() {
		super.performPostFinish();

		NewLiferayWorkspaceOp op = element().nearest(NewLiferayWorkspaceOp.class);

		IProject newProject = CoreUtil.getProject(get(op.getWorkspaceName()));

		try {
			addToWorkingSets(newProject);
		}
		catch (Exception ex) {
			ProjectUI.logError("Unable to add project to working set", ex);
		}

		ProjectExplorerLayoutUtil.setNested(true);
	}

	private static NewLiferayWorkspaceOp _createDefaultOp() {
		return NewLiferayWorkspaceOp.TYPE.instantiate();
	}

	private boolean _firstErrorMessageRemoved = false;

}