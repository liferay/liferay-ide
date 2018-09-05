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

package com.liferay.ide.gradle.ui.action;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.core.ModuleCoreUtil;
import com.liferay.ide.ui.action.AbstractObjectAction;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Andy Wu
 */
public class AddJSPValidationAction extends AbstractObjectAction {

	@Override
	public void run(IAction action) {
		if (fSelection instanceof IStructuredSelection) {
			Object[] elems = ((IStructuredSelection)fSelection).toArray();

			Object elem = elems[0];

			if (elem instanceof IProject) {
				IProject project = (IProject)elem;

				try {
					ModuleCoreUtil.addFacetsIfNeeded(FileUtil.getFile(project), new NullProgressMonitor());
				}
				catch (CoreException ce) {
				}
			}
		}
	}

}