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

import com.liferay.ide.upgrade.plan.core.internal.NewUpgradePlanOpMethods;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Required;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public interface NewUpgradePlanOp extends ExecutableElement {

	@DelegateImplementation(NewUpgradePlanOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getCurrentVersion();

	public Value<String> getName();

	public Value<String> getTargetVersion();

	public void setCurrentVersion(String currentVersion);

	public void setName(String name);

	public void setTargetVersion(String targetVersion);

	@PossibleValues(values = {"6.2", "7.0"})
	public ValueProperty PROP_CURRENT_VERSION = new ValueProperty(TYPE, "CurrentVersion");

	@Required
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@PossibleValues(values = {"7.0", "7.1"})
	public ValueProperty PROP_TARGET_VERSION = new ValueProperty(TYPE, "TargetVersion");

	public ElementType TYPE = new ElementType(NewUpgradePlanOp.class);

}