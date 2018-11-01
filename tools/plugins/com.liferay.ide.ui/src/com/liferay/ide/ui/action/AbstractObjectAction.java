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

package com.liferay.ide.ui.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractObjectAction implements IObjectActionDelegate {

	public Display getDisplay() {
		Display display = Display.getCurrent();

		if (display == null) {
			display = Display.getDefault();
		}

		return display;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	protected void afterAction() {
	}

	protected void beforeAction() {
	}

	protected ISelection fSelection;

}