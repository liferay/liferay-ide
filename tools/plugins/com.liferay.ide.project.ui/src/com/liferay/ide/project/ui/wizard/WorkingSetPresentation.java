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

import java.util.List;

import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkingSet;

/**
 * @author Gregory Amerson
 */
public class WorkingSetPresentation extends FormComponentPresentation {

	public WorkingSetPresentation(
		FormComponentPart part, SwtPresentation parent, Composite composite, List<IWorkingSet> workingSets) {

		super(part, parent, composite);

		_workingSets = workingSets;
	}

	public boolean isAddToWorkingSetsEnabled() {
		return _workingSetGroup.addToWorkingSetButton.isEnabled();
	}

	@Override
	public WorkingSetCustomPart part() {
		return (WorkingSetCustomPart)super.part();
	}

	@Override
	public void render() {
		final Composite partParent = composite();

		final Composite localParent = new Composite(partParent, SWT.NONE);

		final GridData data = new GridData(SWT.FILL, SWT.DEFAULT, true, false, 3, 1);

		data.horizontalIndent = 8;

		localParent.setLayoutData(data);

		localParent.setLayout(new GridLayout(3, false));

		register(localParent);

		_workingSetGroup = new WorkingSetGroup(localParent, _workingSets, shell());
	}

	private WorkingSetGroup _workingSetGroup;
	private final List<IWorkingSet> _workingSets;

}