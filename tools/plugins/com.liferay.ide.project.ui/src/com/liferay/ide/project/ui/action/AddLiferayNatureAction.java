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

import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.action.AbstractObjectAction;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Simon Jiang
 */
public class AddLiferayNatureAction extends AbstractObjectAction {

	@Override
	public void run(IAction action) {
		if (fSelection instanceof IStructuredSelection) {
			IStructuredSelection structureSelection = (IStructuredSelection)fSelection;

			Object[] elems = structureSelection.toArray();

			IProject project = null;

			Object elem = elems[0];

			if (elem instanceof IProject) {
				project = (IProject)elem;

				try {
					LiferayNature.addLiferayNature(project, new NullProgressMonitor());
				}
				catch (CoreException ce) {
					ProjectUI.logError("Failed to add Liferay Project Nature", ce);
				}
			}
		}
	}

}