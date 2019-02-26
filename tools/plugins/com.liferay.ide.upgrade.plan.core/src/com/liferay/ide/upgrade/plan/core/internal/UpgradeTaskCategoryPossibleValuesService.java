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

import com.liferay.ide.upgrade.plan.core.UpgradeTaskCategory;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.sapphire.PossibleValuesService;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public class UpgradeTaskCategoryPossibleValuesService extends PossibleValuesService {

	public UpgradeTaskCategoryPossibleValuesService() {
		Bundle bundle = FrameworkUtil.getBundle(UpgradeTaskCategoryPossibleValuesService.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_upgradeTaskCategoryServiceTracker = new ServiceTracker<>(bundleContext, UpgradeTaskCategory.class, null);

		_upgradeTaskCategoryServiceTracker.open();
	}

	@Override
	public void dispose() {
		super.dispose();

		_upgradeTaskCategoryServiceTracker.close();
	}

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	protected void compute(Set<String> values) {
		values.addAll(_upgradeTaskCategoryValues);
	}

	@Override
	protected void initPossibleValuesService() {
		_upgradeTaskCategoryValues = Stream.of(
			_upgradeTaskCategoryServiceTracker.getServices(new UpgradeTaskCategory[0])
		).map(
			UpgradeTaskCategory::getId
		).collect(
			Collectors.toSet()
		);
	}

	private ServiceTracker<UpgradeTaskCategory, UpgradeTaskCategory> _upgradeTaskCategoryServiceTracker;
	private Set<String> _upgradeTaskCategoryValues;

}