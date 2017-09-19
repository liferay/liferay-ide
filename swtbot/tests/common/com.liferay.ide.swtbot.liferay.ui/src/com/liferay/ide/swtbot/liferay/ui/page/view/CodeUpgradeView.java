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

package com.liferay.ide.swtbot.liferay.ui.page.view;

import com.liferay.ide.swtbot.ui.page.Canvas;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class CodeUpgradeView extends View {

	public CodeUpgradeView(SWTWorkbenchBot bot, String viewIdentifier) {
		super(bot, viewIdentifier);

		_gear = new GearPO(bot, 3);
		_navigator = new Canvas(bot, 4);

		_showAllPagesBtn = new ToolbarButtonWithTooltip(bot, SHOW_ALL_PAGES);
		_restartBtn = new ToolbarButtonWithTooltip(bot, RESTART_UPGRADE);
		_restartDialog = new Dialog(bot, RESTART_CODE_UPGRADE, NO, YES);
		_showAllPagesDialog = new Dialog(bot, SHOW_ALL_PAGES, NO, YES);
	}

	public GearPO getGear() {
		return _gear;
	}

	public Canvas getNavigator() {
		return _navigator;
	}

	public void restartWithConfirm() {
		_restartBtn.click();
		_restartDialog.confirm();
	}

	public void showAllPagesWithConfirm() {
		_showAllPagesBtn.click();
		_showAllPagesDialog.confirm();
	}

	public class GearPO extends Canvas {

		public GearPO(SWTWorkbenchBot bot, int index) {
			super(bot, index);
		}

		public void clickGear(int i) {
			click(_x + _step * i, _y);

			sleep();
		}

		private int _step = 64;
		private int _x = 52;
		private int _y = 52;

	}

	private GearPO _gear;
	private Canvas _navigator;
	private ToolbarButtonWithTooltip _restartBtn;
	private Dialog _restartDialog;
	private ToolbarButtonWithTooltip _showAllPagesBtn;
	private Dialog _showAllPagesDialog;

}