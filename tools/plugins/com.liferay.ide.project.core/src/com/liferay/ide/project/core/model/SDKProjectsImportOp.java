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

package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.model.internal.HasWorkspaceSdkDefaultValueService;
import com.liferay.ide.project.core.model.internal.SDKImportLocationValidationService;
import com.liferay.ide.project.core.model.internal.SDKImportVersionDerivedValueService;
import com.liferay.ide.project.core.model.internal.SDKProjectsImportLocationInitialValueService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Simon Jiang
 */
public interface SDKProjectsImportOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(SDKProjectsImportOp.class);

	@DelegateImplementation(SDKImportProjectsOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<Path> getSdkLocation();

	public Value<String> getSdkVersion();

	public ElementList<ProjectNamedItem> getSelectedProjects();

	public void setSdkLocation(Path value);

	public void setSdkLocation(String value);

	public void setSdkVersion(String value);

	@Service(impl = HasWorkspaceSdkDefaultValueService.class)
	@Type(base = Boolean.class)
	public ValueProperty PROP_HAS_WORKSPACE_SDK = new ValueProperty(TYPE, "HasWorkspaceSDK");

	@AbsolutePath
	@Enablement(expr = "${HasWorkspaceSDK == 'false'}")
	@Label(standard = "SDK Directory")
	@Services(
		{
			@Service(impl = SDKImportLocationValidationService.class),
			@Service(impl = SDKProjectsImportLocationInitialValueService.class)
		}
	)
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	public ValueProperty PROP_SDK_LOCATION = new ValueProperty(TYPE, "SdkLocation");

	@Derived
	@Enablement(expr = "${HasWorkspaceSDK == 'false'}")
	@Label(standard = "SDK Version")
	@Service(impl = SDKImportVersionDerivedValueService.class)
	public ValueProperty PROP_SDK_VERSION = new ValueProperty(TYPE, "SdkVersion");

	@Type(base = ProjectNamedItem.class)
	public ListProperty PROP_SELECTED_PROJECTS = new ListProperty(TYPE, "SelectedProjects");

}