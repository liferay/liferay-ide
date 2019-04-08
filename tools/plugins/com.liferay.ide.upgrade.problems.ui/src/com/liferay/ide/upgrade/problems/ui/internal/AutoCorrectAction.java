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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.MarkerSupport;

import java.io.File;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.actions.SelectionProviderAction;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Seiphon Wang
 * @author Terry Jia
 */
public class AutoCorrectAction extends SelectionProviderAction implements UpgradeProblemSupport, MarkerSupport {

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

		String autoCorrectContext = upgradeProblem.getAutoCorrectContext();

		if (autoCorrectContext == null) {
			return;
		}

		int filterKeyIndex = autoCorrectContext.indexOf(":");

		if (filterKeyIndex > -1) {
			autoCorrectContext = autoCorrectContext.substring(0, filterKeyIndex);
		}

		Bundle bundle = FrameworkUtil.getBundle(AutoCorrectAction.class);

		BundleContext context = bundle.getBundleContext();

		IResource resource = upgradeProblem.getResource();

		File file = FileUtil.getFile(resource);

		try {
			Collection<ServiceReference<AutoFileMigrator>> serviceReferences = context.getServiceReferences(
				AutoFileMigrator.class, "(auto.correct=" + autoCorrectContext + ")");

			for (ServiceReference<AutoFileMigrator> serviceReference : serviceReferences) {
				AutoFileMigrator autoFileMigrator = context.getService(serviceReference);

				int problemsCorrected = autoFileMigrator.correctProblems(
					file, Collections.singletonList(upgradeProblem));

				if (problemsCorrected > 0) {
					IMarker marker = findMarker(upgradeProblem);

					if (marker != null) {
						deleteMarker(marker);
					}
				}
			}

			UpgradePlanner upgradePlanner = _serviceTracker.getService();

			UpgradePlan currentUpgradePlan = upgradePlanner.getCurrentUpgradePlan();

			Collection<UpgradeProblem> upgradeProblems = currentUpgradePlan.getUpgradeProblems();

			upgradeProblems.remove(upgradeProblem);

			Viewer viewer = (Viewer)getSelectionProvider();

			UIUtil.async(() -> viewer.refresh());
		}
		catch (Exception e) {
		}
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		if (selection.isEmpty() || (selection.size() > 2)) {
			setEnabled(false);

			return;
		}

		Iterator<?> items = selection.iterator();

		Object item = items.next();

		if (!(item instanceof UpgradeProblem)) {
			setEnabled(false);

			return;
		}

		UpgradeProblem upgradeProblem = (UpgradeProblem)item;

		String autoCorrectContext = upgradeProblem.getAutoCorrectContext();

		setEnabled(CoreUtil.isNotNullOrEmpty(autoCorrectContext));
	}

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

}