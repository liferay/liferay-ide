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
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 * @author Andy Wu
 */
public class OSGiBundleListener extends FilteredListener<PropertyContentEvent> implements SapphireContentAccessor {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		NewModuleFragmentOp op = op(event);

		ProjectCore projectCore = ProjectCore.getDefault();

		IPath temp = projectCore.getStateLocation();

		String runtimeName = get(op.getLiferayRuntimeName());
		String hostOsgiBundle = get(op.getHostOsgiBundle());

		IRuntime runtime = ServerUtil.getRuntime(runtimeName);

		if (!CoreUtil.empty(hostOsgiBundle)) {
			ServerUtil.getModuleFileFrom70Server(runtime, hostOsgiBundle, temp);
		}

		SapphireUtil.clear(op.getOverrideFiles());
	}

	protected NewModuleFragmentOp op(PropertyContentEvent event) {
		Property property = event.property();

		Element element = property.element();

		return element.nearest(NewModuleFragmentOp.class);
	}

}