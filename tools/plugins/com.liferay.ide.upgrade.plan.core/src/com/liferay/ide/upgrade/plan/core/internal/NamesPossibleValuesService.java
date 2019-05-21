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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.upgrade.plan.core.IMemento;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanCorePlugin;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.PossibleValuesService;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 */
public class NamesPossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	public NamesPossibleValuesService() {
		Bundle bundle = FrameworkUtil.getBundle(NamesPossibleValuesService.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();
	}

	@Override
	protected void compute(Set<String> values) {
		UpgradePlanCorePlugin upgradePlanCorePlugin = UpgradePlanCorePlugin.getInstance();

		IPath stateLocation = upgradePlanCorePlugin.getStateLocation();

		IPath xmlFile = stateLocation.append("upgradePlanner.xml");

		File file = xmlFile.toFile();

		if (!file.exists()) {
			return;
		}

		try (InputStream inputStream = new FileInputStream(file)) {
			IMemento rootMemento = XMLMemento.loadMemento(inputStream);

			if (rootMemento == null) {
				return;
			}

			UpgradePlanner upgradePlanner = _serviceTracker.getService();

			UpgradePlan upgradePlan = upgradePlanner.getCurrentUpgradePlan();

			Set<String> names = Stream.of(
				rootMemento.getChildren("upgradePlan")
			).map(
				memento -> memento.getString("upgradePlanName")
			).filter(
				name -> !name.equals(upgradePlan.getName())
			).collect(
				Collectors.toSet()
			);

			values.addAll(names);
		}
		catch (IOException ioe) {
		}
	}

	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

}