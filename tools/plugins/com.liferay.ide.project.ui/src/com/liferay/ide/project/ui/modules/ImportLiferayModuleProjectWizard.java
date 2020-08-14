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

package com.liferay.ide.project.ui.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOp;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Andy Wu
 */
public class ImportLiferayModuleProjectWizard
	extends SapphireWizard<ImportLiferayModuleProjectOp> implements INewWizard, IWorkbenchWizard {

	public ImportLiferayModuleProjectWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(ImportLiferayModuleProjectWizard.class).wizard());
	}

	@Override
	public IWizardPage[] getPages() {
		final IWizardPage[] wizardPages = super.getPages();

		if (!_firstErrorMessageRemoved && (wizardPages != null)) {
			final SapphireWizardPage wizardPage = (SapphireWizardPage)wizardPages[0];

			final int messageType = wizardPage.getMessageType();

			if ((messageType == IMessageProvider.ERROR) && !CoreUtil.isNullOrEmpty(wizardPage.getMessage())) {
				wizardPage.setMessage(null, SapphireWizardPage.NONE);

				_firstErrorMessageRemoved = true;
			}
		}

		return wizardPages;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	private static ImportLiferayModuleProjectOp _createDefaultOp() {
		return ImportLiferayModuleProjectOp.TYPE.instantiate();
	}

	private boolean _firstErrorMessageRemoved = false;

}