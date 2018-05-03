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

package com.liferay.ide.theme.core.facet;

import com.liferay.ide.project.core.facet.PluginFacetUninstall;
import com.liferay.ide.theme.core.ThemeCSSBuilder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Gregory Amerson
 */
public class ThemePluginFacetUninstall extends PluginFacetUninstall {

	@Override
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
		throws CoreException {

		super.execute(project, fv, config, monitor);

		removeThemeCSSBuilder(project);
	}

	protected void removeThemeCSSBuilder(IProject project) throws CoreException {
		if (project == null) {
			return;
		}

		IProjectDescription desc = project.getDescription();

		ICommand[] commands = desc.getBuildSpec();

		List<ICommand> newCommands = new ArrayList<>();

		for (ICommand command : commands) {
			if (!ThemeCSSBuilder.ID.equals(command.getBuilderName())) {
				newCommands.add(command);
			}
		}

		desc.setBuildSpec(newCommands.toArray(new ICommand[0]));
	}

}