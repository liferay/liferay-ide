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

package com.liferay.ide.project.ui.modules.fragment;

import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.IWorkbench;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;
import com.liferay.ide.project.ui.BaseProjectWizard;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.RequireLiferayWorkspaceProject;

/**
 * @author Terry Jia
 * @author Seiphon Wang
 */
public class NewModuleFragmentWizard
	extends BaseProjectWizard<NewModuleFragmentOp> implements RequireLiferayWorkspaceProject {

	public NewModuleFragmentWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(NewModuleFragmentWizard.class).wizard());

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		promptIfLiferayWorkspaceNotExists("Liferay Module Fragment Project");
	}

	private static final String _INITIAL_MESSAGE = "Please select at least one project to import.";
	private boolean _supressedFirstErrorMessage = false;
	
	
	@Override
	public IWizardPage[] getPages() {
		final IWizardPage[] wizardPages = super.getPages();

		if (wizardPages != null) {
			final SapphireWizardPage wizardPage = (SapphireWizardPage)wizardPages[0];

			Value<String> projectName = _op.getProjectName();
			
			if (CoreUtil.isNullOrEmpty(wizardPage.getMessage()) && Objects.isNull(projectName)) {
				wizardPage.setMessage(_INITIAL_MESSAGE);
			}
			else {
				wizardPage.setMessage("Docker Server can not be used for new Fragment Project Wizard", SapphireWizardPage.WARNING);
			}

			if ((wizardPage.getMessageType() == IMessageProvider.ERROR) && !_supressedFirstErrorMessage) {
				_supressedFirstErrorMessage = true;

				wizardPage.setMessage(_INITIAL_MESSAGE);
			}
		}

		return wizardPages;
	}
	
	@Override
	protected void performPostFinish() {
		super.performPostFinish();

		final NewModuleFragmentOp op = element().nearest(NewModuleFragmentOp.class);

		final IProject project = CoreUtil.getProject(get(op.getProjectName()));

		try {
			addToWorkingSets(project);
		}
		catch (Exception ex) {
			ProjectUI.logError("Unable to add project to working set", ex);
		}

		openLiferayPerspective(project);
	}

	private static NewModuleFragmentOp _createDefaultOp() {
		_op =  NewModuleFragmentOp.TYPE.instantiate();
		
		return _op;
	}
	
	private static NewModuleFragmentOp _op;

}