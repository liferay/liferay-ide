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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sapphire.services.ValueLabelService;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public class UpgradeCategoryValueLabelService extends ValueLabelService {

	@Override
	public String provide(String value) {
		String label = _labelMap.get(value);

		if (label == null) {
			Bundle bundle = FrameworkUtil.getBundle(UpgradeCategoryPossibleValuesService.class);

			BundleContext bundleContext = bundle.getBundleContext();

			try {
				List<ServiceReference<UpgradeTaskCategory>> serviceReferences = new ArrayList<>(
					bundleContext.getServiceReferences(UpgradeTaskCategory.class, "(id=" + value + ")"));

				if (!serviceReferences.isEmpty()) {
					UpgradeTaskCategory upgradeTaskCategory = bundleContext.getService(serviceReferences.get(0));

					label = upgradeTaskCategory.getTitle();

					_labelMap.put(value, label);
				}
			}
			catch (InvalidSyntaxException ise) {
			}
		}

		return label;
	}

	private static Map<String, String> _labelMap = new HashMap<>();

}