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

package com.liferay.ide.functional.liferay.page.wizard.project;

import com.liferay.ide.functional.swtbot.page.ComboBox;
import com.liferay.ide.functional.swtbot.page.ToolbarButtonWithTooltip;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ying Xu
 */
public class NewFragmentWizard extends NewProjectWizard {

	public NewFragmentWizard(SWTBot bot) {
		super(bot, 2);
	}

	public void clickNewRuntimeBtn() {
		getNewRuntimeBtn().click();
	}

	public ComboBox getLiferyRuntimes() {
		return new ComboBox(getShell().bot(), LIFERAY_RUNTIME_NAME);
	}

	public ToolbarButtonWithTooltip getNewRuntimeBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), NEW_LIFERAY_RUNTIME);
	}

}