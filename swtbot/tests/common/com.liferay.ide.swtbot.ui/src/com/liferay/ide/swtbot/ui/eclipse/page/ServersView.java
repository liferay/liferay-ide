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

package com.liferay.ide.swtbot.ui.eclipse.page;

import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class ServersView extends View {

	public ServersView(SWTWorkbenchBot bot) {
		super(bot, SERVERS);

		_servers = new Tree(bot, 1);
	}

	public void clickDebugBtn() {
		clickToolbarButton(START_THE_SERVER_IN_DEBUG_MODE);
	}

	public void clickStartBtn() {
		clickToolbarButton(START_THE_SERVER);
	}

	public void clickStopBtn() {
		clickToolbarButton(STOP_THE_SERVER);
	}

	public Tree getServers() {
		return _servers;
	}

	private Tree _servers;

}