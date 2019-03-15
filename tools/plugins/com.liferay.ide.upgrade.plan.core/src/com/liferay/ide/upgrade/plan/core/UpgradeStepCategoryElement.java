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

import com.liferay.ide.upgrade.plan.core.internal.UpgradeStepCategoryValueImageService;
import com.liferay.ide.upgrade.plan.core.internal.UpgradeStepCategoryValueLabelService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 */
public interface UpgradeStepCategoryElement extends Element {

	public Value<String> getUpgradeStepCategory();

	public void setUpgradeStepCategory(String value);

	@Label(standard = "Upgrade Step Category")
	@Service(context = Service.Context.METAMODEL, impl = UpgradeStepCategoryValueImageService.class)
	@Service(context = Service.Context.METAMODEL, impl = UpgradeStepCategoryValueLabelService.class)
	public ValueProperty PROP_UPGRADE_STEP_CATEGORY = new ValueProperty(
		new ElementType(UpgradeStepCategoryElement.class), "UpgradeStepCategory");

	public ElementType TYPE = new ElementType(UpgradeStepCategoryElement.class);

}