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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.InitialValueService;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public interface UpgradePlanDetailsOp extends Element {

	public ElementType TYPE = new ElementType(UpgradePlanDetailsOp.class);

	@Enablement(expr = "false")
	@Label(standard = "Completed Step Count")
	@Service(impl = UpgradePlanInitialValueService.class)
	public ValueProperty PROP_COMPLETED_STEP_COUNT = new ValueProperty(TYPE, "CompletedStepCount");

	@Enablement(expr = "false")
	@Label(standard = "Current Liferay Version")
	@Service(impl = UpgradePlanInitialValueService.class)
	public ValueProperty PROP_CURRENT_VERSION = new ValueProperty(TYPE, "CurrentVersion");

	@Enablement(expr = "false")
	@Label(standard = "Current Code Location")
	@Service(impl = UpgradePlanInitialValueService.class)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, "Location");

	@Enablement(expr = "false")
	@Label(standard = "Name")
	@Service(impl = UpgradePlanInitialValueService.class)
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@Enablement(expr = "false")
	@Label(standard = "Upgrade Problem Count")
	@Service(impl = UpgradePlanInitialValueService.class)
	public ValueProperty PROP_PROBLEM_COUNT = new ValueProperty(TYPE, "ProblemCount");

	@Enablement(expr = "false")
	@Label(standard = "Target Code Location")
	@Service(impl = UpgradePlanInitialValueService.class)
	public ValueProperty PROP_TARGET_LOCATION = new ValueProperty(TYPE, "TargetLocation");

	@Enablement(expr = "false")
	@Label(standard = "Target Liferay Version")
	@Service(impl = UpgradePlanInitialValueService.class)
	public ValueProperty PROP_TARGET_VERSION = new ValueProperty(TYPE, "TargetVersion");

	@Enablement(expr = "false")
	@Label(standard = "Total Step Count")
	@Service(impl = UpgradePlanInitialValueService.class)
	public ValueProperty PROP_TOTAL_STEP_COUNT = new ValueProperty(TYPE, "TotalStepCount");

	@Service(impl = UpgradePlanInitialValueService.class)
	public ValueProperty PROP_UPGRADE_PLAN_OUTLINE = new ValueProperty(TYPE, "UpgradePlanOutline");

	public class UpgradePlanInitialValueService extends InitialValueService {

		public UpgradePlanInitialValueService() {
			Bundle bundle = FrameworkUtil.getBundle(UpgradePlanDetailsOp.class);

			_serviceTracker = new ServiceTracker<>(bundle.getBundleContext(), UpgradePlanner.class, null);

			_serviceTracker.open();
		}

		@Override
		public void dispose() {
			_serviceTracker.close();
		}

		@Override
		protected String compute() {
			return _initialValue;
		}

		@Override
		protected void initInitialValueService() {
			super.initInitialValueService();

			UpgradePlanner upgradePlanner = _serviceTracker.getService();

			UpgradePlan upgradePlan = upgradePlanner.getCurrentUpgradePlan();

			ValueProperty valueProperty = context(ValueProperty.class);

			if (valueProperty.equals(UpgradePlanDetailsOp.PROP_CURRENT_VERSION)) {
				_initialValue = upgradePlan.getCurrentVersion();
			}
			else if (valueProperty.equals(UpgradePlanDetailsOp.PROP_NAME)) {
				_initialValue = upgradePlan.getName();
			}
			else if (valueProperty.equals(UpgradePlanDetailsOp.PROP_PROBLEM_COUNT)) {
				Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

				_initialValue = String.valueOf(upgradeProblems.size());
			}
			else if (valueProperty.equals(UpgradePlanDetailsOp.PROP_TARGET_VERSION)) {
				_initialValue = upgradePlan.getTargetVersion();
			}
			else if (valueProperty.equals(UpgradePlanDetailsOp.PROP_UPGRADE_PLAN_OUTLINE)) {
				IUpgradePlanOutline upgradePlanOutline = upgradePlan.getUpgradePlanOutline();

				_initialValue = upgradePlanOutline.getName();
			}
			else if (valueProperty.equals(UpgradePlanDetailsOp.PROP_TOTAL_STEP_COUNT)) {
				List<UpgradeStep> allUpgradeSteps = new ArrayList<>();

				_computeUpgradeSteps(upgradePlan.getUpgradeSteps(), allUpgradeSteps, false);

				_initialValue = String.valueOf(allUpgradeSteps.size());
			}
			else if (valueProperty.equals(UpgradePlanDetailsOp.PROP_COMPLETED_STEP_COUNT)) {
				List<UpgradeStep> allUpgradeSteps = new ArrayList<>();

				_computeUpgradeSteps(upgradePlan.getUpgradeSteps(), allUpgradeSteps, true);

				_initialValue = String.valueOf(allUpgradeSteps.size());
			}
		}

		private void _computeUpgradeSteps(
			List<UpgradeStep> upgradeSteps, List<UpgradeStep> allUpgradeSteps, boolean statusCompleted) {

			if (statusCompleted) {
				Stream<UpgradeStep> upgradeStepsStream = upgradeSteps.stream();

				allUpgradeSteps.addAll(
					upgradeStepsStream.filter(
						UpgradeStep::completed
					).collect(
						Collectors.toList()
					));
			}
			else {
				allUpgradeSteps.addAll(upgradeSteps);
			}

			for (UpgradeStep upgradeStep : upgradeSteps) {
				List<UpgradeStep> children = upgradeStep.getChildren();

				if (!children.isEmpty()) {
					_computeUpgradeSteps(children, allUpgradeSteps, statusCompleted);
				}
			}
		}

		private String _initialValue;
		private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

	}

}