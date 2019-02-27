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

import com.liferay.ide.upgrade.plan.core.internal.UpgradeTaskCategoryValueImageService;
import com.liferay.ide.upgrade.plan.core.internal.UpgradeTaskCategoryValueLabelService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 */
public interface UpgradeTaskCategoryElement extends Element {

	public Value<String> getUpgradeTaskCategory();

	public void setUpgradeTaskCategory(String value);

	@Label(standard = "Upgrade Task Category")
	@Service(context = Service.Context.METAMODEL, impl = UpgradeTaskCategoryValueImageService.class)
	@Service(context = Service.Context.METAMODEL, impl = UpgradeTaskCategoryValueLabelService.class)
	public ValueProperty PROP_UPGRADE_TASK_CATEGORY = new ValueProperty(
		new ElementType(UpgradeTaskCategoryElement.class), "UpgradeTaskCategory");

	public ElementType TYPE = new ElementType(UpgradeTaskCategoryElement.class);

}