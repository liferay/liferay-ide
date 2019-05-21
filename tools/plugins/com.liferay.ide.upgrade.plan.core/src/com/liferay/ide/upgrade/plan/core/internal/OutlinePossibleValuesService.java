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
import com.liferay.ide.upgrade.plan.core.UpgradePlanCorePlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Terry Jia
 */
public class OutlinePossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	@Override
	protected void compute(Set<String> values) {
		IPreferencesService preferencesService = Platform.getPreferencesService();

		String outlines = preferencesService.getString(UpgradePlanCorePlugin.ID, "outlines", "", null);

		List<String> outlineList = UpgradePlanCorePlugin.defaultUpgradePlanOutlines;

		if (!"".equals(outlines)) {
			outlineList = Arrays.asList(outlines.split(","));
		}

		values.addAll(outlineList);
	}

}