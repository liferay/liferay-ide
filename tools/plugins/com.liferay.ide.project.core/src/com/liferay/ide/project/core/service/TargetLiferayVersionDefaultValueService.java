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

package com.liferay.ide.project.core.service;

import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.project.core.ProjectCore;

import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Joye Luo
 * @author Terry Jia
 */
public class TargetLiferayVersionDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		Set<String> liferayTargetPlatformVersions = WorkspaceConstants.liferayTargetPlatformVersions.keySet();

		String[] versions = liferayTargetPlatformVersions.toArray(new String[0]);

		String defaultValue = versions[versions.length - 2];

		IScopeContext[] scopeContexts = {DefaultScope.INSTANCE, InstanceScope.INSTANCE};

		IPreferencesService preferencesService = Platform.getPreferencesService();

		String defaultLiferayVersion = preferencesService.getString(
			ProjectCore.PLUGIN_ID, ProjectCore.PREF_DEFAULT_LIFERAY_VERSION_OPTION, null, scopeContexts);

		if (defaultLiferayVersion != null) {
			defaultValue = defaultLiferayVersion;
		}

		return defaultValue;
	}

}