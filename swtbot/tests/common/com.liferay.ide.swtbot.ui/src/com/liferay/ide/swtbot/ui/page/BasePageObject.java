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

package com.liferay.ide.swtbot.ui.page;

import com.liferay.ide.swtbot.ui.UI;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.apache.log4j.Logger;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class BasePageObject implements UI {

	public BasePageObject(SWTWorkbenchBot bot) {
		this.bot = bot;

		log = Logger.getLogger(getClass());
	}

	public BasePageObject(SWTWorkbenchBot bot, int index) {
		this(bot);

		this.index = index;
	}

	public BasePageObject(SWTWorkbenchBot bot, String label) {
		this(bot);

		this.label = label;
	}

	public BasePageObject(SWTWorkbenchBot bot, String label, int index) {
		this(bot);

		this.label = label;
		this.index = index;
	}

	public void sleep() {
		sleep(_DEFAULT_SLEEP_MILLIS);
	}

	public void sleep(long millis) {
		bot.sleep(millis);
	}

	protected boolean hasIndex() {
		if (index >= 0) {
			return true;
		}

		return false;
	}

	protected boolean isLabelNull() {
		return label.equals(StringPool.BLANK);
	}

	protected SWTWorkbenchBot bot;
	protected int index = -1;
	protected String label = "";
	protected Logger log;

	private static final long _DEFAULT_SLEEP_MILLIS = 1000;

}