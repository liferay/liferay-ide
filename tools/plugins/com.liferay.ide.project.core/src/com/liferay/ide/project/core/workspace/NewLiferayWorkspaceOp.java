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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.project.core.service.CommonProjectLocationInitialValueService;
import com.liferay.ide.project.core.service.TargetLiferayVersionDefaultValueService;
import com.liferay.ide.project.core.service.TargetLiferayVersionPossibleValuesService;
import com.liferay.ide.project.core.service.TargetPlatformDefaultValueService;
import com.liferay.ide.project.core.service.TargetPlatformPossibleValuesService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Andy Wu
 * @author Terry Jia
 */
public interface NewLiferayWorkspaceOp extends BaseLiferayWorkspaceOp {

	public ElementType TYPE = new ElementType(NewLiferayWorkspaceOp.class);

	@DelegateImplementation(NewLiferayWorkspaceOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getLiferayVersion();

	public Value<Path> getLocation();

	public Value<String> getTargetPlatform();

	public Value<Boolean> getUseDefaultLocation();

	public Value<String> getWorkspaceName();

	public void setLiferayVersion(String value);

	public void setLocation(Path value);

	public void setLocation(String value);

	public void setTargetPlatform(String value);

	public void setUseDefaultLocation(Boolean value);

	public void setUseDefaultLocation(String value);

	public void setWorkspaceName(String value);

	@Label(standard = "liferay version")
	@Listeners(value = TargetLiferayVersionListener.class)
	@Service(impl = TargetLiferayVersionDefaultValueService.class)
	@Service(impl = TargetLiferayVersionPossibleValuesService.class)
	public ValueProperty PROP_LIFERAY_VERSION = new ValueProperty(TYPE, "LiferayVersion");

	@AbsolutePath
	@Enablement(expr = "${ UseDefaultLocation == 'false' }")
	@Label(standard = "location")
	@Required
	@Service(impl = CommonProjectLocationInitialValueService.class)
	@Service(impl = WorkspaceLocationValidationService.class)
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, "Location");

	@Service(impl = NewLiferayWorkspaceServerNameService.class)
	public ValueProperty PROP_SERVER_NAME = new ValueProperty(TYPE, BaseLiferayWorkspaceOp.PROP_SERVER_NAME);

	@Label(standard = "target platform")
	@Service(impl = TargetPlatformDefaultValueService.class)
	@Service(impl = TargetPlatformPossibleValuesService.class)
	public ValueProperty PROP_TARGET_PLATFORM = new ValueProperty(TYPE, "TargetPlatform");

	@DefaultValue(text = "true")
	@Label(standard = "use default location")
	@Listeners(WorkspaceUseDefaultLocationListener.class)
	@Type(base = Boolean.class)
	public ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty(TYPE, "UseDefaultLocation");

	@Label(standard = "Project name")
	@Required
	@Service(impl = WorkspaceNameValidationService.class)
	public ValueProperty PROP_WORKSPACE_NAME = new ValueProperty(TYPE, "WorkspaceName");

}