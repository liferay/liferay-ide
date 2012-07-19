package com.liferay.ide.common.ui.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


public abstract class AbstractObjectAction implements IObjectActionDelegate {

	protected ISelection fSelection;

	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}

	public Display getDisplay() {
		Display display = Display.getCurrent();

		if (display == null)
			display = Display.getDefault();

		return display;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}
