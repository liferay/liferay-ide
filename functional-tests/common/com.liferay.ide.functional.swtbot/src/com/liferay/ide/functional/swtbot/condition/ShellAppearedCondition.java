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

package com.liferay.ide.functional.swtbot.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * @author Terry Jia
 */
public class ShellAppearedCondition implements ICondition {

	public ShellAppearedCondition(SWTBot bot, String title) {
		_bot = bot;
		_title = title;
	}

	public String getFailureMessage() {
		return "Shell \"" + _title + "\" does not appeared";
	}

	public void init(SWTBot bot) {
	}

	public boolean test() throws Exception {
		SWTBotShell shell = _bot.activeShell();

		if (_title.equals(shell.getText())) {
			return true;
		}

		return false;
	}

	private SWTBot _bot;
	private String _title;

}