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

import com.liferay.ide.ui.swtbot.page.ComboBox;
import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.ui.swtbot.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ying Xu
 */
public class NewLiferayComponentWizard extends Wizard {

	public NewLiferayComponentWizard(SWTBot bot) {
		super(bot, 4);
	}

	public void clickBrowseBtn() {
		getBrowseBtn().click();
	}

	public void clickPackageBrowseBtn() {
		getPackageBrowseBtn().click();
	}

	public ToolbarButtonWithTooltip getBrowseBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), BROWSE, 1);
	}

	public Text getComponentClassName() {
		return new Text(getShell().bot(), COMPONENT_CLASS_NAME);
	}

	public ComboBox getComponentClassTemplates() {
		return new ComboBox(getShell().bot(), COMPONENT_CLASS_TEMPLATE);
	}

	public Text getModelClassName() {
		return new Text(getShell().bot(), MODEL_CLASS);
	}

	public ToolbarButtonWithTooltip getPackageBrowseBtn() {
		return new ToolbarButtonWithTooltip(getShell().bot(), BROWSE);
	}

	public Text getPackageName() {
		return new Text(getShell().bot(), PACKAGE_NAME);
	}

	public ComboBox getProjectNames() {
		return new ComboBox(getShell().bot(), PROJECT_NAME);
	}

	public Text getServiceName() {
		return new Text(getShell().bot(), SERVICE_NAME);
	}

	public void setComponentClassTemplates(String template) {
		getComponentClassTemplates().setSelection(template);
	}

	public void setModelClassName(String className) {
		getModelClassName().setText(className);
	}

	public void setServiceName(String serviceName) {
		getServiceName().setText(serviceName);
	}

}