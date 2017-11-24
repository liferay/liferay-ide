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

import com.liferay.ide.ui.swtbot.eclipse.page.ConsoleView;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * @author Terry Jia
 */
public class ConsoleContentCondition implements ICondition {

	public ConsoleContentCondition(SWTWorkbenchBot bot, String content) {
		_bot = bot;

		_content = content;
	}

	@Override
	public String getFailureMessage() {
		return "Console doesn't contain " + _content;
	}

	@Override
	public void init(SWTBot bot) {
	}

	@Override
	public boolean test() throws Exception {
		ConsoleView console = new ConsoleView(_bot);

		String consoleContent = console.getLog().getText();

		return consoleContent.contains(_content);
	}

	private SWTWorkbenchBot _bot;
	private String _content;

}