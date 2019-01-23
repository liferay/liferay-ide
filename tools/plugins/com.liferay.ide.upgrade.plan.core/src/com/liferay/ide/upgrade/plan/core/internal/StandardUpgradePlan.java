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

import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradeTask;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskCategory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 */
public class StandardUpgradePlan implements UpgradePlan {

	public StandardUpgradePlan(String name) {
		_name = name;

		Bundle bundle = FrameworkUtil.getBundle(StandardUpgradePlan.class);

		BundleContext bundleContext = bundle.getBundleContext();

		List<UpgradeTask> upgradeTasks = null;

		try {
			List<UpgradeTaskCategory> orderedUpgradeTaskCategories = _orderedServices(
				bundleContext, bundleContext.getServiceReferences(UpgradeTaskCategory.class, null));

			Stream<UpgradeTaskCategory> stream = orderedUpgradeTaskCategories.stream();

			upgradeTasks = stream.flatMap(
				upgradeTaskCategory -> {
					try {
						List<UpgradeTask> orderedUpgradeTasks = _orderedServices(
							bundleContext,
							bundleContext.getServiceReferences(
								UpgradeTask.class, "(categoryId=" + upgradeTaskCategory.getId() + ")"));

						return orderedUpgradeTasks.stream();
					}
					catch (InvalidSyntaxException ise) {
						return null;
					}
				}
			).collect(
				Collectors.toList()
			);
		}
		catch (InvalidSyntaxException ise) {
			upgradeTasks = Collections.emptyList();
		}

		_upgradeTasks = upgradeTasks;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public List<UpgradeTask> getTasks() {
		return Collections.unmodifiableList(_upgradeTasks);
	}

	private <T> List<T> _orderedServices(
		BundleContext bundleContext, Collection<ServiceReference<T>> serviceReferenceCollection) {

		List<ServiceReference<T>> serviceReferenceList = new ArrayList<>(serviceReferenceCollection);

		Collections.sort(
			serviceReferenceList,
			(srLeft, srRight) -> {
				try {
					Dictionary<String, Object> srLeftProperties = srLeft.getProperties();

					Object srLeftOrder = srLeftProperties.get("order");

					try {
						int srLeftInt = Integer.parseInt(srLeftOrder.toString());

						Dictionary<String, Object> srRightProperties = srRight.getProperties();

						Object srRightOrder = srRightProperties.get("order");

						int srRightInt = Integer.parseInt(srRightOrder.toString());

						return Integer.compare(srLeftInt, srRightInt);
					}
					catch (NumberFormatException nfe) {
					}
				}
				catch (Throwable t) {
				}

				return -1;
			});

		Stream<ServiceReference<T>> stream = serviceReferenceList.stream();

		return stream.map(
			bundleContext::getService
		).collect(
			Collectors.toList()
		);
	}

	private final String _name;
	private final List<UpgradeTask> _upgradeTasks;

}