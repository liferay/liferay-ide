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

package com.liferay.ide.swtbot.liferay.ui.page.wizard.project;

import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ying Xu
 */
public class NewFragmentWizard extends NewProjectWizard {

	public NewFragmentWizard(SWTWorkbenchBot bot) {
		super(bot, 2);

		_liferyRuntimes = new ComboBox(bot, LIFERAY_RUNTIME_NAME);
		_newRuntimeBtn = new ToolbarButtonWithTooltip(bot, "New Liferay Runtime...");
	}

	public ComboBox getLiferyRuntimes() {
		return _liferyRuntimes;
	}

	public ToolbarButtonWithTooltip getNewRuntimeBtn() {
		return _newRuntimeBtn;
	}

	private ComboBox _liferyRuntimes;
	private ToolbarButtonWithTooltip _newRuntimeBtn;

}