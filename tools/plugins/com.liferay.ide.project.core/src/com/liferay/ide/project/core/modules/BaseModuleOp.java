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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.ProjectName;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Terry Jia
 */
public interface BaseModuleOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(BaseModuleOp.class);

	public Value<String> getFinalProjectName();

	public Value<Path> getInitialSelectionPath();

	public Value<Path> getLocation();

	public Value<String> getProjectName();

	public ElementList<ProjectName> getProjectNames();

	public Value<NewLiferayProjectProvider<BaseModuleOp>> getProjectProvider();

	public Value<Boolean> getUseDefaultLocation();

	public void setFinalProjectName(String value);

	public void setInitialSelectionPath(Path value);

	public void setInitialSelectionPath(String value);

	public void setLocation(Path value);

	public void setLocation(String value);

	public void setProjectName(String value);

	public void setProjectProvider(NewLiferayProjectProvider<BaseModuleOp> value);

	public void setProjectProvider(String value);

	public void setUseDefaultLocation(Boolean value);

	public void setUseDefaultLocation(String value);

	@DefaultValue(text = "${ProjectName}")
	public ValueProperty PROP_FINAL_PROJECT_NAME = new ValueProperty(TYPE, "FinalProjectName");

	@AbsolutePath
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	public ValueProperty PROP_INITIAL_SELECTION_PATH = new ValueProperty(TYPE, "InitialSelectionPath");

	@AbsolutePath
	@Enablement(expr = "${ UseDefaultLocation == 'false' }")
	@Label(standard = "location")
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, "Location");

	@Label(standard = "project name")
	@Required
	@Service(impl = DumbStateValidationService.class)
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, "ProjectName");

	@Type(base = ProjectName.class)
	public ListProperty PROP_PROJECT_NAMES = new ListProperty(TYPE, "ProjectNames");

	@Type(base = ILiferayProjectProvider.class)
	public ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty(TYPE, "ProjectProvider");

	@DefaultValue(text = "true")
	@Label(standard = "use default location")
	@Type(base = Boolean.class)
	public ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty(TYPE, "UseDefaultLocation");

}