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

package com.liferay.ide.project.core.modules.ext;

import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.project.core.service.CommonProjectLocationInitialValueService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;

import org.osgi.framework.Version;

/**
 * @author Charles Wu
 */
public interface NewModuleExtOp extends BaseModuleOp {

	public ElementType TYPE = new ElementType(NewModuleExtOp.class);

	@DelegateImplementation(NewModuleExtOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getOriginalModuleName();

	public Value<Version> getOriginalModuleVersion();

	public Value<String> getTargetPlatformVersion();

	public void setOriginalModuleName(String value);

	public void setOriginalModuleVersion(Version value);

	public void setTargetPlatformVersion(String value);

	@Service(impl = CommonProjectLocationInitialValueService.class)
	@Service(impl = ModuleExtProjectLocationValidationService.class)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_LOCATION);

	@Required
	public ValueProperty PROP_ORIGINAL_MODULE_NAME = new ValueProperty(TYPE, "OriginalModuleName");

	@Enablement(expr = "${ TargetPlatformVersion == null }")
	@Required
	@Type(base = Version.class)
	public ValueProperty PROP_ORIGINAL_MODULE_VERSION = new ValueProperty(TYPE, "OriginalModuleVersion");

	@Listeners(ModuleExtProjectNameListener.class)
	@Service(impl = ModuleExtProjectNameValidationService.class)
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_NAME);

	@DefaultValue(text = "gradle-module-ext")
	public ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_PROVIDER);

	@Service(impl = TargetPlatformVersionDefaultValueService.class)
	@Type(base = String.class)
	public ValueProperty PROP_TARGET_PLATFORM_VERSION = new ValueProperty(TYPE, "TargetPlatformVersion");

	@Listeners(ModuleExtProjectUseDefaultLocationListener.class)
	public ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_USE_DEFAULT_LOCATION);

}