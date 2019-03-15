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

package com.liferay.ide.upgrade.plan.ui.internal;

import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * @author Christopher Bryan Boyd
 */
public class RestartUpgradeHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		UpgradePlanner upgradePlanner = ServicesLookup.getSingleService(UpgradePlanner.class, null);

		UpgradePlan upgradePlan = upgradePlanner.getCurrentUpgradePlan();

		if (upgradePlan == null) {
			return null;
		}

		List<UpgradeStep> rootSteps = upgradePlan.getRootSteps();

		for (UpgradeStep step : rootSteps) {
			upgradePlanner.restartStep(step);
		}

		return null;
	}

}