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

package com.liferay.ide.project.ui.workspace;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.ImportLiferayWorkspaceOp;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.modules.BaseProjectWizard;
import com.liferay.ide.ui.util.ProjectExplorerLayoutUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.IWorkbench;

/**
 * @author Andy Wu
 */
public class ImportLiferayWorkspaceWizard extends BaseProjectWizard<ImportLiferayWorkspaceOp> {

	public ImportLiferayWorkspaceWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(ImportLiferayWorkspaceWizard.class).wizard());
	}

	@Override
	public IWizardPage[] getPages() {
		final IWizardPage[] wizardPages = super.getPages();

		if (!_firstErrorMessageRemoved && (wizardPages != null)) {
			SapphireWizardPage wizardPage = (SapphireWizardPage)wizardPages[0];

			try {
				if (LiferayWorkspaceUtil.hasWorkspace()) {
					wizardPage.setMessage(LiferayWorkspaceUtil.hasLiferayWorkspaceMsg, SapphireWizardPage.ERROR);
				}
				else {
					wizardPage.setMessage("Please select the workspace location.", SapphireWizardPage.NONE);
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

		ImportLiferayWorkspaceOp op = element().nearest(ImportLiferayWorkspaceOp.class);

		Path projectPath = get(op.getWorkspaceLocation());

		IProject newProject = CoreUtil.getProject(projectPath.lastSegment());

		try {
			addToWorkingSets(newProject);
		}
		catch (Exception ex) {
			ProjectUI.logError("Unable to add project to working set", ex);
		}

		openLiferayPerspective(newProject);

		ProjectExplorerLayoutUtil.setNested(true);
	}

	private static ImportLiferayWorkspaceOp _createDefaultOp() {
		return ImportLiferayWorkspaceOp.TYPE.instantiate();
	}

	private boolean _firstErrorMessageRemoved = false;

}