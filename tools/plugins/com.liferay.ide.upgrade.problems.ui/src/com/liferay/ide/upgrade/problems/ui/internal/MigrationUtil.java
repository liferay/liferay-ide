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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Seiphon Wang
 */
public class MigrationUtil {

	public static List<UpgradeProblem> getProblemsFromSelection(ISelection selection) {
		final List<UpgradeProblem> problems = new ArrayList<>();

		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection)selection;

			Iterator<?> elements = ss.iterator();

			while (elements.hasNext()) {
				Object element = elements.next();

				if (element instanceof UpgradeProblem) {
					problems.add((UpgradeProblem)element);
				}
			}
		}

		return problems;
	}

}