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

import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Li Lu
 */
public class ChoosePortletFrameworkWizard extends Wizard {

	public ChoosePortletFrameworkWizard(SWTWorkbenchBot bot) {
		super(bot, 3);

		_liferayMvc = new Radio(bot, LIFERAY_MVC);
		_jsf2x = new Radio(bot, JSF_2_X);
		_springMvc = new Radio(bot, SPRING_MVC);
		_vaadin = new Radio(bot, VAADIN);
		_displayName = new Text(bot, DISPLAY_NAME);
		_archetype = new Text(bot, ARCHETYPE);
	}

	public Text getArchetypeText() {
		return _archetype;
	}

	public Text getDisplayNameText() {
		return _displayName;
	}

	public void selectPortletFramework(String label) {
		switch (label) {
			case LIFERAY_MVC:

				_liferayMvc.click();
				break;
			case JSF_2_X:
				_jsf2x.click();
				break;

			case SPRING_MVC:
				_springMvc.click();
				break;
			case VAADIN:
				_vaadin.click();
		}
	}

	private Text _archetype;
	private Text _displayName;
	private Radio _jsf2x;
	private Radio _liferayMvc;
	private Radio _springMvc;
	private Radio _vaadin;

}