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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCanvas;

/**
 * @author Terry Jia
 */
public class Canvas extends AbstractWidget {

	public Canvas(SWTWorkbenchBot bot, int index) {
		super(bot, index);
	}

	public void click(int x, int y) {
		getWidget().click(x, y);
	}

	@Override
	protected SWTBotCanvas getWidget() {
		return bot.canvas(index);
	}

}