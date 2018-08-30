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

import com.liferay.ide.core.util.SapphireUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Joye Luo
 */
public class ModuleFragmentProjectArtifactVersionDefaultValueService extends DefaultValueService {

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
		String data = null;

		NewModuleFragmentOp op = _op();

		Path location = SapphireUtil.getContent(op.getLocation());

		if (location != null) {
			String parentProjectLocation = location.toOSString();

			IPath parentProjectOsPath = org.eclipse.core.runtime.Path.fromOSString(parentProjectLocation);

			String projectName = SapphireUtil.getContent(op.getProjectName());

			data = NewModuleFragmentOpMethods.getMavenParentPomVersion(op, projectName, parentProjectOsPath);
		}

		if (data == null) {
			data = "1.0.0-SNAPSHOT";
		}

		return data;
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

	private NewModuleFragmentOp _op() {
		return context(NewModuleFragmentOp.class);
	}

	private Listener _listener;

}