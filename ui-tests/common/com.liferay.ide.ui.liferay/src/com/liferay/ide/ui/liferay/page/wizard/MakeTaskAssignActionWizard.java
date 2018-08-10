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

package com.liferay.ide.ui.liferay.page.wizard;

import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.ui.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Haoyi Sun
 */
public class MakeTaskAssignActionWizard extends Wizard {

	public MakeTaskAssignActionWizard(SWTBot bot) {
		super(bot);
	}

	public void clickAddResourceActionBtn() {
		getAddResourceActionBtn().click();
	}

	public ToolbarButtonWithTooltip getAddResourceActionBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), ADD_RESOURCE_ACTION);
	}

	public ToolbarButtonWithTooltip getDeleteRoleBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), DELETE);
	}

	public Text getResourceAction() {
		return new Text(getShell().bot(), 0);
	}

	public void setResourceAction(String resourceAction) {
		getResourceAction().setText(resourceAction);
	}

}