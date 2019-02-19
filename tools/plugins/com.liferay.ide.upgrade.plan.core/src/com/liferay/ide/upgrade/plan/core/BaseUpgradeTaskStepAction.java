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

import java.util.Dictionary;

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

		_stepId = getProperty(properties, "stepId");
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
		_upgradeTaskStepActionStatus = upgradeTaskStepActionStatus;
	}

	private String _stepId;
	private UpgradeTaskStepActionStatus _upgradeTaskStepActionStatus = UpgradeTaskStepActionStatus.INCOMPLETE;

}