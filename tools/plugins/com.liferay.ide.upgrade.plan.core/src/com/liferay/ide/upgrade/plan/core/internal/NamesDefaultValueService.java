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

package com.liferay.ide.upgrade.plan.core.internal;

import com.liferay.ide.upgrade.plan.core.IMemento;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DefaultValueService;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 */
public class NamesDefaultValueService extends DefaultValueService {

	public NamesDefaultValueService() {
		Bundle bundle = FrameworkUtil.getBundle(NamesDefaultValueService.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();
	}

	@Override
	protected String compute() {
		String retval = "";

		UpgradePlanCorePlugin upgradePlanCorePlugin = UpgradePlanCorePlugin.getInstance();

		IPath stateLocation = upgradePlanCorePlugin.getStateLocation();

		IPath xmlFile = stateLocation.append("upgradePlanner.xml");

		File file = xmlFile.toFile();

		if (file.exists()) {
			try (InputStream inputStream = new FileInputStream(file)) {
				IMemento rootMemento = XMLMemento.loadMemento(inputStream);

				if (rootMemento != null) {
					UpgradePlanner upgradePlanner = _serviceTracker.getService();

					UpgradePlan upgradePlan = upgradePlanner.getCurrentUpgradePlan();

					List<String> names = Stream.of(
						rootMemento.getChildren("upgradePlan")
					).map(
						memento -> memento.getString("upgradePlanName")
					).filter(
						name -> !name.equals(upgradePlan.getName())
					).collect(
						Collectors.toList()
					);

					if (!names.isEmpty()) {
						retval = names.get(0);
					}
				}
			}
			catch (IOException ioe) {
			}
		}

		return retval;
	}

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

}