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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;

/**
 * @author Andy Wu
 */
public class NewLiferayWorkspaceServerNameService extends DefaultValueService {

	@Override
	public void dispose() {
		Value<Object> workspaceNameValue = _op().property(NewLiferayWorkspaceOp.PROP_WORKSPACE_NAME);

		workspaceNameValue.detach(_listener);

		super.dispose();
	}

	@Override
	protected String compute() {
		Value<String> workspaceNameValue = _op().getWorkspaceName();

		String name = workspaceNameValue.content();

		if (CoreUtil.isNullOrEmpty(name)) {
			return StringPool.EMPTY;
		}

		return name;
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

		Value<Object> workspaceNameValue = _op().property(NewLiferayWorkspaceOp.PROP_WORKSPACE_NAME);

		workspaceNameValue.attach(_listener);
	}

	private NewLiferayWorkspaceOp _op() {
		return context(NewLiferayWorkspaceOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}