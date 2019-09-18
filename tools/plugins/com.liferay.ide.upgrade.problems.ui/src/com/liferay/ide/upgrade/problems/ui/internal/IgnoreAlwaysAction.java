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

import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.ui.util.UIUtil;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.actions.SelectionProviderAction;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Seiphon Wang
 * @author Terry Jia
 */
public class IgnoreAlwaysAction extends SelectionProviderAction implements UpgradeProblemUISupport {

	public IgnoreAlwaysAction(ISelectionProvider provider) {
		super(provider, "Ignore all problems of this type");

		Bundle bundle = FrameworkUtil.getBundle(IgnoreAlwaysAction.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();
	}

	@Override
	public void run() {
		UpgradeProblem problem = getUpgradeProblem(getSelection());

		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		UpgradePlan upgradePlan = upgradePlanner.getCurrentUpgradePlan();

		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		Stream<UpgradeProblem> stream = upgradeProblems.stream();

		stream.filter(
			upgradeProblem ->
				StringUtil.equals(upgradeProblem.getTicket(), problem.getTicket()) &&
				StringUtil.equals(upgradeProblem.getTitle(), problem.getTitle())
		).forEach(
			this::ignore
		);

		Set<UpgradeProblem> ignoredProblems = upgradeProblems.stream(
		).filter(
			upgradeProblem -> UpgradeProblem.STATUS_IGNORE == upgradeProblem.getStatus()
		).collect(
			Collectors.toSet()
		);

		upgradePlan.addIgnoredProblems(ignoredProblems);

		Viewer viewer = (Viewer)getSelectionProvider();

		UIUtil.async(() -> viewer.refresh());
	}

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

}