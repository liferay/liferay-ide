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

import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.ui.swtbot.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Rui Wang
 */
public class ProgressView extends View {

	public ProgressView(SWTWorkbenchBot bot) {
		super(bot, PROGRESS);
	}

	public ToolbarButtonWithTooltip cancelOperationBtn() {
		return new ToolbarButtonWithTooltip(getPart().bot(), CANCEL_OPERATION);
	}

	public void clickCancelOperationBtn() {
		cancelOperationBtn().click();
	}

	public Text getNoOperation() {
		return new Text(getPart().bot());
	}

}