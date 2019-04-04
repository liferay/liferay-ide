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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.ui.internal.UpgradeProblemsUIPlugin;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Seiphon Wang
 */
public class IgnoreAlwaysAction extends SelectionProviderAction implements IAction {

	public IgnoreAlwaysAction(ISelectionProvider provider) {
		super(provider, "Ignore all problems of this type");

		Bundle bundle = FrameworkUtil.getBundle(IgnoreAlwaysAction.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();

		_upgradePlanner = _serviceTracker.getService();

		_serviceTracker.close();
	}

	@Override
	public void run() {
		final UpgradeProblem problem = _getProblemFromSelection(getSelection());

		new WorkspaceJob("Ignore all problems of this type") {

			public IStatus runInWorkspace(IProgressMonitor monitor) {
				IStatus retval = Status.OK_STATUS;

				try {
					UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

					Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

					Stream<UpgradeProblem> stream = upgradeProblems.stream();

					List<UpgradeProblem> problemList = stream.filter(
						upgradeProblem -> StringUtil.equals(upgradeProblem.getTicket(), problem.getTicket())
					).collect(
						Collectors.toList()
					);

					new IgnoreAction(getSelectionProvider()).run(problemList);
				}
				catch (Exception e) {
					retval = UpgradeProblemsUIPlugin.createErrorStatus("Unable to get file from problem");
				}

				return retval;
			}

		}.schedule();
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		final Object element = getStructuredSelection().getFirstElement();

		if (element instanceof UpgradeProblem && !CoreUtil.empty(((UpgradeProblem)element).getTicket())) {
			setEnabled(true);
		}
		else {
			setEnabled(false);
		}
	}

	private UpgradeProblem _getProblemFromSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structureSelection = (IStructuredSelection)selection;

			Object element = structureSelection.getFirstElement();

			if (element instanceof UpgradeProblem) {
				return (UpgradeProblem)element;
			}
		}

		return null;
	}

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;
	private UpgradePlanner _upgradePlanner;

}