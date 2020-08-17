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

import com.liferay.ide.ui.LiferayUIPlugin;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractCommandAction extends AbstractObjectAction {

	public void run(IAction action) {
		if (fSelection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)fSelection;

			Object[] elems = structuredSelection.toArray();

			Object elem = elems[0];

			IProject project = null;

			if (elem instanceof IFile) {
				IFile projectFile = (IFile)elem;

				project = projectFile.getProject();
			}
			else if (elem instanceof IProject) {
				project = (IProject)elem;
			}

			if (project != null) {
				IWorkbench workbench = PlatformUI.getWorkbench();

				ICommandService cmdService = (ICommandService)workbench.getService(ICommandService.class);

				Command buildServiceCmd = cmdService.getCommand(getCommandId());

				try {
					buildServiceCmd.executeWithChecks(new ExecutionEvent());
				}
				catch (Exception e) {
					LiferayUIPlugin.logError("Error running command " + getCommandId(), e);
				}
			}
		}
	}

	protected abstract String getCommandId();

}