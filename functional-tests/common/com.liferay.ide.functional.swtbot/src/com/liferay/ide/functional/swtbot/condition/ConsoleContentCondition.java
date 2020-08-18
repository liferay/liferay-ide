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

import org.eclipse.jface.text.IDocument;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.TextConsole;

/**
 * @author Terry Jia
 */
public class ConsoleContentCondition implements ICondition {

	public ConsoleContentCondition(String consoleName, String content) {
		_consoleName = consoleName;
		_content = content;

		_lastContent = "No Text Console be found.";
	}

	@Override
	public String getFailureMessage() {
		return "Console does not contain " + _content + ". The last content is " + _lastContent;
	}

	@Override
	public void init(SWTBot bot) {
	}

	@Override
	public boolean test() throws Exception {
		ConsolePlugin plugin = ConsolePlugin.getDefault();

		IConsoleManager manager = plugin.getConsoleManager();

		IConsole[] consoles = manager.getConsoles();

		for (IConsole console : consoles) {
			String consoleName = console.getName();

			if (consoleName.contains(_consoleName) && (console instanceof TextConsole)) {
				TextConsole textConsole = (TextConsole)console;

				IDocument content = textConsole.getDocument();

				_lastContent = content.get();

				return _lastContent.contains(_content);
			}
		}

		return false;
	}

	private String _consoleName;
	private String _content;
	private String _lastContent;

}