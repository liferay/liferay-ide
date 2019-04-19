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

import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;

import java.io.File;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Seiphon Wang
 * @author Terry Jia
 */
public class AutoCorrectAction extends BaseAutoCorrectAction implements UpgradeProblemSupport {

	public AutoCorrectAction(ISelectionProvider provider) {
		super(provider, "Correct automatically");

		Bundle bundle = FrameworkUtil.getBundle(AutoCorrectAction.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();
	}

	@Override
	public void run() {
		ISelection selection = getSelection();

		UpgradeProblem upgradeProblem = getUpgradeProblem(selection);

		File file = getUpgradeFile(upgradeProblem);

		autoCorrect(file, upgradeProblem, true);

		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		UpgradePlan currentUpgradePlan = upgradePlanner.getCurrentUpgradePlan();

		Collection<UpgradeProblem> upgradeProblems = currentUpgradePlan.getUpgradeProblems();

		upgradeProblems.remove(upgradeProblem);

		Viewer viewer = (Viewer)getSelectionProvider();

		UIUtil.async(() -> viewer.refresh());
	}

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

}