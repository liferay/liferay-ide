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

import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;

import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Andy Wu
 */
public class ImportWorkspaceBuildTypeDerivedValueService extends DerivedValueService {

	@Override
	public void dispose() {
		if (_op() != null) {
			Value<Object> workspaceLocation = _op().property(ImportLiferayWorkspaceOp.PROP_WORKSPACE_LOCATION);

			workspaceLocation.detach(_listener);
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		String retVal = null;

		if (_op().getWorkspaceLocation() != null) {
			Value<Path> workspaceLocation = _op().getWorkspaceLocation();

			Path path = workspaceLocation.content();

			if ((path != null) && !path.isEmpty()) {
				String location = path.toOSString();

				retVal = LiferayWorkspaceUtil.getWorkspaceType(location);
			}
		}

		return retVal;
	}

	@Override
	protected void initDerivedValueService() {
		super.initDerivedValueService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		Value<Object> workspaceLocation = _op().property(ImportLiferayWorkspaceOp.PROP_WORKSPACE_LOCATION);

		workspaceLocation.attach(_listener);
	}

	private ImportLiferayWorkspaceOp _op() {
		return context(ImportLiferayWorkspaceOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}