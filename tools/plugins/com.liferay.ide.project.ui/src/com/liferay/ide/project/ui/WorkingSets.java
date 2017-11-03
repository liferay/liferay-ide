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

package com.liferay.ide.project.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Gregory Amerson
 */
public class WorkingSets {

	/**
	 * Returns one of the working sets the element directly belongs to. Returns
	 * {@code null} if the element does not belong to any working set.
	 *
	 * @since 1.5
	 */
	public static IWorkingSet getAssignedWorkingSet(IResource element) {
		IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();

		for (IWorkingSet workingSet : workingSetManager.getWorkingSets()) {
			for (IAdaptable adaptable : workingSet.getElements()) {
				if (adaptable.getAdapter(IResource.class) == element) {
					return workingSet;
				}
			}
		}

		return null;
	}

	/**
	 * Returns all working sets the element directly belongs to. Returns empty
	 * collection if the element does not belong to any working set. The order of
	 * returned working sets is not specified.
	 *
	 * @since 1.5
	 */
	public static List<IWorkingSet> getAssignedWorkingSets(IResource element) {
		List<IWorkingSet> list = new ArrayList<>();
		IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();

		for (IWorkingSet workingSet : workingSetManager.getWorkingSets()) {
			for (IAdaptable adaptable : workingSet.getElements()) {
				if (adaptable.getAdapter(IResource.class) == element) {
					list.add(workingSet);
				}
			}
		}

		return list;
	}

	public static String[] getWorkingSets() {
		List<String> workingSets = new ArrayList<>();
		IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();

		for (IWorkingSet workingSet : workingSetManager.getWorkingSets()) {
			if (workingSet.isVisible()) {
				workingSets.add(workingSet.getName());
			}
		}

		return workingSets.toArray(new String[workingSets.size()]);
	}

}