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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ReleaseUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.DefaultValueService;

import org.osgi.framework.Version;

/**
 * @author Joye Luo
 * @author Terry Jia
 */
public class TargetLiferayVersionDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		String liferayWorkspaceProjectVersion = LiferayWorkspaceUtil.getLiferayWorkspaceProjectVersion();

		if (CoreUtil.isNotNullOrEmpty(liferayWorkspaceProjectVersion)) {
			return liferayWorkspaceProjectVersion;
		}

		String[] versions = ReleaseUtil.getProductGroupVersions();

		String defaultValue = versions[versions.length - 1];

		IScopeContext[] scopeContexts = {DefaultScope.INSTANCE, InstanceScope.INSTANCE};

		IPreferencesService preferencesService = Platform.getPreferencesService();

		String defaultLiferayVersion = preferencesService.getString(
			ProjectCore.PLUGIN_ID, ProjectCore.PREF_DEFAULT_LIFERAY_VERSION_OPTION, null, scopeContexts);

		if (defaultLiferayVersion != null) {
			Version liferayVersion = new Version(defaultLiferayVersion);

			defaultValue = new String(liferayVersion.getMajor() + "." + liferayVersion.getMinor());
		}

		return defaultValue;
	}

}