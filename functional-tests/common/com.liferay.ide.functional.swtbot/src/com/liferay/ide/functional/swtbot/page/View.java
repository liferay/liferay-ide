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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.ui.IViewReference;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class View extends AbstractPart {

	public View(SWTWorkbenchBot bot) {
		super(bot);
	}

	public View(SWTWorkbenchBot bot, String label) {
		super(bot, label);
	}

	public void clickToolbarButton(String btnLabel) {
		SWTBotToolbarButton swtBotToolbarButton = toolbarBtn(btnLabel);

		swtBotToolbarButton.click();
	}

	public void clickToolBarWithTooltipButton(String btnLabel) {
		SWTBotToolbarButton swtBotToolbarButton = toolbarBtn(btnLabel);

		swtBotToolbarButton.click();
	}

	public String getLabel() {
		return label;
	}

	public IViewReference getReference() {
		return getPart().getReference();
	}

	public SWTBotToolbarButton toolbarBtn(String btnLabel) {
		SWTBotView swtBotView = getPart();

		return swtBotView.toolbarButton(btnLabel);
	}

	protected SWTBotView getPart() {
		SWTWorkbenchBot swtBot = (SWTWorkbenchBot)bot;

		if (isLabelNull()) {
			swtBot.activeView();
		}

		//SWTBotView testView = ((SWTWorkbenchBot)bot).viewByTitle(label);

		return swtBot.viewByPartName(label);
	}

}