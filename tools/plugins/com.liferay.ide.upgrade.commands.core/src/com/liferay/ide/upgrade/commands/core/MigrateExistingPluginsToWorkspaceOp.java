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

package com.liferay.ide.upgrade.commands.core;

import com.liferay.ide.project.core.model.ProjectNamedItem;
import com.liferay.ide.upgrade.commands.core.internal.MigrateExistingPluginsToWorkspacOpMethods;

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
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public interface MigrateExistingPluginsToWorkspaceOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(MigrateExistingPluginsToWorkspaceOp.class);

	@DelegateImplementation(MigrateExistingPluginsToWorkspacOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<Path> getSdkLocation();

	public ElementList<ProjectNamedItem> getSelectedProjects();

	public void setSdkLocation(Path value);

	public void setSdkLocation(String value);

	@Enablement(expr = "${false}")
	@Label(standard = "SDK Directory")
	@Type(base = Path.class)
	public ValueProperty PROP_SDK_LOCATION = new ValueProperty(TYPE, "SdkLocation");

	@Type(base = ProjectNamedItem.class)
	public ListProperty PROP_SELECTED_PROJECTS = new ListProperty(TYPE, "SelectedProjects");

}