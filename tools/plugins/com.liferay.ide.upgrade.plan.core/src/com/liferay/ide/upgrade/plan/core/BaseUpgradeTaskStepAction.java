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

package com.liferay.ide.upgrade.plan.core;

import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;

import java.util.Dictionary;
import java.util.List;
import java.util.stream.Stream;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Gregory Amerson
 */
public abstract class BaseUpgradeTaskStepAction extends BaseUpgradePlanElement implements UpgradeTaskStepAction {

	@Activate
	public void activate(ComponentContext componentContext) {
		super.activate(componentContext);

		Dictionary<String, Object> properties = componentContext.getProperties();

		_stepId = getStringProperty(properties, "stepId");

		_upgradePlanner = ServicesLookup.getSingleService(UpgradePlanner.class, null);
	}

	@Override
	public boolean completed() {
		if (!UpgradeTaskStepActionStatus.INCOMPLETE.equals(getStatus())) {
			return true;
		}

		return false;
	}

	public boolean enabled() {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		List<UpgradeTask> upgradeTasks = upgradePlan.getTasks();

		Stream<UpgradeTask> stream = upgradeTasks.stream();

		UpgradeTaskStep upgradeTaskStep = stream.map(
			upgradeTask -> upgradeTask.getSteps()
		).flatMap(
			upgradeTaskSteps -> upgradeTaskSteps.stream()
		).filter(
			upgradeStep -> getStepId().equals(upgradeStep.getId())
		).findFirst(
		).orElse(
			null
		);

		if ((upgradeTaskStep != null) && upgradeTaskStep.enabled()) {
			return true;
		}

		return false;
	}

	@Override
	public UpgradeTaskStepActionStatus getStatus() {
		return _upgradeTaskStepActionStatus;
	}

	@Override
	public String getStepId() {
		return _stepId;
	}

	@Override
	public void setStatus(UpgradeTaskStepActionStatus upgradeTaskStepActionStatus) {
		UpgradeTaskStepActionStatusChangedEvent event = new UpgradeTaskStepActionStatusChangedEvent(
			this, _upgradeTaskStepActionStatus, upgradeTaskStepActionStatus);

		_upgradeTaskStepActionStatus = upgradeTaskStepActionStatus;

		_upgradePlanner.dispatch(event);
	}

	private String _stepId;
	private UpgradePlanner _upgradePlanner;
	private UpgradeTaskStepActionStatus _upgradeTaskStepActionStatus = UpgradeTaskStepActionStatus.INCOMPLETE;

}