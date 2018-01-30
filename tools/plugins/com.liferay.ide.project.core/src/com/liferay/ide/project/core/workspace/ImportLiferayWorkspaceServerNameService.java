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

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Andy Wu
 * @author Charles Wu
 */
public class ImportLiferayWorkspaceServerNameService extends DefaultValueService {

	@Override
	public void dispose() {
		Value<Object> workspaceLocation = _op().property(ImportLiferayWorkspaceOp.PROP_WORKSPACE_LOCATION);

		workspaceLocation.detach(_listener);

		super.dispose();
	}

	@Override
	protected String compute() {
		Value<Path> workspaceLocation = _op().getWorkspaceLocation();

		Path path = workspaceLocation.content();

		if (path == null) {
			return StringPool.EMPTY;
		}

		String serverName = path.lastSegment() + " server";

		PortalBundle bundle = LiferayServerCore.newPortalBundle(PathBridge.create(path).append("bundles"));

		if (bundle != null) {
			serverName = bundle.getServerReleaseInfo();
		}

		return serverName;
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

		Value<Object> workspaceLocation = _op().property(ImportLiferayWorkspaceOp.PROP_WORKSPACE_LOCATION);

		workspaceLocation.attach(_listener);
	}

	private ImportLiferayWorkspaceOp _op() {
		return context(ImportLiferayWorkspaceOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}