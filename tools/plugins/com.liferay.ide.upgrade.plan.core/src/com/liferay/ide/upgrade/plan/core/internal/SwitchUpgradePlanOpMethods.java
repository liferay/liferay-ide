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
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradePlansOp;

import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 */
public class SwitchUpgradePlanOpMethods {

	public static final Status execute(UpgradePlansOp upgradePlansOp, ProgressMonitor progressMonitor) {
		Bundle bundle = FrameworkUtil.getBundle(SwitchUpgradePlanOpMethods.class);

		ServiceTracker<UpgradePlanner, UpgradePlanner> serviceTracker = new ServiceTracker<>(
			bundle.getBundleContext(), UpgradePlanner.class, null);

		serviceTracker.open();

		UpgradePlanner upgradePlanner = serviceTracker.getService();

		if (upgradePlanner == null) {
			return Status.createErrorStatus("Could not get UpgradePlanner service");
		}

		String name = _getter.get(upgradePlansOp.getName());

		upgradePlanner.saveUpgradePlan(upgradePlanner.getCurrentUpgradePlan());

		UpgradePlan upgradePlan = upgradePlanner.loadUpgradePlan(name);

		upgradePlanner.startUpgradePlan(upgradePlan);

		return Status.createOkStatus();
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};

}