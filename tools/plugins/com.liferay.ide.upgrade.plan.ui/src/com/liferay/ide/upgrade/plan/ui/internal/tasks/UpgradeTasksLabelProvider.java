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
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.ui.internal.UpgradePlanUIPlugin;

import java.net.URL;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeTasksLabelProvider extends LabelProvider implements IStyledLabelProvider {

	public UpgradeTasksLabelProvider() {
		_images = new HashMap<>();
	}

	@Override
	public void dispose() {
		Collection<Image> values = _images.values();

		Stream<Image> stream = values.stream();

		stream.forEach(Image::dispose);
	}

	@Override
	public Image getImage(Object element) {
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
		else if (element instanceof UpgradeTaskStep) {
			UpgradeTaskStep upgradeTaskStep = (UpgradeTaskStep)element;

			Bundle upgradeTaskStepBundle = FrameworkUtil.getBundle(upgradeTaskStep.getClass());

			return Optional.ofNullable(
				upgradeTaskStep.getImagePath()
			).map(
				upgradeTaskStepBundle::getEntry
			).filter(
				Objects::nonNull
			).map(
				this::_getImage
			).orElse(
				super.getImage(element)
			);
		}

		return super.getImage(element);
	}

	@Override
	public StyledString getStyledText(Object element) {
		if (UpgradeTasksContentProvider.NO_TASKS.equals(element)) {
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

	private Image _getImage(URL url) {
		return _images.computeIfAbsent(url, u -> {
			ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);

			return imageDescriptor.createImage();
		});
	}

	private final Map<URL, Image> _images;

}