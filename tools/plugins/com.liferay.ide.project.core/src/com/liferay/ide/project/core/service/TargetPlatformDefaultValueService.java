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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Terry Jia
 * @author Ethan Sun
 */
public class TargetPlatformDefaultValueService extends DefaultValueService implements SapphireContentAccessor {

	@Override
	protected String compute() {
		String liferayVersion = get(_op.getLiferayVersion());

		return WorkspaceConstants.liferayTargetPlatformVersions.get(liferayVersion)[0];
	}

	@Override
	protected void initDefaultValueService() {
		_op = context(NewLiferayWorkspaceOp.class);

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				String liferayVersion = get(_op.getLiferayVersion());

				_op.setTargetPlatform(WorkspaceConstants.liferayTargetPlatformVersions.get(liferayVersion)[0]);

				refresh();
			}

		};

		SapphireUtil.attachListener(_op.property(NewLiferayWorkspaceOp.PROP_LIFERAY_VERSION), _listener);
	}

	private FilteredListener<PropertyContentEvent> _listener;
	private NewLiferayWorkspaceOp _op;

}