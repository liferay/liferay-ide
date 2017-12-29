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

package com.liferay.ide.ui.liferay.page.view;

import com.liferay.ide.ui.swtbot.page.Canvas;
import com.liferay.ide.ui.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.ui.swtbot.page.View;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class CodeUpgradeView extends View {

	public CodeUpgradeView(SWTWorkbenchBot bot) {
		super(bot, LIFERAY_CODE_UPGRADE);

		_gear = new Gear(bot);
	}

	public Gear getGear() {
		return _gear;
	}

	public ToolbarButtonWithTooltip getRestartUpgradeBtn() {
		return new ToolbarButtonWithTooltip(bot, RESTART_UPGRADE);
	}

	public ToolbarButtonWithTooltip getShowAllPagesBtn() {
		return new ToolbarButtonWithTooltip(bot, SHOW_ALL_PAGES);
	}

	public class Gear extends Canvas {

		public Gear(SWTWorkbenchBot bot) {
			super(bot, "GearControl");
		}

		public void clickGear(int i) {
			click(_x + _step * i, _y);
		}

		private int _step = 64;
		private int _x = 52;
		private int _y = 52;

	}

	private Gear _gear;

}