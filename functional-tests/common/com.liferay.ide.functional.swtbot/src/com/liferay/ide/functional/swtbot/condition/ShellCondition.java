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
public class ShellCondition implements ICondition {

	public ShellCondition(String title, boolean equal) {
		_title = title;
		_equal = equal;
	}

	public String getFailureMessage() {
		if (_equal) {
			return "shell \"" + _title + "\" still not active";
		}
		else {
			return "shell \"" + _title + "\" still active";
		}
	}

	public void init(SWTBot bot) {
		_bot = bot;
	}

	public boolean test() throws Exception {
		SWTBotShell shell = _bot.activeShell();

		if (_title.equals(shell.getText()) == _equal) {
			return true;
		}

		return false;
	}

	private SWTBot _bot;
	private boolean _equal;
	private String _title;

}