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

import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.ui.util.UIUtil;

import java.util.List;
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
public class IgnoreAction extends SelectionProviderAction implements UpgradeProblemUISupport {

	public IgnoreAction(ISelectionProvider provider) {
		super(provider, "Ignore");

		Bundle bundle = FrameworkUtil.getBundle(IgnoreAlwaysAction.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();
	}

	@Override
	public void run() {
		List<UpgradeProblem> upgradeProblems = getUpgradeProblems(getSelection());

		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		UpgradePlan upgradePlan = upgradePlanner.getCurrentUpgradePlan();

		Stream<UpgradeProblem> stream = upgradeProblems.stream();

		stream.forEach(this::ignore);

		upgradePlan.addIgnoredProblems(upgradeProblems);

		Viewer viewer = (Viewer)getSelectionProvider();

		UIUtil.async(() -> viewer.refresh());
	}

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

}