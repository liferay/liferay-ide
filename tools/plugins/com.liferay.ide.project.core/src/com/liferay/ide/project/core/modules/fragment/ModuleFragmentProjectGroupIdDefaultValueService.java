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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectCore;

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
 * @author Joye Luo
 */
public class ModuleFragmentProjectGroupIdDefaultValueService
	extends DefaultValueService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewModuleFragmentOp op = _op();

		if ((_listener != null) && (op != null) && !op.disposed()) {
			SapphireUtil.detachListener(op.getProjectName(), _listener);
			SapphireUtil.detachListener(op.getProjectName(), _listener);

			_listener = null;
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		String groupId = null;

		NewModuleFragmentOp op = _op();

		Path location = get(op.getLocation());

		if (location != null) {
			String parentProjectLocation = location.toOSString();

			IPath parentProjectOsPath = org.eclipse.core.runtime.Path.fromOSString(parentProjectLocation);

			String projectName = get(op.getProjectName());

			groupId = NewModuleFragmentOpMethods.getMavenParentPomGroupId(op, projectName, parentProjectOsPath);
		}

		if (groupId == null) {
			groupId = _getDefaultMavenGroupId();

			if (CoreUtil.isNullOrEmpty(groupId)) {
				groupId = get(op.getProjectName());
			}
		}

		return groupId;
	}

	@Override
	protected void initDefaultValueService() {
		super.initDefaultValueService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewModuleFragmentOp op = _op();

		SapphireUtil.attachListener(op.getLocation(), _listener);
		SapphireUtil.attachListener(op.getProjectName(), _listener);
	}

	private String _getDefaultMavenGroupId() {
		IScopeContext[] prefContexts = {DefaultScope.INSTANCE, InstanceScope.INSTANCE};

		IPreferencesService preferencesService = Platform.getPreferencesService();

		String defaultMavenGroupId = preferencesService.getString(
			ProjectCore.PLUGIN_ID, ProjectCore.PREF_DEFAULT_MODULE_PROJECT_MAVEN_GROUPID, null, prefContexts);

		return defaultMavenGroupId;
	}

	private NewModuleFragmentOp _op() {
		return context(NewModuleFragmentOp.class);
	}

	private Listener _listener;

}