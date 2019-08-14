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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;

/**
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ToolbarButtonWithTooltip extends AbstractWidget {

	public ToolbarButtonWithTooltip(SWTBot bot, String label) {
		super(bot, label);
	}

	public ToolbarButtonWithTooltip(SWTBot bot, String label, int index) {
		super(bot, label, index);
	}

	public void click() {
		getWidget().click();
	}

	@Override
	protected SWTBotToolbarButton getWidget() {
		assert !isLabelNull();

		if (hasIndex()) {
			return bot.toolbarButtonWithTooltip(label, index);
		}

		return bot.toolbarButtonWithTooltip(label);
	}

}