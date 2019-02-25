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

package com.liferay.ide.upgrade.problems.ui.internal;

import com.liferay.ide.ui.navigator.AbstractLabelProvider;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.ui.UpgradeInfoProvider;

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
	public void dispose() {
		super.dispose();

		_upgradeInfoProviderServiceTracker.close();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof ProjectProblemsContainer) {
			return getImageRegistry().get("ProjectMigrationProblems");
		}
		else if (element instanceof MigrationProblemsContainer) {
			return getImageRegistry().get("MigrationProblemsContainer");
		}
		else if (element instanceof FileProblemsContainer) {
			return getImageRegistry().get("FileProblems");
		}
		else if (element instanceof UpgradeProblem) {
			return getImageRegistry().get("Problem");
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		Object[] services = _upgradeInfoProviderServiceTracker.getServices();

		for (Object service : services) {
			if (service instanceof UpgradeProblemsInfoProviderService) {
				UpgradeInfoProvider upgradeInfoProvider = (UpgradeProblemsInfoProviderService)service;

				return upgradeInfoProvider.getLabel(element);
			}
		}

		return null;
	}

	@Override
	protected void initalizeImageRegistry(ImageRegistry imageRegistry) {
		imageRegistry.put(
			"MigrationProblemsContainer",
			UpgradeProblemsUIPlugin.imageDescriptorFromPlugin(
				UpgradeProblemsUIPlugin.PLUGIN_ID, "icons/liferay_logo_16.png"));
		imageRegistry.put(
			"ProjectMigrationProblems",
			UpgradeProblemsUIPlugin.imageDescriptorFromPlugin(
				UpgradeProblemsUIPlugin.PLUGIN_ID, ISharedImages.IMG_OBJ_FOLDER));
		imageRegistry.put(
			"FileProblems",
			UpgradeProblemsUIPlugin.imageDescriptorFromPlugin(
				UpgradeProblemsUIPlugin.PLUGIN_ID, ISharedImages.IMG_OBJ_FILE));
		imageRegistry.put(
			"Problem",
			UpgradeProblemsUIPlugin.imageDescriptorFromPlugin(
				UpgradeProblemsUIPlugin.PLUGIN_ID, ISharedImages.IMG_OBJS_ERROR_TSK));
	}

	private ServiceTracker<UpgradeInfoProvider, UpgradeInfoProvider> _upgradeInfoProviderServiceTracker;

}