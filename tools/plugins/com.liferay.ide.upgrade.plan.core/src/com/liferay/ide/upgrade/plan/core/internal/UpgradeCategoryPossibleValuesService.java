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

import com.liferay.ide.upgrade.plan.core.UpgradePlanElement;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskCategory;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public class UpgradeCategoryPossibleValuesService extends PossibleValuesService {

	public UpgradeCategoryPossibleValuesService() {
		Bundle bundle = FrameworkUtil.getBundle(UpgradeCategoryPossibleValuesService.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_upgradeCategoryServiceTracker = new ServiceTracker<>(bundleContext, UpgradeTaskCategory.class, null);

		_upgradeCategoryServiceTracker.open();
	}

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	protected void compute(Set<String> values) {
		values.addAll(_upgradeCategoryValues);
	}

	@Override
	protected void initPossibleValuesService() {
		_upgradeCategoryValues = new HashSet<>();

		Object[] services = _upgradeCategoryServiceTracker.getServices();

		for (Object service : services) {
			if (service instanceof UpgradePlanElement) {
				UpgradePlanElement upgradePlanElement = (UpgradePlanElement)service;

				_upgradeCategoryValues.add(upgradePlanElement.getId());
			}
		}
	}

	private ServiceTracker<UpgradeTaskCategory, UpgradeTaskCategory> _upgradeCategoryServiceTracker;
	private Set<String> _upgradeCategoryValues;

}