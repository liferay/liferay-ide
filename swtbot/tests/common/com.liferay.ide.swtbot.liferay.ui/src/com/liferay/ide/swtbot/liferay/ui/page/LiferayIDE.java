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

package com.liferay.ide.swtbot.liferay.ui.page;

import com.liferay.ide.swtbot.liferay.ui.page.button.CreateLifeayProjectToolbarDropDownButton;
import com.liferay.ide.swtbot.liferay.ui.page.button.NewToolbarDropDownButton;
import com.liferay.ide.swtbot.liferay.ui.page.view.CodeUpgradeView;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayWorkspaceWizard;
import com.liferay.ide.swtbot.ui.Eclipse;
import com.liferay.ide.swtbot.ui.page.Perspective;
import com.liferay.ide.swtbot.ui.page.Text;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class LiferayIDE extends Eclipse {

	public LiferayIDE(SWTWorkbenchBot bot) {
		super(bot);

		_createLiferayProjectToolbar = new CreateLifeayProjectToolbarDropDownButton(bot);
		_codeUpgradeView = new CodeUpgradeView(bot, LIFERAY_CODE_UPGRADE);
		_liferayPerspective = new Perspective(bot, LIFERAY_PLUGINS);
		_liferayWorkspacePerspective = new Perspective(bot, LIFERAY_WORKSPACE);
		_newBtn = new NewToolbarDropDownButton(bot);
		_newLiferayWorkspaceProjectWizard = new NewLiferayWorkspaceWizard(bot);
	}

	public CreateLifeayProjectToolbarDropDownButton getCreateLiferayProjectToolbar() {
		return _createLiferayProjectToolbar;
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

	public NewLiferayWorkspaceWizard getNewLiferayWorkspaceProjectWizard() {
		return _newLiferayWorkspaceProjectWizard;
	}

	public CodeUpgradeView showCodeUpgradeView() {
		try {
			_codeUpgradeView.show();
		}
		catch (Exception e) {
			getOtherMenu().click();

			Text text = getShowViewDialog().getText();

			text.setText(LIFERAY_CODE_UPGRADE);

			getShowViewDialog().confirm();

			_codeUpgradeView.show();
		}

		return _codeUpgradeView;
	}

	private CodeUpgradeView _codeUpgradeView;
	private CreateLifeayProjectToolbarDropDownButton _createLiferayProjectToolbar;
	private Perspective _liferayPerspective;
	private Perspective _liferayWorkspacePerspective;
	private NewToolbarDropDownButton _newBtn;
	private NewLiferayWorkspaceWizard _newLiferayWorkspaceProjectWizard;

}