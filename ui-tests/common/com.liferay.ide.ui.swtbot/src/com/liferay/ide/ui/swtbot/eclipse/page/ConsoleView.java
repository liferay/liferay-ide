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

package com.liferay.ide.ui.swtbot.eclipse.page;

import com.liferay.ide.ui.swtbot.page.StyledText;
import com.liferay.ide.ui.swtbot.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class ConsoleView extends View {

	public ConsoleView(SWTWorkbenchBot bot) {
		super(bot, CONSOLE);
	}

	public void clickRomoveGradleConsoleBtn() {
		clickToolbarButton(REMOVE_GRADLE_CONSOLE);
	}

	public void clickClearConsoleBtn() {
		clickToolbarButton(CLEAR_CONSOLE);
	}

	public StyledText getLog() {
		return new StyledText(getPart().bot());
	}

}