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

package com.liferay.ide.upgrade.problems.core.internal.tasks;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionDoneEvent;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrateException;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.UpgradeProblemsCorePlugin;

import java.io.File;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"description=" + AutoCorrectUpgradeProblemsActionKeys.DESCRIPTION, "id=auto_correct_problems", "order=1",
		"stepId=auto_correct_problems", "title=" + AutoCorrectUpgradeProblemsActionKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class AutoCorrectUpgradeProblemsAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		final UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		IStatus retval = Status.OK_STATUS;

		Bundle bundle = FrameworkUtil.getBundle(AutoCorrectUpgradeProblemsAction.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		Stream<UpgradeProblem> stream = upgradeProblems.stream();

		stream.filter(
			upgradeProblem -> upgradeProblem.getStatus() != UpgradeProblem.STATUS_IGNORE
		).filter(
			upgradeProblem -> FileUtil.exists(upgradeProblem.getResource())
		).filter(
			upgradeProblem -> Objects.nonNull(upgradeProblem.getAutoCorrectContext())
		).forEach(
			upgradeProblem -> {
				String autoCorrectContext = upgradeProblem.getAutoCorrectContext();

				String autoCorrectKey = autoCorrectContext;

				int filterKeyIndex = autoCorrectContext.indexOf(":");

				if (filterKeyIndex > -1) {
					autoCorrectKey = autoCorrectContext.substring(0, filterKeyIndex);
				}

				try {
					String filter =
						"(&(auto.correct=" + autoCorrectKey + ")(version=" + upgradeProblem.getVersion() + "))";

					Collection<ServiceReference<AutoFileMigrator>> serviceReferences =
						bundleContext.getServiceReferences(AutoFileMigrator.class, filter);

					IResource resource = upgradeProblem.getResource();

					File file = FileUtil.getFile(resource);

					for (ServiceReference<AutoFileMigrator> reference : serviceReferences) {
						AutoFileMigrator autoMigrator = bundleContext.getService(reference);

						int problemsCorrected = 0;

						try {
							problemsCorrected = autoMigrator.correctProblems(file, upgradeProblems);
						}
						catch (AutoFileMigrateException afme) {
							UpgradeProblemsCorePlugin.logError(
								"Error encountered auto migrating file " + file.getAbsolutePath(), afme);
						}

						if ((problemsCorrected > 0) && (resource != null)) {
							IMarker problemMarker = resource.findMarker(upgradeProblem.getMarkerId());

							if ((problemMarker != null) && problemMarker.exists()) {
								problemMarker.delete();
							}
						}
					}
				}
				catch (InvalidSyntaxException ise) {
				}
				catch (CoreException ce) {
					UpgradeProblemsCorePlugin.logError("Could not read marker info. ", ce);
				}
			}
		);

		_upgradePlanner.dispatch(new UpgradeTaskStepActionDoneEvent(AutoCorrectUpgradeProblemsAction.this));

		return retval;
	}

	@Reference
	private UpgradePlanner _upgradePlanner;

}