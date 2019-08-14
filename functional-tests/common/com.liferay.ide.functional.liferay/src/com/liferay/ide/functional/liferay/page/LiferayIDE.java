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

package com.liferay.ide.functional.liferay.page;

import com.liferay.ide.functional.liferay.page.button.CreateLifeayProjectToolbarDropDownButton;
import com.liferay.ide.functional.liferay.page.button.NewToolbarDropDownButton;
import com.liferay.ide.functional.swtbot.Eclipse;
import com.liferay.ide.functional.swtbot.page.Perspective;
import com.liferay.ide.functional.swtbot.page.ToolbarButtonWithTooltip;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * @author Terry Jia
 */
public class LiferayIDE extends Eclipse {

	public static LiferayIDE getInstance(SWTWorkbenchBot bot) {
		if (_ide == null) {
			_ide = new LiferayIDE(bot);
		}

		return _ide;
	}

	public CreateLifeayProjectToolbarDropDownButton getCreateLiferayProjectToolbar() {
		return _createLiferayProjectToolbar;
	}

	public Perspective getKaleoDesignerPerspective() {
		return _kaleoDesignerPerspective;
	}

	public Perspective getLiferayPerspective() {
		return _liferayPerspective;
	}

	public Perspective getLiferayWorkspacePerspective() {
		return _liferayWorkspacePerspective;
	}

	public NewToolbarDropDownButton getNewBtn() {
		return _newBtn;
	}

	public ToolbarButtonWithTooltip getNewUpgradePlan() {
		return _newUpgradePlan;
	}

	public Perspective getUpgradePlannerPerspective() {
		return _upgradePlannerPerspective;
	}

	private LiferayIDE(SWTWorkbenchBot bot) {
		super(bot);

		_createLiferayProjectToolbar = new CreateLifeayProjectToolbarDropDownButton(bot);
		_kaleoDesignerPerspective = new Perspective(bot, KALEO_DESIGNER);
		_liferayPerspective = new Perspective(bot, LIFERAY_PLUGINS);
		_liferayWorkspacePerspective = new Perspective(bot, LIFERAY_WORKSPACE);
		_upgradePlannerPerspective = new Perspective(bot, LIFERAY_UPGRADE_PLANNER);
		_newBtn = new NewToolbarDropDownButton(bot);
		_newUpgradePlan = new ToolbarButtonWithTooltip(_getShell().bot(), NEW_UPGRADE_PLAN);
	}

	private SWTBotShell _getShell() {
		return bot.shell(getLabel());
	}

	private static LiferayIDE _ide;

	private CreateLifeayProjectToolbarDropDownButton _createLiferayProjectToolbar;
	private Perspective _kaleoDesignerPerspective;
	private Perspective _liferayPerspective;
	private Perspective _liferayWorkspacePerspective;
	private NewToolbarDropDownButton _newBtn;
	private ToolbarButtonWithTooltip _newUpgradePlan;
	private Perspective _upgradePlannerPerspective;

}