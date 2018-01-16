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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class View extends AbstractPart {

	public View(SWTWorkbenchBot bot, String label) {
		super(bot, label);
	}

	public void clickToolbarButton(String btnLabel) {
		toolbarBtn(btnLabel).click();
	}

	public void clickToolBarWithTooltipButton(String btnLabel) {
		toolbarBtn(btnLabel).click();
	}

	public String getLabel() {
		return label;
	}

	public SWTBotToolbarButton toolbarBtn(String btnLabel) {
		return getPart().toolbarButton(btnLabel);
	}

	protected SWTBotView getPart() {
		return ((SWTWorkbenchBot)bot).viewByTitle(label);
	}

}