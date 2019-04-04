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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrateException;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.ui.internal.MigrationUtil;
import com.liferay.ide.upgrade.problems.ui.internal.UpgradeProblemsUIPlugin;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
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
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Seiphon Wang
 */
public class AutoCorrectAction extends SelectionProviderAction implements IAction {

	public AutoCorrectAction(ISelectionProvider provider) {
		super(provider, "Correct automatically");

		Bundle bundle = FrameworkUtil.getBundle(AutoCorrectAction.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();

		_upgradePlanner = _serviceTracker.getService();

		_serviceTracker.close();
	}

	@Override
	public void run() {
		ISelection selection = getSelection();

		List<UpgradeProblem> problems = MigrationUtil.getProblemsFromSelection(selection);

		runWithAutoCorrect(problems);
	}

	public IStatus runWithAutoCorrect(List<UpgradeProblem> upgradeProblems) {
		WorkspaceJob job = new WorkspaceJob("Auto correcting breaking changes.") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) {
				IStatus retval = Status.OK_STATUS;

				try {
					List<UpgradeProblem> markDoneNeededProblems = new ArrayList<>();

					for (UpgradeProblem upgradeProblem : upgradeProblems) {
						String autoCorrectKey = null;

						String autoCorrectContext = upgradeProblem.getAutoCorrectContext();

						if (autoCorrectContext == null) {
							continue;
						}

						int filterKeyIndex = autoCorrectContext.indexOf(":");

						if (filterKeyIndex > -1) {
							autoCorrectKey = autoCorrectContext.substring(0, filterKeyIndex);
						}
						else {
							autoCorrectKey = autoCorrectContext;
						}

						Bundle bundle = FrameworkUtil.getBundle(AutoCorrectAction.class);

						BundleContext context = bundle.getBundleContext();

						IResource resource = upgradeProblem.getResource();

						File file = FileUtil.getFile(resource);

						Collection<ServiceReference<AutoFileMigrator>> refs = context.getServiceReferences(
							AutoFileMigrator.class, "(auto.correct=" + autoCorrectKey + ")");

						for (ServiceReference<AutoFileMigrator> ref : refs) {
							AutoFileMigrator autoFileMigrator = context.getService(ref);

							int problemsCorrected = autoFileMigrator.correctProblems(
								file, Collections.singletonList(upgradeProblem));

							if (problemsCorrected > 0) {
								if (resource != null) {
									IMarker problemMarker = resource.findMarker(upgradeProblem.getMarkerId());

									if (MarkerUtil.exists(problemMarker)) {
										problemMarker.delete();
									}
								}
							}
						}

						UpgradePlan currentUpgradePlan = _upgradePlanner.getCurrentUpgradePlan();

						Collection<UpgradeProblem> previoursUpgradeProblems = currentUpgradePlan.getUpgradeProblems();

						previoursUpgradeProblems.remove(upgradeProblem);

						markDoneNeededProblems.add(upgradeProblem);
					}

					new MarkDoneAction(getSelectionProvider()).run(markDoneNeededProblems);
				}
				catch (AutoFileMigrateException | CoreException | InvalidSyntaxException e) {
					return retval = UpgradeProblemsUIPlugin.createErrorStatus("Unable to auto correct problem", e);
				}

				return retval;
			}

		};

		job.schedule();

		return Status.OK_STATUS;
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		if (selection.isEmpty()) {
			setEnabled(false);
		}
		else {
			boolean selectionCompatible = true;

			Iterator<?> items = selection.iterator();
			Object lastItem = null;

			while (items.hasNext()) {
				Object item = items.next();

				if (!(item instanceof UpgradeProblem)) {
					selectionCompatible = false;

					break;
				}

				UpgradeProblem upgradeProblem = (UpgradeProblem)item;

				String autoCorrectContext = upgradeProblem.getAutoCorrectContext();

				if (autoCorrectContext == null) {
					selectionCompatible = false;

					break;
				}

				if (lastItem != null) {
					String itemAutoCorrectContext = ((UpgradeProblem)item).getAutoCorrectContext();

					String prCurrentKey = itemAutoCorrectContext.substring(0, autoCorrectContext.indexOf(":"));

					String lastItemAutoCorrectContext = ((UpgradeProblem)lastItem).getAutoCorrectContext();

					String prLastKey = lastItemAutoCorrectContext.substring(0, autoCorrectContext.indexOf(":"));

					if (!prCurrentKey.equals(prLastKey)) {
						selectionCompatible = false;

						break;
					}
				}

				lastItem = item;
			}

			setEnabled(selectionCompatible);

			setText("Correct automatically");
		}
	}

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;
	private UpgradePlanner _upgradePlanner;

}