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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class Menu extends AbstractWidget {

	public Menu(SWTBot bot, String label) {
		super(bot, label);
	}

	public Menu(SWTBot bot, String[] labels) {
		super(bot);

		_labels = labels;
	}

	public void click() {
		getWidget().click();
	}

	public void clickMenu(String... menus) {
		SWTBotMenu menu = getWidget();

		for (String menuItemLabel : menus) {
			SWTBotMenu menuLabel = menu.menu(menuItemLabel);

			menu = menuLabel.click();
		}
	}

	@Override
	protected SWTBotMenu getWidget() {
		if (!isLabelNull()) {
			return bot.menu(label);
		}

		SWTBotMenu menu = bot.menu(_labels[0]);

		for (int i = 1; i < _labels.length; i++) {
			menu = menu.menu(_labels[i]);
		}

		return menu;
	}

	private String[] _labels;

}