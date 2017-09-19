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

import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ashley Yuan
 * @author Li Lu
 */
public class SelectPortletFrameworkWizard extends Wizard {

	public SelectPortletFrameworkWizard(SWTWorkbenchBot bot) {
		super(bot);

		_liferayMvc = new Radio(bot, LIFERAY_MVC);
		_jsf = new Radio(bot, JSF_2_X);
		_springMvc = new Radio(bot, SPRING_MVC);
		_vaadin = new Radio(bot, VAADIN);
		_portletName = new Text(bot, PORTLET_NAME);
		_displayName = new Text(bot, DISPLAY_NAME);
	}

	public Text getDisplayName() {
		return _displayName;
	}

	public Radio getJsf() {
		return _jsf;
	}

	public Radio getLiferayMvc() {
		return _liferayMvc;
	}

	public Text getPortletName() {
		return _portletName;
	}

	public Radio getSpringMvc() {
		return _springMvc;
	}

	public Radio getVaadin() {
		return _vaadin;
	}

	private Text _displayName;
	private Radio _jsf;
	private Radio _liferayMvc;
	private Text _portletName;
	private Radio _springMvc;
	private Radio _vaadin;

}