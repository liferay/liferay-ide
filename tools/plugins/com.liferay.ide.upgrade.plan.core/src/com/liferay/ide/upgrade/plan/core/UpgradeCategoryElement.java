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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 */
public interface UpgradeCategoryElement extends Element {

	public Value<String> getUpgradeCategory();

	public void setUpgradeCategory(String value);

	@Label(standard = "Upgrade Category")
	@Service(context = Service.Context.METAMODEL, impl = UpgradeCategoryValueImageService.class)
	@Service(context = Service.Context.METAMODEL, impl = UpgradeCategoryValueLabelService.class)
	public ValueProperty PROP_UPGRADE_CATEGORY = new ValueProperty(
		new ElementType(UpgradeCategoryElement.class), "UpgradeCategory");

	public ElementType TYPE = new ElementType(UpgradeCategoryElement.class);

}