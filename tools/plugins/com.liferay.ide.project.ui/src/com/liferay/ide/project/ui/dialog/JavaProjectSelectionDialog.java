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

package com.liferay.ide.project.ui.dialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Andy Wu
 */
public class JavaProjectSelectionDialog extends ProjectSelectionDialog {

	public JavaProjectSelectionDialog(Shell parentShell) {
		super(parentShell, _defaultFilter);

		setTitle("Project Selection");
		setMessage("Select project");
	}

	public JavaProjectSelectionDialog(Shell parentShell, ViewerFilter filter) {
		super(parentShell, filter);

		setTitle("Project Selection");
		setMessage("Select project");
	}

	@Override
	protected boolean checkProject(IJavaProject project) {
		return true;
	}

	private static final ViewerFilter _defaultFilter = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IJavaProject) {
				IProject project = ((IJavaProject)element).getProject();

				if ("External Plug-in Libraries".equals(project.getName())) {
					return false;
				}

				return true;
			}

			return false;
		}

	};

}