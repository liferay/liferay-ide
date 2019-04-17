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

package com.liferay.ide.upgrade.plan.core.internal;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.upgrade.plan.core.NewUpgradePlanOp;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.io.IOException;

import java.nio.file.Paths;

import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class NewUpgradePlanOpMethods {

	public static final Status execute(NewUpgradePlanOp newUpgradePlanOp, ProgressMonitor progressMonitor) {
		Bundle bundle = FrameworkUtil.getBundle(NewUpgradePlanOpMethods.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceTracker<UpgradePlanner, UpgradePlanner> serviceTracker = new ServiceTracker<>(
			bundleContext, UpgradePlanner.class, null);

		serviceTracker.open();

		UpgradePlanner upgradePlanner = serviceTracker.getService();

		if (upgradePlanner == null) {
			return Status.createErrorStatus("Could not get UpgradePlanner service");
		}

		String name = _getter.get(newUpgradePlanOp.getName());

		String upgradePlanOutline = _getter.get(newUpgradePlanOp.getUpgradePlanOutline());

		String currentVersion = _getter.get(newUpgradePlanOp.getCurrentVersion());

		String targetVersion = _getter.get(newUpgradePlanOp.getTargetVersion());

		Path path = _getter.get(newUpgradePlanOp.getLocation());

		java.nio.file.Path sourceCodeLocation = null;

		if (path != null) {
			sourceCodeLocation = Paths.get(path.toOSString());
		}

		try {
			UpgradePlan upgradePlan = upgradePlanner.newUpgradePlan(
				name, currentVersion, targetVersion, sourceCodeLocation, upgradePlanOutline);

			upgradePlanner.startUpgradePlan(upgradePlan);
		}
		catch (IOException ioe) {
			return Status.createErrorStatus("Could not create upgrade plan named: " + name, ioe);
		}

		return Status.createOkStatus();
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};

}