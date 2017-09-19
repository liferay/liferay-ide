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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class CheckBox extends AbstractWidget {

	public CheckBox(SWTWorkbenchBot bot) {
		super(bot);
	}

	public CheckBox(SWTWorkbenchBot bot, int index) {
		super(bot, index);
	}

	public CheckBox(SWTWorkbenchBot bot, String label) {
		super(bot, label);
	}

	public CheckBox(SWTWorkbenchBot bot, String label, int index) {
		super(bot, label, index);
	}

	public void deselect() {
		getWidget().deselect();
	}

	public boolean isChecked() {
		return getWidget().isChecked();
	}

	public void select() {
		getWidget().select();
	}

	@Override
	protected SWTBotCheckBox getWidget() {
		if (!isLabelNull() && hasIndex()) {
			return bot.checkBox(label, index);
		}
		else if (isLabelNull() && hasIndex()) {
			return bot.checkBox(index);
		}
		else if (!isLabelNull() && !hasIndex()) {
			return bot.checkBox(label);
		}
		else {
			return bot.checkBox();
		}
	}

}