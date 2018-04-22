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

package com.liferay.ide.ui.snippets.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractModelWizard extends Wizard {

	public AbstractModelWizard(IEditorPart fEditorPart) {
		setWindowTitle(Msgs.model);
		editorPart = fEditorPart;
	}

	@Override
	public void addPages() {
		wizardPage = createModelWizardPage(editorPart);

		addPage(wizardPage);
	}

	public String getModel() {
		return wizardPage.getModel();
	}

	public String[] getPropertyColumns() {
		return wizardPage.getPropertyColumns();
	}

	public String getVarName() {
		return wizardPage.getVarName();
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	protected abstract AbstractModelWizardPage createModelWizardPage(IEditorPart editorPart);

	protected IEditorPart editorPart;
	protected AbstractModelWizardPage wizardPage;

	private static class Msgs extends NLS {

		public static String model;

		static {
			initializeMessages(AbstractModelWizard.class.getName(), Msgs.class);
		}

	}

}