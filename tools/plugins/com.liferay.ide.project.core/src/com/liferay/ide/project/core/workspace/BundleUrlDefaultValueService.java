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

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Andy Wu
 */
public class BundleUrlDefaultValueService extends DefaultValueService {

	@Override
	public void dispose() {
		ImportLiferayWorkspaceOp op = _op();

		Value<Object> workspaceLocation = op.property(ImportLiferayWorkspaceOp.PROP_WORKSPACE_LOCATION);

		workspaceLocation.detach(_listener);

		super.dispose();
	}

	@Override
	protected String compute() {
		ImportLiferayWorkspaceOp op = _op();

		Value<Path> workspaceLocationValue = op.getWorkspaceLocation();

		Path path = workspaceLocationValue.content();

		if (path == null) {
			return null;
		}

		String workspaceLocation = path.toPortableString();

		String buildType = LiferayWorkspaceUtil.getWorkspaceType(workspaceLocation);

		if (buildType == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> provider =
			(NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp>) LiferayCore.getProvider(buildType);

		return provider.getInitBundleUrl(workspaceLocation);
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

		ImportLiferayWorkspaceOp op = _op();

		Value<Object> workspaceLocation = op.property(ImportLiferayWorkspaceOp.PROP_WORKSPACE_LOCATION);

		workspaceLocation.attach(_listener);
	}

	private ImportLiferayWorkspaceOp _op() {
		return context(ImportLiferayWorkspaceOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}