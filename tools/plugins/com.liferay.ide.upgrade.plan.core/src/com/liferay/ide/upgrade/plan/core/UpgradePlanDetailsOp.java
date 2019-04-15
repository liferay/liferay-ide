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

import com.liferay.ide.upgrade.plan.core.internal.UpgradePlanCurrentLocationDefaultValueService;
import com.liferay.ide.upgrade.plan.core.internal.UpgradePlanCurrentVersionDefaultValueService;
import com.liferay.ide.upgrade.plan.core.internal.UpgradePlanDetailsOpMethods;
import com.liferay.ide.upgrade.plan.core.internal.UpgradePlanNameDefaultValueService;
import com.liferay.ide.upgrade.plan.core.internal.UpgradePlanProblemCountDefaultValueService;
import com.liferay.ide.upgrade.plan.core.internal.UpgradePlanTargetLocationDefaultValueService;
import com.liferay.ide.upgrade.plan.core.internal.UpgradePlanTargetVersionDefaultValueService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Terry Jia
 */
public interface UpgradePlanDetailsOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(UpgradePlanDetailsOp.class);

	@DelegateImplementation(UpgradePlanDetailsOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	@Label(standard = "Current Liferay Version")
	@Service(impl = UpgradePlanCurrentVersionDefaultValueService.class)
	public ValueProperty PROP_CURRENT_VERSION = new ValueProperty(TYPE, "CurrentVersion");

	@Label(standard = "Current Code Location")
	@Service(impl = UpgradePlanCurrentLocationDefaultValueService.class)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, "Location");

	@Label(standard = "Name")
	@Service(impl = UpgradePlanNameDefaultValueService.class)
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@Label(standard = "Upgrade Problem Count")
	@Service(impl = UpgradePlanProblemCountDefaultValueService.class)
	public ValueProperty PROP_PROBLEM_COUNT = new ValueProperty(TYPE, "ProblemCount");

	@Label(standard = "Target Code Location")
	@Service(impl = UpgradePlanTargetLocationDefaultValueService.class)
	public ValueProperty PROP_TARGET_LOCATION = new ValueProperty(TYPE, "TargetLocation");

	@Label(standard = "Target Liferay Version")
	@Service(impl = UpgradePlanTargetVersionDefaultValueService.class)
	public ValueProperty PROP_TARGET_VERSION = new ValueProperty(TYPE, "TargetVersion");

}