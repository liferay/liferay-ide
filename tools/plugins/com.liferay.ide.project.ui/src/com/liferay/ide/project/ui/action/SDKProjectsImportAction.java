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

package com.liferay.ide.project.ui.action;

import com.liferay.ide.project.ui.wizard.ImportSDKProjectsWizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class SDKProjectsImportAction implements IObjectActionDelegate {

	public SDKProjectsImportAction() {
	}

	public Display getDisplay() {
		Display display = Display.getCurrent();

		if (display == null) {
			display = Display.getDefault();
		}

		return display;
	}

	@Override
	public void run(IAction action) {
		if (_fSelection instanceof IStructuredSelection) {
			IStructuredSelection fStructureSelection = (IStructuredSelection)_fSelection;

			Object[] elems = fStructureSelection.toArray();

			IProject project = null;

			Object elem = elems[0];

			if (elem instanceof IProject) {
				project = (IProject)elem;
			}

			ImportSDKProjectsWizard wizard = new ImportSDKProjectsWizard(project.getLocation());

			final Display display = getDisplay();

			final WizardDialog dialog = new WizardDialog(display.getActiveShell(), wizard);

			BusyIndicator.showWhile(
				display,
				new Runnable() {

					@Override
					public void run() {
						dialog.open();
					}

				});
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		_fSelection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	private ISelection _fSelection;

}