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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class Text extends AbstractWidget {

	public Text(SWTWorkbenchBot bot) {
		super(bot);
	}

	public Text(SWTWorkbenchBot bot, int index) {
		super(bot, index);
	}

	public Text(SWTWorkbenchBot bot, String label) {
		super(bot, label);
	}

	public Text(SWTWorkbenchBot bot, String label, int index) {
		super(bot, label, index);
	}

	public void setText(String text) {
		getWidget().setText(text);
	}

	@Override
	protected SWTBotText getWidget() {
		if (!isLabelNull() && hasIndex()) {
			return bot.text(label, index);
		}
		else if (isLabelNull() && hasIndex()) {
			return bot.text(index);
		}
		else if (!isLabelNull() && !hasIndex()) {
			return bot.textWithLabel(label);
		}
		else {
			return bot.text();
		}
	}

}