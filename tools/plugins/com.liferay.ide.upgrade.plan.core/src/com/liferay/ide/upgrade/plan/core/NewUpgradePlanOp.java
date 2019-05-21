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

import com.liferay.ide.upgrade.plan.core.internal.NameValidationService;
import com.liferay.ide.upgrade.plan.core.internal.NewUpgradePlanOpMethods;
import com.liferay.ide.upgrade.plan.core.internal.OutlinePossibleValuesService;
import com.liferay.ide.upgrade.plan.core.internal.OutlineValidationService;
import com.liferay.ide.upgrade.plan.core.internal.SourceLocationValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Fact;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Siomn Jiang
 */
public interface NewUpgradePlanOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(NewUpgradePlanOp.class);

	@DelegateImplementation(NewUpgradePlanOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getCurrentVersion();

	public Value<Path> getLocation();

	public Value<String> getName();

	public Value<String> getTargetVersion();

	public Value<String> getUpgradePlanOutline();

	public void setCurrentVersion(String currentVersion);

	public void setLocation(Path value);

	public void setName(String name);

	public void setTargetVersion(String targetVersion);

	public void setUpgradePlanOutline(String upgradePlanOutline);

	@DefaultValue(text = "6.2")
	@Label(standard = "Current Liferay Version")
	@PossibleValues(values = {"6.2", "7.0"})
	public ValueProperty PROP_CURRENT_VERSION = new ValueProperty(TYPE, "CurrentVersion");

	@AbsolutePath
	@Fact(statement = "This location should be either a Plugins SDK, Liferay Workspace.")
	@Label(standard = "Current Code Location")
	@Service(impl = SourceLocationValidationService.class)
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, "Location");

	@Required
	@Service(impl = NameValidationService.class)
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@DefaultValue(text = "7.1")
	@Label(standard = "Target Liferay Version")
	@PossibleValues(values = {"7.0", "7.1"})
	public ValueProperty PROP_TARGET_VERSION = new ValueProperty(TYPE, "TargetVersion");

	@Required
	@Service(impl = OutlinePossibleValuesService.class)
	@Service(impl = OutlineValidationService.class)
	public ValueProperty PROP_UPGRADE_PLAN_OUTLINE = new ValueProperty(TYPE, "UpgradePlanOutline");

}