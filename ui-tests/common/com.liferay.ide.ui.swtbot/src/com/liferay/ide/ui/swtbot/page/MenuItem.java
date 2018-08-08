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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;

/**
 * @author Terry Jia
 */
public class MenuItem extends AbstractWidget {

	public MenuItem(SWTBot bot, ToolbarDropDownButton dropDownBtn, String label) {
		super(bot, label);

		_dropDownBtn = dropDownBtn;
	}

	public void click() {
		getWidget().click();
	}

	@Override
	protected SWTBotMenu getWidget() {
		SWTBotToolbarDropDownButton dropDownWidget = _dropDownBtn.getWidget();

		return dropDownWidget.menuItem(label);
	}

	private ToolbarDropDownButton _dropDownBtn;

}