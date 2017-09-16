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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Li Lu
 */
public class CreateVaadinPortletWizard extends CreateLiferayPortletWizard {

	public CreateVaadinPortletWizard(SWTWorkbenchBot bot) {
		super(bot, 3);

		_applicationClass = new Text(bot, APPLICATION_CLASS);
		_vaadinPortletClasses = new ComboBox(bot, PORTLET_CLASS);
		_portletClasses = new ComboBox(bot, PORTLET_CLASS);
	}

	public Text getApplicationClass() {
		return _applicationClass;
	}

	public ComboBox getPortletClasses() {
		return _portletClasses;
	}

	public ComboBox getVaadinPortletClasses() {
		return _vaadinPortletClasses;
	}

	private Text _applicationClass;
	private ComboBox _portletClasses;
	private ComboBox _vaadinPortletClasses;

}