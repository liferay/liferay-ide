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

import com.liferay.ide.upgrade.plan.core.UpgradePlanAcessor;
import com.liferay.ide.upgrade.plan.core.UpgradeStepCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.sapphire.services.ValueLabelService;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public class UpgradeStepCategoryValueLabelService extends ValueLabelService implements UpgradePlanAcessor {

	@Override
	public String provide(String value) {
		return _labelMap.computeIfAbsent(
			value,
			v -> {
				return Optional.ofNullable(
					getCategory(value)
				).filter(
					Objects::nonNull
				).map(
					UpgradeStepCategory::getTitle
				).orElse(
					null
				);
			});
	}

	private static Map<String, String> _labelMap = new HashMap<>();

}