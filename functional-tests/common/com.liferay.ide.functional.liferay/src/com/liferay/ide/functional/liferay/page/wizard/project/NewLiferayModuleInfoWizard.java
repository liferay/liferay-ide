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

import com.liferay.ide.functional.swtbot.page.Table;
import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.functional.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ying Xu
 */
public class NewLiferayModuleInfoWizard extends Wizard {

	public NewLiferayModuleInfoWizard(SWTBot bot) {
		super(bot, 2);
	}

	public void clickAddPropertyKeyBtn() {
		getAddPropertyKeyBtn().click();
	}

	public void clickBrowseBtn() {
		getBrowseBtn().click();
	}

	public void clickDeleteBtn() {
		getDeleteBtn().click();
	}

	public ToolbarButtonWithTooltip getAddPropertyKeyBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), ADD_PROPERTY_KEY);
	}

	public ToolbarButtonWithTooltip getBrowseBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), BROWSE);
	}

	public Text getComponentClassName() {
		return new Text(getShell().bot(), COMPONENT_CLASS_NAME);
	}

	public ToolbarButtonWithTooltip getDeleteBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), DELETE);
	}

	public ToolbarButtonWithTooltip getMoveDownBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), MOVE_DOWN);
	}

	public ToolbarButtonWithTooltip getMoveUpBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), MOVE_UP);
	}

	public Text getPackageName() {
		return new Text(getShell().bot(), PACKAGE_NAME);
	}

	public Table getProperties() {
		return new Table(getShell().bot(), PROPERTIES);
	}

	public Text getServiceName() {
		return new Text(getShell().bot(), SERVICE_NAME);
	}

}