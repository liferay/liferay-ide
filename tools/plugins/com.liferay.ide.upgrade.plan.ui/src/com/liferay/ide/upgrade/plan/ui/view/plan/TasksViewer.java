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

package com.liferay.ide.upgrade.plan.ui.view.plan;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Terry Jia
 */
public class TasksViewer {

	public TasksViewer(Composite parent) {
		_taskList = new ListViewer(parent);

		_taskList.setContentProvider(new TasksContentProvider());
		_taskList.setLabelProvider(new TaskLabelProvider());

		_taskList.setInput(_getInitialInput());
	}

	public void addSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
		_taskList.addSelectionChangedListener(selectionChangedListener);
	}

	private Object _getInitialInput() {

		// TODO need to get initial input basing on Upgrade Plan

		return new Object();
	}

	private ListViewer _taskList;

}