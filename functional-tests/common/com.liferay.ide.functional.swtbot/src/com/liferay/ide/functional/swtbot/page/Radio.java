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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRadio;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Li Lu
 */
public class Radio extends AbstractWidget {

	public Radio(SWTBot bot, int index) {
		super(bot, index);
	}

	public Radio(SWTBot bot, String label) {
		super(bot, label);
	}

	public Radio(SWTBot bot, String label, int index) {
		super(bot, label, index);
	}

	public void click() {
		getWidget().click();
	}

	public boolean isSelected() {
		return getWidget().isSelected();
	}

	@Override
	protected SWTBotRadio getWidget() {
		if (isLabelNull()) {
			return bot.radio(index);
		}

		return bot.radio(label);
	}

}