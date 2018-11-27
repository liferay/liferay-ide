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

package com.liferay.ide.ui.liferay.support.server;

import com.liferay.ide.ui.liferay.support.SupportBase;
import com.liferay.ide.ui.liferay.support.workspace.LiferayWorkspaceSupport;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class LiferaryWorkspaceTomcat7xSupport extends SupportBase {

	public LiferaryWorkspaceTomcat7xSupport(SWTWorkbenchBot bot, LiferayWorkspaceSupport workspace) {
		super(bot);

		_workspace = workspace;
	}

	@Override
	public void after() {
		dialogAction.deleteRuntimFromPreferences(_workspace.getServerName());
	}

	@Override
	public void before() {
		super.before();

		_workspace.initBundle();
	}

	private LiferayWorkspaceSupport _workspace;

}