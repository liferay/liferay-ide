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

import com.liferay.ide.upgrade.plan.core.UpgradePlanElement;
import com.liferay.ide.upgrade.plan.core.UpgradeTask;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.ui.internal.UpgradePlanUIPlugin;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradePlanLabelProvider extends BundleImageLabelProvider implements IStyledLabelProvider {

	public UpgradePlanLabelProvider() {
		super(element -> {
			if (element instanceof UpgradePlanElement) {
				UpgradePlanElement upgradePlanElement = (UpgradePlanElement)element;

				return upgradePlanElement.getImagePath();
			}

			return null;
		});
	}

	@Override
	public Image getImage(Object element) {
		if (UpgradePlanContentProvider.NO_TASKS.equals(element)) {
			return UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.NO_TASKS_IMAGE);
		}

		return super.getImage(element);
	}

	@Override
	public StyledString getStyledText(Object element) {
		if (UpgradePlanContentProvider.NO_TASKS.equals(element)) {
			return new StyledString("No upgrade tasks. Double-click to start a new upgrade plan.");
		}

		if (element instanceof UpgradeTask) {
			UpgradeTask upgradeTask = (UpgradeTask)element;

			return new StyledString(upgradeTask.getTitle());
		}
		else if (element instanceof UpgradeTaskStep) {
			UpgradeTaskStep upgradeTaskStep = (UpgradeTaskStep)element;

			return new StyledString(upgradeTaskStep.getTitle());
		}

		return null;
	}

}