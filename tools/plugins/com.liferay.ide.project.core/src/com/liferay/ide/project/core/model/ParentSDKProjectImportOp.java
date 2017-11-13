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

import com.liferay.ide.project.core.model.internal.SDKImportDerivedValueService;
import com.liferay.ide.project.core.model.internal.SDKImportValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Simon Jiang
 */
public interface ParentSDKProjectImportOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(ParentSDKProjectImportOp.class);

	@DelegateImplementation(ParentSDKProjectImportOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<Path> getSdkLocation();

	public Value<String> getSdkVersion();

	public void setSdkLocation(Path value);

	public void setSdkLocation(String value);

	public void setSdkVersion(String value);

	@AbsolutePath
	@Label(standard = "SDK Directory")
	@Required
	@Service(impl = SDKImportValidationService.class)
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	public ValueProperty PROP_SDK_LOCATION = new ValueProperty(TYPE, "SdkLocation");

	@Derived
	@Label(standard = "SDK Version")
	@Service(impl = SDKImportDerivedValueService.class)
	public ValueProperty PROP_SDK_VERSION = new ValueProperty(TYPE, "SdkVersion");

}