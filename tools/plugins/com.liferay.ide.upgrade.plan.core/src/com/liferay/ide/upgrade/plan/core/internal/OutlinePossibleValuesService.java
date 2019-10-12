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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.upgrade.plan.core.IUpgradePlanOutline;
import com.liferay.ide.upgrade.plan.core.UpgradePlanCorePlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class OutlinePossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	@Override
	protected void compute(Set<String> values) {
		values.addAll(_possibleValues);
	}

	@Override
	protected void initPossibleValuesService() {
		_possibleValues = new ArrayList<>();

		List<IUpgradePlanOutline> customerOutlines = UpgradePlanCorePlugin.getOutlines(
			UpgradePlanCorePlugin.CUSTOMER_OUTLINE_KEY);

		for (IUpgradePlanOutline outline : customerOutlines) {
			_possibleValues.add(outline.getName());
		}

		List<IUpgradePlanOutline> offlineOutlines = UpgradePlanCorePlugin.getOutlines(
			UpgradePlanCorePlugin.OFFLINE_OUTLINE_KEY);

		for (IUpgradePlanOutline outline : offlineOutlines) {
			_possibleValues.add(outline.getName());
		}

		List<IUpgradePlanOutline> defaultOutlines = UpgradePlanCorePlugin.getOutlines(
			UpgradePlanCorePlugin.DEFAULT_OUTLINE_KEY);

		for (IUpgradePlanOutline outline : defaultOutlines) {
			_possibleValues.add(outline.getName());
		}

		Collections.sort(_possibleValues);
	}

	private List<String> _possibleValues;

}