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

import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.upgrade.plan.core.NewUpgradePlanOp;

import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;

/**
 * @author Simon Jiang
 */
public class OutlineValueServiceConditon extends ServiceCondition {

	@Override
	public boolean applicable(final ServiceContext context) {
		ValueProperty prop = context.find(ValueProperty.class);

		ValueProperty property = NewUpgradePlanOp.PROP_UPGRADE_PLAN_OUTLINE;

		if ((prop != null) && StringUtil.equals(prop.name(), property.name())) {
			return true;
		}

		return false;
	}

	public ValueProperty getOutline() {
		return NewUpgradePlanOp.PROP_UPGRADE_PLAN_OUTLINE;
	}

}