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
import com.liferay.ide.project.core.model.SDKProjectsImportOp;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.platform.PathBridge;
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
public class ImportSDKProjectsWizard
	extends SapphireWizard<SDKProjectsImportOp> implements IWorkbenchWizard, INewWizard {

	public ImportSDKProjectsWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(ImportSDKProjectsWizard.class).wizard());
	}

	public ImportSDKProjectsWizard(final IPath sdkPath) {
		super(_createDefaultOp(sdkPath), DefinitionLoader.sdef(ImportSDKProjectsWizard.class).wizard());
	}

	public ImportSDKProjectsWizard(final String newTitle) {
		super(_createDefaultOp(), DefinitionLoader.sdef(ImportSDKProjectsWizard.class).wizard());

		_title = newTitle;
	}

	@Override
	public IWizardPage[] getPages() {
		final IWizardPage[] wizardPages = super.getPages();

		if (wizardPages != null) {
			final SapphireWizardPage wizardPage = (SapphireWizardPage)wizardPages[0];

			final String message = wizardPage.getMessage();

			if (CoreUtil.isNullOrEmpty(message)) {
				wizardPage.setMessage(_initialMessage);
			}

			if ((wizardPage.getMessageType() == IMessageProvider.ERROR) && !_supressedFirstErrorMessage) {
				_supressedFirstErrorMessage = true;

				wizardPage.setMessage(_initialMessage);
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

	private static SDKProjectsImportOp _createDefaultOp() {
		return SDKProjectsImportOp.TYPE.instantiate();
	}

	private static SDKProjectsImportOp _createDefaultOp(final IPath sdkPath) {
		SDKProjectsImportOp importOp = SDKProjectsImportOp.TYPE.instantiate();

		importOp.setSdkLocation(PathBridge.create(sdkPath));

		return importOp;
	}

	private static final String _initialMessage = "Please select at least one project to import.";

	private boolean _supressedFirstErrorMessage = false;
	private String _title;

}