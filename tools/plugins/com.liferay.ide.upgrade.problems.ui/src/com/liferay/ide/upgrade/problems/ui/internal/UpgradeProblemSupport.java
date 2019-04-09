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

package com.liferay.ide.upgrade.problems.ui.internal;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.MarkerSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Seiphon Wang
 */
public interface UpgradeProblemSupport extends MarkerSupport {

	public default UpgradeProblem getUpgradeProblem(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structureSelection = (IStructuredSelection)selection;

			Object element = structureSelection.getFirstElement();

			if (element instanceof UpgradeProblem) {
				return (UpgradeProblem)element;
			}
		}

		return null;
	}

	public default List<UpgradeProblem> getUpgradeProblems(ISelection selection) {
		List<UpgradeProblem> upgradeProblems = new ArrayList<>();

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)selection;

			Iterator<?> elements = structuredSelection.iterator();

			while (elements.hasNext()) {
				Object element = elements.next();

				if (element instanceof UpgradeProblem) {
					upgradeProblems.add((UpgradeProblem)element);
				}
			}
		}

		return upgradeProblems;
	}

	public default void ignore(UpgradeProblem upgradeProblem) {
		upgradeProblem.setStatus(UpgradeProblem.STATUS_IGNORE);

		IMarker marker = findMarker(upgradeProblem);

		if (markerExists(marker)) {
			deleteMarker(marker);
		}
	}

}