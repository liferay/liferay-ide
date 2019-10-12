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

import com.liferay.ide.upgrade.plan.core.IUpgradePlanOutline;
import com.liferay.ide.upgrade.plan.core.UpgradePlanCorePlugin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.sapphire.services.ValueLabelService;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class OutlineValueLabelService extends ValueLabelService {

	@Override
	public String provide(String value) {
		List<IUpgradePlanOutline> outlines = new CopyOnWriteArrayList<>();

		outlines.addAll(UpgradePlanCorePlugin.getOutlines(UpgradePlanCorePlugin.CUSTOMER_OUTLINE_KEY));
		outlines.addAll(UpgradePlanCorePlugin.getOutlines(UpgradePlanCorePlugin.OFFLINE_OUTLINE_KEY));
		outlines.addAll(UpgradePlanCorePlugin.getOutlines(UpgradePlanCorePlugin.DEFAULT_OUTLINE_KEY));

		IUpgradePlanOutline outline = UpgradePlanCorePlugin.getFilterOutlines(value);

		if (outline != null) {
			if (outline.isOffline()) {
				return outline.getName() + "(Offline)";
			}

			return outline.getName();
		}

		return value;
	}

}