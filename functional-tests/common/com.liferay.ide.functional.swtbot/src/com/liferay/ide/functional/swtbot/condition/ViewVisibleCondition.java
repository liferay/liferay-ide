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

import org.eclipse.swt.widgets.Control;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
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
public class ViewVisibleCondition implements ICondition {

	public ViewVisibleCondition(String viewIdentifier, boolean visible, boolean id) {
		_identifier = viewIdentifier;

		_visible = visible;
		_id = id;
	}

	public String getFailureMessage() {
		if (_visible) {
			return "wait for view " + _identifier + " is visible failed"; //$NON-NLS-1$
		}

		return "wait for view " + _identifier + " is not visible failed"; //$NON-NLS-1$
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
		if (_viewIsVisible() == _visible) {
			return true;
		}

		return false;
	}

	private SWTBotView _getView() {
		long oldTimeOut = SWTBotPreferences.TIMEOUT;

		SWTBotPreferences.TIMEOUT = 1000;

		SWTBotView view = null;

		try {
			if (_id) {
				view = _bot.viewById(_identifier);
			}
			else {
				view = _bot.viewByTitle(_identifier);
			}
		}
		catch (WidgetNotFoundException wnfe) {
		}
		finally {
			SWTBotPreferences.TIMEOUT = oldTimeOut;
		}

		return view;
	}

	private boolean _viewIsVisible() {
		SWTBotView view = _getView();

		if (view != null) {
			return UIThreadRunnable.syncExec(
				new BoolResult() {

					public Boolean run() {
						if (view.getWidget() instanceof Control) {
							Control controlWidget = (Control)view.getWidget();

							return controlWidget.isVisible();
						}

						return false;
					}

				});
		}

		return false;
	}

	private SWTWorkbenchBot _bot;
	private boolean _id;
	private String _identifier;
	private boolean _visible;

}