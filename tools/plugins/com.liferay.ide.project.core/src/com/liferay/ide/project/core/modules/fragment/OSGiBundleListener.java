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
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 * @author Andy Wu
 */
public class OSGiBundleListener extends FilteredListener<PropertyContentEvent> {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		NewModuleFragmentOp op = op(event);

		IPath temp = ProjectCore.getDefault().getStateLocation();

		String runtimeName = op.getLiferayRuntimeName().content();
		String hostOsgiBundle = op.getHostOsgiBundle().content();

		IRuntime runtime = ServerUtil.getRuntime(runtimeName);

		if (!CoreUtil.empty(hostOsgiBundle)) {
			ServerUtil.getModuleFileFrom70Server(runtime, hostOsgiBundle, temp);
		}

		op.getOverrideFiles().clear();
	}

	protected NewModuleFragmentOp op(PropertyContentEvent event) {
		Element element = event.property().element();

		return element.nearest(NewModuleFragmentOp.class);
	}

}