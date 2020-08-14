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
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class HasBundlesDirDerivedValueService extends DerivedValueService {

	@Override
	protected String compute() {
		String retval = "false";

		Value<Path> workspaceLocationValue = _op().getWorkspaceLocation();

		Path path = workspaceLocationValue.content();

		if ((path != null) && LiferayWorkspaceUtil.isValidWorkspaceLocation(path.toPortableString())) {
			retval = LiferayWorkspaceUtil.hasBundlesDir(path.toPortableString()) ? "true" : "false";
		}

		return retval;
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

		Value<Object> workspaceLaction = _op().property(ImportLiferayWorkspaceOp.PROP_WORKSPACE_LOCATION);

		workspaceLaction.attach(_listener);
	}

	private ImportLiferayWorkspaceOp _op() {
		return context(ImportLiferayWorkspaceOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}