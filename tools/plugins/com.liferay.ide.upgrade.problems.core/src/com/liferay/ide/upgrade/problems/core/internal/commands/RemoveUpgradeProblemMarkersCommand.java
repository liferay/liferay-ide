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

package com.liferay.ide.upgrade.problems.core.internal.commands;

import com.liferay.ide.upgrade.commands.core.MessagePrompt;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.MarkerSupport;
import com.liferay.ide.upgrade.problems.core.commands.RemoveUpgradeProblemMarkersCommandKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
@Component(
	property = "id=" + RemoveUpgradeProblemMarkersCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = UpgradeCommand.class
)
public class RemoveUpgradeProblemMarkersCommand implements UpgradeCommand, MarkerSupport {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		boolean result = _messagePrompt.prompt(
			"Remove Upgrade Proble Markers", "Are you sure to remove the upgrade problem markers?");

		if (result) {
			UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

			Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

			Stream<UpgradeProblem> stream = upgradeProblems.stream();

			stream.map(
				upgradeProblem -> findMarker(upgradeProblem.getResource(), upgradeProblem.getMarkerId())
			).filter(
				this::markerExists
			).forEach(
				this::deleteMarker
			);

			_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, new ArrayList<>(upgradeProblems)));

			upgradeProblems.clear();
		}

		return Status.OK_STATUS;
	}

	@Reference
	private MessagePrompt _messagePrompt;

	@Reference
	private UpgradePlanner _upgradePlanner;

}