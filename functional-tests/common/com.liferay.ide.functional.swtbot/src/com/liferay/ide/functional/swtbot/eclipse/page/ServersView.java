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

package com.liferay.ide.functional.swtbot.eclipse.page;

import com.liferay.ide.functional.swtbot.page.Tree;
import com.liferay.ide.functional.swtbot.page.View;
import com.liferay.ide.functional.swtbot.util.CoreUtil;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class ServersView extends View {

	public ServersView(SWTWorkbenchBot bot) {
		super(bot, SERVERS);
	}

	public void clickDebugBtn() {
		if (CoreUtil.isMac()) {
			clickToolbarButton(START_THE_SERVER_IN_DEBUG_MODE_WITH_KEY_MAC);
		}
		else {
			clickToolbarButton(START_THE_SERVER_IN_DEBUG_MODE_WITH_KEY);
		}
	}

	public void clickStartBtn() {
		if (CoreUtil.isMac()) {
			clickToolbarButton(START_THE_SERVER_WITH_KEYS_MAC);
		}
		else {
			clickToolbarButton(START_THE_SERVER_WITH_KEYS);
		}
	}

	public void clickStopBtn() {
		if (CoreUtil.isMac()) {
			clickToolbarButton(STOP_THE_SERVER_WITH_KEYS_MAC);
		}
		else {
			clickToolbarButton(STOP_THE_SERVER_WITH_KEYS);
		}
	}

	public Tree getServers() {
		return new Tree(getPart().bot());
	}

}