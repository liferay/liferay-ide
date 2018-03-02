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

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Terry Jia
 */
public class LiferayRuntimeNamePossibleValuesService
	extends PossibleValuesService implements IRuntimeLifecycleListener {

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	public Status problem(Value<?> value) {
		if (value.content().equals("<None>")) {
			return Status.createOkStatus();
		}

		return super.problem(value);
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
	protected void compute(Set<String> values) {
		IRuntime[] runtimes = ServerCore.getRuntimes();

		if (ListUtil.isNotEmpty(runtimes)) {
			for (IRuntime runtime : runtimes) {
				if (LiferayServerCore.newPortalBundle(runtime.getLocation()) != null) {
					values.add(runtime.getName());
				}
			}
		}
	}

	@Override
	protected void initPossibleValuesService() {
		super.initPossibleValuesService();

		ServerCore.addRuntimeLifecycleListener(this);
	}

}