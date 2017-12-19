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

package com.liferay.ide.ui.liferay.page;

import com.liferay.ide.ui.liferay.page.button.CreateLifeayProjectToolbarDropDownButton;
import com.liferay.ide.ui.liferay.page.button.NewToolbarDropDownButton;
import com.liferay.ide.ui.swtbot.Eclipse;
import com.liferay.ide.ui.swtbot.page.Perspective;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class LiferayIDE extends Eclipse {

	public LiferayIDE(SWTWorkbenchBot bot) {
		super(bot);

		_createLiferayProjectToolbar = new CreateLifeayProjectToolbarDropDownButton(bot);
		_kaleoDesignerPerspective = new Perspective(bot, KALEO_DESIGNER);
		_liferayPerspective = new Perspective(bot, LIFERAY_PLUGINS);
		_liferayWorkspacePerspective = new Perspective(bot, LIFERAY_WORKSPACE);
		_newBtn = new NewToolbarDropDownButton(bot);
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

	public void showCodeUpgradeView() {
		showView(LIFERAY_CODE_UPGRADE);
	}

	private CreateLifeayProjectToolbarDropDownButton _createLiferayProjectToolbar;
	private Perspective _kaleoDesignerPerspective;
	private Perspective _liferayPerspective;
	private Perspective _liferayWorkspacePerspective;
	private NewToolbarDropDownButton _newBtn;

}