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

package com.liferay.ide.project.core.upgrade.service;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Terry Jia
 */
public class LiferayServerNamePossibleValuesService extends PossibleValuesService implements IServerLifecycleListener {

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

	@Override
	public void serverAdded(IServer server) {
		refresh();
	}

	@Override
	public void serverChanged(IServer server) {
		refresh();
	}

	@Override
	public void serverRemoved(IServer arg0) {
		refresh();
	}

	@Override
	protected void compute(Set<String> values) {
		IServer[] servers = ServerCore.getServers();

		if (ListUtil.isNotEmpty(servers)) {
			for (IServer server : servers) {
				if (LiferayServerCore.newPortalBundle(server.getRuntime().getLocation()) != null) {
					values.add(server.getName());
				}
			}
		}
	}

	@Override
	protected void initPossibleValuesService() {
		super.initPossibleValuesService();

		ServerCore.addServerLifecycleListener(this);
	}

}