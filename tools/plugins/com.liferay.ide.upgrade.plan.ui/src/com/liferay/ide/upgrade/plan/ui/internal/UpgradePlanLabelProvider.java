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
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepRequirement;

import java.util.List;
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
 * @author Simon Jiang
 */
public class UpgradePlanLabelProvider extends BundleImageLabelProvider implements IStyledLabelProvider {

	public UpgradePlanLabelProvider() {
		super(_imagePathMapper());
	}

	@Override
	public Image getImage(Object element) {
		if (UpgradePlanContentProvider.NO_STEPS.equals(element)) {
			return UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.NO_STEPS_IMAGE);
		}

		return super.getImage(element);
	}

	@Override
	public StyledString getStyledText(Object element) {
		if (UpgradePlanContentProvider.NO_STEPS.equals(element)) {
			return new StyledString("No upgrade steps. Double-click to start a new upgrade plan.");
		}

		if (element instanceof UpgradeStep) {
			UpgradeStep upgradeStep = (UpgradeStep)element;

			Styler styler = null;

			if (upgradeStep.completed() || !upgradeStep.enabled()) {
				styler = StyledString.QUALIFIER_STYLER;
			}

			StyledString styledString = new StyledString(upgradeStep.getTitle(), styler);

			List<UpgradeStep> children = upgradeStep.getChildren();

			if (children.isEmpty()) {
				UpgradeStepRequirement upgradeStepRequirement = upgradeStep.getRequirement();

				styledString.append(" [" + upgradeStepRequirement.toString() + "]", StyledString.DECORATIONS_STYLER);
			}

			return styledString;
		}

		return null;
	}

	private static Function<Object, Pair<Bundle, String>> _imagePathMapper() {
		return element -> {
			Bundle bundle = null;

			String imagePath = null;

			if (element instanceof UpgradeStep) {
				UpgradeStep upgradeStep = (UpgradeStep)element;

				imagePath = upgradeStep.getIcon();

				if (imagePath != null) {
					bundle = FrameworkUtil.getBundle(element.getClass());
				}
			}

			if ((bundle != null) && (imagePath != null)) {
				return new Pair<>(bundle, imagePath);
			}

			return null;
		};
	}

}