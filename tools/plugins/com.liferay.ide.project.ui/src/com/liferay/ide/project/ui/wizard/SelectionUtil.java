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

import com.liferay.ide.project.ui.WorkingSets;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkingSet;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class SelectionUtil {

	public static IWorkingSet getSelectedWorkingSet(IStructuredSelection selection) {
		Object element = selection == null ? null : selection.getFirstElement();

		if (element == null) {
			return null;
		}

		IWorkingSet workingSet = getType(element, IWorkingSet.class);

		if (workingSet != null) {
			return workingSet;
		}

		IResource resource = getType(element, IResource.class);

		if (resource != null) {
			return WorkingSets.getAssignedWorkingSet(resource.getProject());
		}

		return null;
	}

	/**
	 * Checks if the object belongs to a given type and returns it or a suitable
	 * adapter.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getType(Object element, Class<T> type) {
		if (element == null) {
			return null;
		}

		if (type.isInstance(element)) {
			return (T)element;
		}

		if (element instanceof IAdaptable) {
			T adapter = (T)((IAdaptable)element).getAdapter(type);

			if (adapter != null) {
				return adapter;
			}
		}

		IAdapterManager adapterManager = Platform.getAdapterManager();

		return (T)adapterManager.getAdapter(element, type);
	}

}