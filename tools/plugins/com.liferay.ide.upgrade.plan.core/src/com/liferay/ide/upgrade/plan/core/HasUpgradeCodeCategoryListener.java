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

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.upgrade.plan.core.internal.NewUpgradePlanOpMethods;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Simon Jiang
 */
public class HasUpgradeCodeCategoryListener extends FilteredListener<PropertyContentEvent> {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		NewUpgradePlanOp op = op(event);

		Property eventProperty = event.property();
		ListProperty selectedUpgradeCategoriesPropety = NewUpgradePlanOp.PROP_SELECTED_UPGRADE_CATEGORIES;

		if (selectedUpgradeCategoriesPropety.equals(eventProperty.definition())) {
			ElementList<UpgradeCategoryElement> selectedCategories = op.getSelectedUpgradeCategories();

			for (UpgradeCategoryElement category : selectedCategories) {
				String cateogryValue = SapphireUtil.getContent(category.getUpgradeCategory());

				if (cateogryValue.equals(NewUpgradePlanOpMethods.upgradeCategoryCode)) {
					op.setHasCodeUpgradeCategory(true);

					return;
				}
			}
		}

		op.setHasCodeUpgradeCategory(false);
	}

	protected NewUpgradePlanOp op(PropertyContentEvent event) {
		Element element = SapphireUtil.getElement(event);

		return element.nearest(NewUpgradePlanOp.class);
	}

}