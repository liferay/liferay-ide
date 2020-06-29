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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class LiferayRuntimeNameDefaultValueService
	extends DefaultValueService implements IRuntimeLifecycleListener, SapphireContentAccessor {

	@Override
	public void dispose() {
		ServerCore.removeRuntimeLifecycleListener(this);

		_op().detach(_listener, "*");

		super.dispose();
	}

	public void runtimeAdded(IRuntime runtime) {
		refresh();
	}

	public void runtimeChanged(IRuntime runtime) {
		refresh();
	}

	public void runtimeRemoved(IRuntime runtime) {
		refresh();
	}

	@Override
	protected String compute() {
		IRuntime[] runtimes = ServerCore.getRuntimes();

		if (ListUtil.isEmpty(runtimes)) {
			return _NONE;
		}

		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();
		String value = _NONE;

		for (IRuntime runtime : runtimes) {
			if (LiferayServerCore.newPortalBundle(runtime.getLocation()) != null) {
				if (workspaceProject != null) {
					IPath workspaceProjectLocation = workspaceProject.getLocation();

					String homeDir = LiferayWorkspaceUtil.getHomeDir(workspaceProject);

					Path bundlePath = new Path(homeDir);

					IPath runtimeLocation = runtime.getLocation();

					if (bundlePath.isAbsolute()) {
						if (runtimeLocation.equals(bundlePath)) {
							value = runtime.getName();

							break;
						}
					}
					else {
						IPath bundleLocation = workspaceProjectLocation.append(homeDir);

						if (bundleLocation.equals(runtimeLocation)) {
							value = runtime.getName();

							break;
						}
					}
				}
				else {
					value = runtime.getName();

					break;
				}
			}
		}

		return value;
	}

	@Override
	protected void initDefaultValueService() {
		super.initDefaultValueService();

		ServerCore.addRuntimeLifecycleListener(this);

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		_op().attach(_listener, "*");
	}

	private BaseModuleOp _op() {
		return context(BaseModuleOp.class);
	}

	private static final String _NONE = "<None>";

	private FilteredListener<PropertyContentEvent> _listener;

}