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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
public class EditorActiveCondition implements ICondition {

	public EditorActiveCondition(String name, boolean active) {
		_name = name;
		_active = active;
	}

	public String getFailureMessage() {
		if (_active) {
			return "wait for editor " + _name + " is active failed";
		}

		return "wait for editor " + _name + " is not active failed";
	}

	public void init(SWTBot bot) {
		if (bot instanceof SWTWorkbenchBot) {
			_bot = SWTWorkbenchBot.class.cast(bot);
		}
		else {
			Assert.fail("init with wrong bot class");
		}
	}

	public boolean test() throws Exception {
		if (_editorIsActive(_name) == _active) {
			return true;
		}

		return false;
	}

	private boolean _editorIsActive(String editorName) {
		SWTBotEditor editor = _getEditor(editorName);

		if (editor != null) {
			return UIThreadRunnable.syncExec(
				new BoolResult() {

					public Boolean run() {
						return editor.isActive();
					}

				});
		}

		return false;
	}

	private SWTBotEditor _getEditor(String name) {
		long oldTimeOut = SWTBotPreferences.TIMEOUT;

		SWTBotPreferences.TIMEOUT = 1000;

		try {
			return _bot.editorByTitle(name);
		}
		catch (WidgetNotFoundException widgetNotFoundException) {
		}
		finally {
			SWTBotPreferences.TIMEOUT = oldTimeOut;
		}

		return null;
	}

	private boolean _active;
	private SWTWorkbenchBot _bot;
	private String _name;

}