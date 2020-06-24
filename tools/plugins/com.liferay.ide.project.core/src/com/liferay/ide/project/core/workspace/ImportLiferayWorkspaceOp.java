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

import org.eclipse.sapphire.ElementType;
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
 * @author Andy Wu
 */
public interface ImportLiferayWorkspaceOp extends BaseLiferayWorkspaceOp {

	public ElementType TYPE = new ElementType(ImportLiferayWorkspaceOp.class);

	@DelegateImplementation(ImportLiferayWorkspaceOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getBuildType();

	public Value<Boolean> getHasBundlesDir();

	public Value<Path> getWorkspaceLocation();

	public void setBuildType(String value);

	public void setHasBundlesDir(Boolean value);

	public void setHasBundlesDir(String value);

	public void setWorkspaceLocation(Path value);

	public void setWorkspaceLocation(String value);

	@Derived
	@Service(impl = ImportWorkspaceBuildTypeDerivedValueService.class)
	public ValueProperty PROP_BUILD_TYPE = new ValueProperty(TYPE, "BuildType");

	@Service(impl = ImportWorkspaceBundleUrlDefaultValueService.class)
	public ValueProperty PROP_BUNDLE_URL = new ValueProperty(TYPE, BaseLiferayWorkspaceOp.PROP_BUNDLE_URL);

	@Derived
	@Service(impl = HasBundlesDirDerivedValueService.class)
	@Type(base = Boolean.class)
	public ValueProperty PROP_HAS_BUNDLES_DIR = new ValueProperty(TYPE, "hasBundlesDir");

	@Service(impl = ImportLiferayWorkspaceServerNameService.class)
	public ValueProperty PROP_SERVER_NAME = new ValueProperty(TYPE, BaseLiferayWorkspaceOp.PROP_SERVER_NAME);

	@AbsolutePath
	@Label(standard = "workspace location")
	@Required
	@Service(impl = ImportWorkspaceLocationValidationService.class)
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	public ValueProperty PROP_WORKSPACE_LOCATION = new ValueProperty(TYPE, "WorkspaceLocation");

}