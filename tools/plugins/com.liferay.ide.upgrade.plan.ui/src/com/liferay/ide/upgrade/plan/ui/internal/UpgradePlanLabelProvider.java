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

package com.liferay.ide.upgrade.plan.ui.internal;

import com.liferay.ide.upgrade.plan.core.Pair;
import com.liferay.ide.upgrade.plan.core.UpgradePlanElement;
import com.liferay.ide.upgrade.plan.core.UpgradeTask;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskCategory;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepRequirement;
import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;

import java.util.function.Function;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Image;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradePlanLabelProvider extends BundleImageLabelProvider implements IStyledLabelProvider {

	public UpgradePlanLabelProvider() {
		super(_imagePathMapper());
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

			Styler styler = null;

			if (upgradeTaskStep.completed() || !upgradeTaskStep.enabled()) {
				styler = StyledString.QUALIFIER_STYLER;
			}

			StyledString styledString = new StyledString(upgradeTaskStep.getTitle(), styler);

			UpgradeTaskStepRequirement upgradeTaskStepRequirement = upgradeTaskStep.getRequirement();

			styledString.append(" [" + upgradeTaskStepRequirement.toString() + "]", StyledString.DECORATIONS_STYLER);

			return styledString;
		}

		return null;
	}

	private static Function<Object, Pair<Bundle, String>> _imagePathMapper() {
		return element -> {
			Bundle bundle = null;

			String imagePath = null;

			if (element instanceof UpgradePlanElement) {
				UpgradePlanElement upgradePlanElement = (UpgradePlanElement)element;

				imagePath = upgradePlanElement.getImagePath();

				if (imagePath != null) {
					bundle = FrameworkUtil.getBundle(element.getClass());
				}
			}

			if ((imagePath == null) && (element instanceof UpgradeTask)) {
				UpgradeTask upgradeTask = (UpgradeTask)element;

				UpgradeTaskCategory upgradeTaskCategory = ServicesLookup.getSingleService(
					UpgradeTaskCategory.class, "(id=" + upgradeTask.getCategoryId() + ")");

				imagePath = upgradeTaskCategory.getImagePath();

				bundle = FrameworkUtil.getBundle(upgradeTaskCategory.getClass());
			}

			if ((imagePath == null) && (element instanceof UpgradeTaskStep)) {
				UpgradeTaskStep upgradeTaskStep = (UpgradeTaskStep)element;

				UpgradeTask upgradeTask = ServicesLookup.getSingleService(
					UpgradeTask.class, "(id=" + upgradeTaskStep.getTaskId() + ")");

				UpgradeTaskCategory upgradeTaskCategory = ServicesLookup.getSingleService(
					UpgradeTaskCategory.class, "(id=" + upgradeTask.getCategoryId() + ")");

				imagePath = upgradeTaskCategory.getImagePath();

				bundle = FrameworkUtil.getBundle(upgradeTaskCategory.getClass());
			}

			if ((bundle != null) && (imagePath != null)) {
				return new Pair<>(bundle, imagePath);
			}

			return null;
		};
	}

}