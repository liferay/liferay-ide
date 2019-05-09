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

import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanDetailsOp;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.ui.util.UIUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 */
public class UpgradePlanDetailsHandler extends AbstractHandler {

	public UpgradePlanDetailsHandler() {
		Bundle bundle = FrameworkUtil.getBundle(UpgradeStep.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		UpgradePlan upgradePlan = upgradePlanner.getCurrentUpgradePlan();

		if (upgradePlan == null) {
			UIUtil.postInfo("No Upgrade Plan", "There is no upgrade plan.");

			return null;
		}

		Element element = UpgradePlanDetailsOp.TYPE.instantiate();

		DefinitionLoader definitionLoader = DefinitionLoader.context(UpgradePlanDetailsHandler.class);

		definitionLoader = definitionLoader.sdef("UpgradePlanDetailsDialog");

		SapphireDialog sapphireDialog = new SapphireDialog(UIUtil.getActiveShell(), element, definitionLoader.dialog());

		return sapphireDialog.open();
	}

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

}