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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCanvas;

/**
 * @author Terry Jia
 */
public class Canvas extends AbstractWidget {

	/**
	 * This label is not the "label" of view, the value may be found by debug mode.
	 */
	public Canvas(SWTBot bot, String label) {
		super(bot, label);
	}

	public void click(int x, int y) {
		getWidget().click(x, y);
	}

	@Override
	protected SWTBotCanvas getWidget() {
		if (index < 0) {
			index = _findIndex(label);
		}

		assert index > -1;

		return bot.canvas(index);
	}

	private int _findIndex(String canvasName) {
		for (int i = 0; i < 100; i++) {
			SWTBotCanvas botCanvas = bot.canvas(i);

			org.eclipse.swt.widgets.Canvas canvas = botCanvas.widget;

			String canvasString = canvas.toString();

			if (canvasString.contains(canvasName)) {
				return i;
			}
		}

		return -1;
	}

}