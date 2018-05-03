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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;

/**
 * @author Gregory Amerson
 */
public class WorkingSetCustomPart extends FormComponentPart {

	public WorkingSetCustomPart() {
	}

	@Override
	public FormComponentPresentation createPresentation(SwtPresentation parent, Composite composite) {
		_presentation = new WorkingSetPresentation(this, parent, composite, _workingSets);

		return _presentation;
	}

	public IWorkingSet[] getWorkingSets() {
		if (_presentation.isAddToWorkingSetsEnabled()) {
			return _workingSets.toArray(new IWorkingSet[0]);
		}
		else {
			return new IWorkingSet[0];
		}
	}

	@Override
	protected void init() {
		super.init();

		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		ISelectionService service = (ISelectionService)activeWorkbenchWindow.getSelectionService();

		ISelection selection = service.getSelection();

		if (selection instanceof IStructuredSelection) {
			final IWorkingSet workingSet = SelectionUtil.getSelectedWorkingSet((IStructuredSelection)selection);

			if (workingSet != null) {
				_workingSets.add(workingSet);
			}
		}
	}

	private WorkingSetPresentation _presentation;
	private List<IWorkingSet> _workingSets = new ArrayList<>();

}