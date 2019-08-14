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

package com.liferay.ide.functional.swtbot.page;

import com.liferay.ide.functional.swtbot.UI;
import com.liferay.ide.functional.swtbot.util.CoreUtil;
import com.liferay.ide.functional.swtbot.util.StringPool;

import org.apache.log4j.Logger;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public abstract class BasePageObject implements UI {

	public BasePageObject(SWTBot bot) {
		this.bot = bot;

		log = Logger.getLogger(getClass());
	}

	public BasePageObject(SWTBot bot, int index) {
		this(bot);

		this.index = index;
	}

	public BasePageObject(SWTBot bot, String label) {
		this(bot);

		this.label = label;
	}

	public BasePageObject(SWTBot bot, String label, int index) {
		this(bot);

		this.label = label;
		this.index = index;
	}

	public abstract String getLabel();

	public void sleep() {
		sleep(_DEFAULT_SLEEP_MILLIS);
	}

	public void sleep(long millis) {
		bot.sleep(millis);
	}

	public void sleepLinux(long millis) {
		if (CoreUtil.isLinux()) {
			bot.sleep(millis);
		}
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

	protected SWTBot bot;
	protected int index = -1;
	protected String label = "";
	protected Logger log;

	private static final long _DEFAULT_SLEEP_MILLIS = 1000;

}