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

import com.liferay.ide.upgrade.plan.core.internal.NewUpgradePlanOpMethods;

import org.eclipse.sapphire.services.ValueLabelService;

/**
 * @author Simon Jiang
 */
public final class UpgradeCategoryValueLabelService extends ValueLabelService {

	@Override
	public String provide(final String value) {
		if (value != null) {
			if (value.equalsIgnoreCase(NewUpgradePlanOpMethods.upgradeCategoryCode)) {
				return "Code Upgrade";
			}
			else if (value.equalsIgnoreCase(NewUpgradePlanOpMethods.upgradeCategoryDatabase)) {
				return "Database Upgrade";
			}
			else if (value.equalsIgnoreCase(NewUpgradePlanOpMethods.upgradeCategoryConfig)) {
				return "Configuration Upgrade";
			}
		}

		return value;
	}

}