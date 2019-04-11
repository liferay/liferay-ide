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

import com.liferay.ide.upgrade.plan.core.internal.NamesDefaultValueService;
import com.liferay.ide.upgrade.plan.core.internal.NamesPossibleValuesService;
import com.liferay.ide.upgrade.plan.core.internal.SwitchUpgradePlanOpMethods;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Terry Jia
 */
public interface UpgradePlansOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(UpgradePlansOp.class);

	@DelegateImplementation(SwitchUpgradePlanOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getName();

	public void setName(String name);

	@Service(impl = NamesPossibleValuesService.class)
	@Service(impl = NamesDefaultValueService.class)
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

}