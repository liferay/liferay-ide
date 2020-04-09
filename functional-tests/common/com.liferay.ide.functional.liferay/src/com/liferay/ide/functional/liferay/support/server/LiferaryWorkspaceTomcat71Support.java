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

package com.liferay.ide.functional.liferay.support.server;

import com.liferay.ide.functional.liferay.support.SupportBase;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceSupport;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class LiferaryWorkspaceTomcat71Support extends SupportBase {

	public LiferaryWorkspaceTomcat71Support(SWTWorkbenchBot bot, LiferayWorkspaceSupport workspace) {
		super(bot);

		_workspace = workspace;
	}

	@Override
	public void after() {
		dialogAction.deleteRuntimFromPreferences(0);
	}

	@Override
	public void before() {
		super.before();

		_workspace.initBundle();
	}

	public String getServerName() {
		return serverName;
	}

	public String getStartedLabel() {
		return getServerName() + "  [Started]";
	}

	public String getStoppedLabel() {
		return getServerName() + "  [Stopped]";
	}

	public String serverName = "Liferay Community Edition Portal 7.1.3 CE GA4";

	private LiferayWorkspaceSupport _workspace;

}