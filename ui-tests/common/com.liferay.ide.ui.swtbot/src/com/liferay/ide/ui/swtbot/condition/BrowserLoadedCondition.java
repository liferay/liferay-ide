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

package com.liferay.ide.ui.swtbot.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotBrowser;

/**
 * @author Terry Jia
 */
public class BrowserLoadedCondition implements ICondition {

	public BrowserLoadedCondition(SWTBot bot) {
		_bot = bot;
	}

	@Override
	public String getFailureMessage() {
		return "Broswer is still loading";
	}

	@Override
	public void init(SWTBot bot) {
	}

	@Override
	public boolean test() throws Exception {
		SWTBotBrowser botBrower = _bot.browser();

		return botBrower.isPageLoaded();
	}

	private final SWTBot _bot;

}