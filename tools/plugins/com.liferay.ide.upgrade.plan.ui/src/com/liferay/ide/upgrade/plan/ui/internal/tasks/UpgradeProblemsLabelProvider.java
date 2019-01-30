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

import com.liferay.ide.ui.navigator.AbstractLabelProvider;
import com.liferay.ide.upgrade.plan.core.FileProblems;
import com.liferay.ide.upgrade.plan.core.MigrationProblemsContainer;
import com.liferay.ide.upgrade.plan.core.Problem;
import com.liferay.ide.upgrade.plan.core.ProjectProblems;
import com.liferay.ide.upgrade.plan.ui.UpgradeInfoProvider;
import com.liferay.ide.upgrade.plan.ui.internal.UpgradePlanUIPlugin;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeProblemsLabelProvider extends AbstractLabelProvider {

	public UpgradeProblemsLabelProvider() {
		Bundle bundle = FrameworkUtil.getBundle(UpgradeProblemsLabelProvider.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_upgradeInfoProviderServiceTracker = new ServiceTracker<>(bundleContext, UpgradeInfoProvider.class, null);

		_upgradeInfoProviderServiceTracker.open();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof ProjectProblems) {
			return getImageRegistry().get("ProjectMigrationProblems");
		}
		else if (element instanceof MigrationProblemsContainer) {
			return getImageRegistry().get("MigrationProblemsContainer");
		}
		else if (element instanceof FileProblems) {
			return getImageRegistry().get("FileProblems");
		}
		else if (element instanceof Problem) {
			return getImageRegistry().get("Problem");
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		UpgradeInfoProvider upgradeInfoProvider = _upgradeInfoProviderServiceTracker.getService();

		return upgradeInfoProvider.getLabel(element);
	}

	@Override
	protected void initalizeImageRegistry(ImageRegistry imageRegistry) {
		imageRegistry.put(
			"MigrationProblemsContainer",
			UpgradePlanUIPlugin.imageDescriptorFromPlugin(UpgradePlanUIPlugin.PLUGIN_ID, "icons/liferay_logo_16.png"));
		imageRegistry.put(
			"ProjectMigrationProblems",
			UpgradePlanUIPlugin.imageDescriptorFromPlugin(UpgradePlanUIPlugin.PLUGIN_ID, ISharedImages.IMG_OBJ_FOLDER));
		imageRegistry.put(
			"FileProblems",
			UpgradePlanUIPlugin.imageDescriptorFromPlugin(UpgradePlanUIPlugin.PLUGIN_ID, ISharedImages.IMG_OBJ_FILE));
		imageRegistry.put(
			"Problem",
			UpgradePlanUIPlugin.imageDescriptorFromPlugin(
				UpgradePlanUIPlugin.PLUGIN_ID, ISharedImages.IMG_OBJS_ERROR_TSK));
	}

	private ServiceTracker<UpgradeInfoProvider, UpgradeInfoProvider> _upgradeInfoProviderServiceTracker;

}