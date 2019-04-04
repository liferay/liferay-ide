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

package com.liferay.ide.upgrade.problems.ui.internal.migration;

import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.ui.internal.UpgradeProblemsUIPlugin;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Seiphon Wang
 */
public class MarkUndoneAction extends ProblemAction {

	public MarkUndoneAction() {
		this(new DummySelectionProvider());
	}

	public MarkUndoneAction(ISelectionProvider provider) {
		super(provider, "Mark undone");
	}

	public void run(List<UpgradeProblem> upgradeProblems) {
		super.run(upgradeProblems);

		Stream<UpgradeProblem> stream = upgradeProblems.stream();

		stream.forEach(upgradeProblem -> upgradeProblem.setStatus(UpgradeProblem.STATUS_NOT_RESOLVED));

		Viewer viewer = (Viewer)getSelectionProvider();

		UIUtil.async(
			new Runnable() {

				public void run() {
					viewer.refresh();
				}

			});
	}

	@Override
	protected IStatus runWithMarker(UpgradeProblem upgradeProblem) {
		IStatus retval = Status.OK_STATUS;

		IResource resource = upgradeProblem.getResource();

		IMarker marker = resource.getMarker(upgradeProblem.getMarkerId());

		try {
			if (marker.exists()) {
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute("migrationProblem.resolved", Boolean.FALSE);
			}

			upgradeProblem.setStatus(UpgradeProblem.STATUS_NOT_RESOLVED);
		}
		catch (CoreException ce) {
			retval = UpgradeProblemsUIPlugin.createErrorStatus("Unable to unmark done the problem");
		}

		return retval;
	}

}