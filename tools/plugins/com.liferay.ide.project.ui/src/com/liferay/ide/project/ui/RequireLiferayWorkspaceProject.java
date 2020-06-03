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

package com.liferay.ide.project.ui;

import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.ui.workspace.ImportLiferayWorkspaceWizard;
import com.liferay.ide.project.ui.workspace.NewLiferayWorkspaceWizard;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Seiphon Wang
 */
public interface RequireLiferayWorkspaceProject {

	public default void promptIfLiferayWorkspaceNotExists(String wizardName) {
		IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (liferayWorkspaceProject == null) {
			Shell activeShell = UIUtil.getActiveShell();

			int requireLiferayWorkspace = MessageDialog.open(
				MessageDialog.QUESTION, activeShell, NLS.bind(Msgs.newElement, wizardName),
				NLS.bind(Msgs.needLiferayWorkspace, wizardName), SWT.NONE, "Create New LiferayWorkspace",
				"Import Existing LiferayWorkspace", "Cancel");

			switch (requireLiferayWorkspace) {
				case 0:
					NewLiferayWorkspaceOp newLiferayWorkspaceOp = NewLiferayWorkspaceOp.TYPE.instantiate();

					NewLiferayWorkspaceWizard newLiferayWorkspaceWizard = new NewLiferayWorkspaceWizard(
						newLiferayWorkspaceOp);

					WizardDialog wizardDialog = new WizardDialog(activeShell, newLiferayWorkspaceWizard);

					wizardDialog.open();

					break;

				case 1:
					ImportLiferayWorkspaceWizard wizard = new ImportLiferayWorkspaceWizard();

					WizardDialog dialog = new WizardDialog(activeShell, wizard);

					dialog.open();

					break;

				case 2:

					break;
			}
		}
	}

	public static class Msgs extends NLS {

		public static String needLiferayWorkspace;
		public static String newElement;

		static {
			initializeMessages(RequireLiferayWorkspaceProject.class.getName(), Msgs.class);
		}

	}

}