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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Kuo Zhang
 */
public class GroupIdDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		String groupId = null;

		NewLiferayPluginProjectOp op = _op();

		Path location = SapphireUtil.getContent(op.getLocation());

		if (location != null) {
			String parentProjectLocation = location.toOSString();

			IPath parentProjectOsPath = org.eclipse.core.runtime.Path.fromOSString(parentProjectLocation);

			String projectName = SapphireUtil.getContent(op.getProjectName());

			groupId = NewLiferayPluginProjectOpMethods.getMavenParentPomGroupId(op, projectName, parentProjectOsPath);
		}

		if (groupId == null) {
			groupId = _getDefaultMavenGroupId();

			if (CoreUtil.isNullOrEmpty(groupId)) {
				groupId = "com.example.plugins";
			}
		}

		return groupId;
	}

	@Override
	protected void initDefaultValueService() {
		super.initDefaultValueService();

		Listener listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayPluginProjectOp op = _op();

		SapphireUtil.attachListener(op.getLocation(), listener);
		SapphireUtil.attachListener(op.getProjectName(), listener);
	}

	private String _getDefaultMavenGroupId() {
		IScopeContext[] prefContexts = {DefaultScope.INSTANCE, InstanceScope.INSTANCE};

		IPreferencesService preferencesService = Platform.getPreferencesService();

		String defaultMavenGroupId = preferencesService.getString(
			ProjectCore.PLUGIN_ID, ProjectCore.PREF_DEFAULT_PLUGIN_PROJECT_MAVEN_GROUPID, null, prefContexts);

		return defaultMavenGroupId;
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

}