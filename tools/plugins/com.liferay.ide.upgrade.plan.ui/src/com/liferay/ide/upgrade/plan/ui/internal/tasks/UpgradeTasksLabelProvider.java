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

package com.liferay.ide.upgrade.plan.ui.internal.tasks;

import com.liferay.ide.upgrade.plan.core.UpgradeTask;
import com.liferay.ide.upgrade.plan.ui.UpgradePlanUIPlugin;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeTasksLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (UpgradeTasksContentProvider.NO_TASKS.equals(element)) {
			return UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.NO_TASKS_IMAGE);
		}
		else if (element instanceof UpgradeTask) {
			UpgradeTask upgradeTask = (UpgradeTask)element;

			String categoryId = upgradeTask.getCategoryId();

			if ("database".equals(categoryId)) {
				return UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.CATEGORY_DATABASE_IMAGE);
			}
			else if ("config".equals(categoryId)) {
				return UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.CATEGORY_CONFIG_IMAGE);
			}
			else if ("code".equals(categoryId)) {
				return UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.CATEGORY_CODE_IMAGE);
			}
		}

		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (UpgradeTasksContentProvider.NO_TASKS.equals(element)) {
			return "No upgrade tasks. Double-click to start a new upgrade plan.";
		}

		if (element instanceof UpgradeTask) {
			UpgradeTask upgradeTask = (UpgradeTask)element;

			return upgradeTask.getTitle();
		}

		return null;
	}

}