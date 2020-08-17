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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.ParentSDKProjectImportOp;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Simon Jiang
 */
public class ParentSDKProjectImportWizard
	extends SapphireWizard<ParentSDKProjectImportOp> implements INewWizard, IWorkbenchWizard {

	public ParentSDKProjectImportWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(ParentSDKProjectImportWizard.class).wizard());
	}

	public ParentSDKProjectImportWizard(final String newTitle) {
		super(_createDefaultOp(), DefinitionLoader.sdef(ParentSDKProjectImportWizard.class).wizard());
	}

	@Override
	public IWizardPage[] getPages() {
		final IWizardPage[] wizardPages = super.getPages();

		if (wizardPages != null) {
			final SapphireWizardPage wizardPage = (SapphireWizardPage)wizardPages[0];

			if (CoreUtil.isNullOrEmpty(wizardPage.getMessage())) {
				wizardPage.setMessage(_INITIAL_MESSAGE);
			}

			if ((wizardPage.getMessageType() == IMessageProvider.ERROR) && !_supressedFirstErrorMessage) {
				_supressedFirstErrorMessage = true;

				wizardPage.setMessage(_INITIAL_MESSAGE);
			}
		}

		if (_title != null) {
			Shell shell = getContainer().getShell();

			shell.setText(_title);
		}

		return wizardPages;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	private static ParentSDKProjectImportOp _createDefaultOp() {
		return ParentSDKProjectImportOp.TYPE.instantiate();
	}

	private static final String _INITIAL_MESSAGE =
		"Please select Liferay Plugins SDK directory to import as a project.";

	private boolean _supressedFirstErrorMessage = false;
	private String _title;

}