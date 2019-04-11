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

package com.liferay.ide.alloy.core;

import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import tern.eclipse.ide.core.DefaultTernModule;
import tern.eclipse.ide.core.IDefaultTernModulesProvider;
import tern.eclipse.ide.core.ITernNatureCapability;

import tern.server.TernPlugin;

/**
 * @author Gregory Amerson
 */
public class LiferayProjectTernAdapter implements IDefaultTernModulesProvider, ITernNatureCapability {

	@Override
	public Collection<DefaultTernModule> getTernModules(IProject project) {
		Collection<DefaultTernModule> modules = new ArrayList<>();

		// here manage your condition to add aui2.0.x

		modules.add(new DefaultTernModule(TernPlugin.aui2.getName()));

		return modules;
	}

	@Override
	public boolean hasTernNature(IProject project) throws CoreException {
		return ProjectUtil.isPortletProject(project);
	}

}