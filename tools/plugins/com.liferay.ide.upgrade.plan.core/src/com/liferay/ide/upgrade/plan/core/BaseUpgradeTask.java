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
public abstract class BaseUpgradeTask extends BaseUpgradePlanElement implements UpgradeTask {

	@Activate
	public void activate(ComponentContext componentContext) {
		super.activate(componentContext);

		Dictionary<String, Object> properties = componentContext.getProperties();

		_categoryId = getProperty(properties, "categoryId");

		_lookupTaskSteps(componentContext);
	}

	@Override
	public String getCategoryId() {
		return _categoryId;
	}

	@Override
	public List<UpgradeTaskStep> getSteps() {
		return Collections.unmodifiableList(_upgradeTaskSteps);
	}

	private void _lookupTaskSteps(ComponentContext componentContext) {
		BundleContext bundleContext = componentContext.getBundleContext();

		try {
			Collection<ServiceReference<UpgradeTaskStep>> upgradeTaskStepServiceReferences =
				bundleContext.getServiceReferences(UpgradeTaskStep.class, "(taskId=" + getId() + ")");

			List<UpgradeTaskStep> upgradeTaskSteps = ServicesLookup.getOrderedServices(
				bundleContext, upgradeTaskStepServiceReferences);

			UpgradePlanner upgradePlanner = ServicesLookup.getSingleService(UpgradePlanner.class, null);

			if (upgradePlanner == null) {
				return;
			}

			Stream<UpgradeTaskStep> stream = upgradeTaskSteps.stream();

			_upgradeTaskSteps = stream.filter(
				upgradeTaskStep -> upgradeTaskStep.appliesTo(upgradePlanner.getCurrentUpgradePlan())
			).collect(
				Collectors.toList()
			);
		}
		catch (InvalidSyntaxException ise) {
		}
	}

	private String _categoryId;
	private List<UpgradeTaskStep> _upgradeTaskSteps;

}