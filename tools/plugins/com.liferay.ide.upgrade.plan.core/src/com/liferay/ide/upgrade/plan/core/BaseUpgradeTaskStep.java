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

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Gregory Amerson
 */
public abstract class BaseUpgradeTaskStep extends BaseUpgradePlanElement implements UpgradeTaskStep {

	@Activate
	public void activate(ComponentContext componentContext) {
		super.activate(componentContext);

		Dictionary<String, Object> properties = componentContext.getProperties();

		_requirement = getProperty(properties, "requirement");
		_taskId = getProperty(properties, "taskId");
		_url = getProperty(properties, "url");

		_lookupActions(componentContext);
	}

	@Override
	public List<UpgradeTaskStepAction> getActions() {
		return Collections.unmodifiableList(_upgradeTaskStepActions);
	}

	@Override
	public UpgradeTaskStepRequirement getRequirement() {
		return UpgradeTaskStepRequirement.valueOf(UpgradeTaskStepRequirement.class, _requirement.toUpperCase());
	}

	@Override
	public UpgradeTaskStepStatus getStatus() {
		return _upgradeTaskStepStatus;
	}

	@Override
	public String getTaskId() {
		return _taskId;
	}

	@Override
	public String getUrl() {
		return _url;
	}

	private void _lookupActions(ComponentContext componentContext) {
		BundleContext bundleContext = componentContext.getBundleContext();

		try {
			Collection<ServiceReference<UpgradeTaskStepAction>> upgradeTaskStepActionServiceReferences =
				bundleContext.getServiceReferences(UpgradeTaskStepAction.class, "(stepId=" + getId() + ")");

			List<UpgradeTaskStepAction> upgradeTaskStepActions = ServicesLookup.getOrderedServices(
				bundleContext, upgradeTaskStepActionServiceReferences);

			Stream<UpgradeTaskStepAction> stream = upgradeTaskStepActions.stream();

			UpgradePlanner upgradePlanner = ServicesLookup.getSingleService(UpgradePlanner.class, null);

			_upgradeTaskStepActions = stream.filter(
				upgradeTaskStepAction -> upgradeTaskStepAction.appliesTo(upgradePlanner.getCurrentUpgradePlan())
			).collect(
				Collectors.toList()
			);
		}
		catch (InvalidSyntaxException ise) {
		}
	}

	private String _requirement;
	private String _taskId;
	private List<UpgradeTaskStepAction> _upgradeTaskStepActions;
	private UpgradeTaskStepStatus _upgradeTaskStepStatus = UpgradeTaskStepStatus.INCOMPLETE;
	private String _url;

}