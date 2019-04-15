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

package com.liferay.ide.upgrade.plan.core;

import com.liferay.ide.upgrade.plan.core.internal.UpgradePlanDetailsOpMethods;

import java.nio.file.Path;

import java.util.Collection;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 */
public interface UpgradePlanDetailsOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(UpgradePlanDetailsOp.class);

	@DelegateImplementation(UpgradePlanDetailsOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	@Label(standard = "Current Liferay Version")
	@Service(impl = UpgradePlanDefaultValueService.class)
	public ValueProperty PROP_CURRENT_VERSION = new ValueProperty(TYPE, "CurrentVersion");

	@Label(standard = "Current Code Location")
	@Service(impl = UpgradePlanDefaultValueService.class)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, "Location");

	@Label(standard = "Name")
	@Service(impl = UpgradePlanDefaultValueService.class)
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@Label(standard = "Upgrade Problem Count")
	@Service(impl = UpgradePlanDefaultValueService.class)
	public ValueProperty PROP_PROBLEM_COUNT = new ValueProperty(TYPE, "ProblemCount");

	@Label(standard = "Target Code Location")
	@Service(impl = UpgradePlanDefaultValueService.class)
	public ValueProperty PROP_TARGET_LOCATION = new ValueProperty(TYPE, "TargetLocation");

	@Label(standard = "Target Liferay Version")
	@Service(impl = UpgradePlanDefaultValueService.class)
	public ValueProperty PROP_TARGET_VERSION = new ValueProperty(TYPE, "TargetVersion");

	public class UpgradePlanDefaultValueService extends DefaultValueService {

		public UpgradePlanDefaultValueService() {
			Bundle bundle = FrameworkUtil.getBundle(UpgradePlanDetailsOp.class);

			BundleContext bundleContext = bundle.getBundleContext();

			_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

			_serviceTracker.open();
		}

		@Override
		public void dispose() {
			_serviceTracker.close();
		}

		@Override
		protected String compute() {
			return _defaultValue;
		}

		@Override
		protected void initDefaultValueService() {
			super.initDefaultValueService();

			UpgradePlanner upgradePlanner = _serviceTracker.getService();

			UpgradePlan upgradePlan = upgradePlanner.getCurrentUpgradePlan();

			ValueProperty valueProperty = context(ValueProperty.class);

			if (valueProperty.equals(UpgradePlanDetailsOp.PROP_CURRENT_VERSION)) {
				_defaultValue = upgradePlan.getCurrentVersion();
			}
			else if (valueProperty.equals(UpgradePlanDetailsOp.PROP_LOCATION)) {
				Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

				if (currentProjectLocation == null) {
					_defaultValue = "";
				}
				else {
					_defaultValue = currentProjectLocation.toString();
				}
			}
			else if (valueProperty.equals(UpgradePlanDetailsOp.PROP_NAME)) {
				_defaultValue = upgradePlan.getName();
			}
			else if (valueProperty.equals(UpgradePlanDetailsOp.PROP_PROBLEM_COUNT)) {
				Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

				_defaultValue = String.valueOf(upgradeProblems.size());
			}
			else if (valueProperty.equals(UpgradePlanDetailsOp.PROP_TARGET_LOCATION)) {
				Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

				if (targetProjectLocation == null) {
					_defaultValue = "";
				}
				else {
					_defaultValue = targetProjectLocation.toString();
				}
			}
			else if (valueProperty.equals(UpgradePlanDetailsOp.PROP_TARGET_VERSION)) {
				_defaultValue = upgradePlan.getTargetVersion();
			}
		}

		private String _defaultValue;
		private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

	}

}