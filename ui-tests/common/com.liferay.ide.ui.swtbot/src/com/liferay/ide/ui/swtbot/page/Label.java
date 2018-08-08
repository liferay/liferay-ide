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

package com.liferay.ide.ui.swtbot.page;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class Label extends AbstractWidget {

	public Label(SWTBot bot, String label) {
		super(bot, label);
	}

	public boolean isVisible(String label) {
		long oldTimeOut = SWTBotPreferences.TIMEOUT;

		SWTBotPreferences.TIMEOUT = 1000;

		try {
			return getWidget().isVisible();
		}
		catch (Exception e) {
			if (label.contains(e.getMessage())) {
				return false;
			}
			else {
				throw e;
			}
		}
		finally {
			SWTBotPreferences.TIMEOUT = oldTimeOut;
		}
	}

	@Override
	protected SWTBotLabel getWidget() {
		return bot.label(label);
	}

}