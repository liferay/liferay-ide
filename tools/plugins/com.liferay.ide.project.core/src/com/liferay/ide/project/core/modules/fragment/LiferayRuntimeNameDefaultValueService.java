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
import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Terry Jia
 */
public class LiferayRuntimeNameDefaultValueService extends DefaultValueService implements IRuntimeLifecycleListener {

	@Override
	public void dispose() {
		ServerCore.removeRuntimeLifecycleListener(this);

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

		String value = _NONE;

		for (IRuntime runtime : runtimes) {
			if (LiferayServerCore.newPortalBundle(runtime.getLocation()) != null) {
				value = runtime.getName();

				break;
			}
		}

		return value;
	}

	@Override
	protected void initDefaultValueService() {
		super.initDefaultValueService();

		ServerCore.addRuntimeLifecycleListener(this);
	}

	private static final String _NONE = "<None>";

}