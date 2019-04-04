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

import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.MarkerSupport;
import com.liferay.ide.upgrade.problems.ui.internal.MigrationUtil;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
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
public abstract class ProblemAction extends SelectionProviderAction implements IAction, MarkerSupport {

	public ProblemAction(ISelectionProvider provider, String text) {
		super(provider, text);

		Bundle bundle = FrameworkUtil.getBundle(ProblemAction.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();

		upgradePlanner = _serviceTracker.getService();

		_serviceTracker.close();
	}

	@Override
	public void run() {
		List<UpgradeProblem> upgradeProblems = MigrationUtil.getProblemsFromSelection(getSelection());

		run(upgradeProblems);
	}

	public void run(List<UpgradeProblem> upgradeProblems) {
		Stream<UpgradeProblem> stream = upgradeProblems.stream();

		stream.filter(
			upgradeProblem -> {
				IResource resource = upgradeProblem.getResource();

				if ((resource != null) && resource.exists()) {
					IMarker marker = resource.getMarker(upgradeProblem.getMarkerId());

					if (marker != null) {
						return true;
					}
				}

				return false;
			}
		).forEach(
			this::runWithMarker
		);
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		Object element = selection.getFirstElement();

		setEnabled(element instanceof UpgradeProblem);
	}

	protected abstract IStatus runWithMarker(UpgradeProblem upgradeProblem);

	protected UpgradePlanner upgradePlanner;

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

}