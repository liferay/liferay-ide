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

package com.liferay.ide.functional.swtbot.eclipse.page;

import com.liferay.ide.functional.swtbot.page.CLabel;
import com.liferay.ide.functional.swtbot.page.CheckBox;
import com.liferay.ide.functional.swtbot.page.Dialog;
import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.TextInGroup;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Rui Wang
 */
public class MavenPreferencesDialog extends Dialog {

	public MavenPreferencesDialog(SWTBot bot) {
		super(bot);
	}

	public CheckBox getAddPluginTypeSuffixForMavenProjectContextRoot() {
		return new CheckBox(getShell().bot(), "Add plugin type suffix for maven project context root");
	}

	public TextInGroup getDefaultArchetypesForNewLiferayPluginProjectWizard() {
		return new TextInGroup(bot, "Default Archetypes for New Liferay Plugin Project Wizard", 0);
	}

	public CheckBox getDisableCustomJspValidationChecking() {
		return new CheckBox(getShell().bot(), "Disable custom jsp validation checking");
	}

	public Text getExt() {
		return new Text(getShell().bot(), EXT);
	}

	public Text getHook() {
		return new Text(getShell().bot(), HOOK);
	}

	public Text getLayoutTemplate() {
		return new Text(getShell().bot(), "Layout Template");
	}

	public CLabel getMaven() {
		return new CLabel(bot, MAVEN);
	}

	public Text getPortlet() {
		return new Text(getShell().bot(), "Portlet");
	}

	public Text getPortletICEfaces() {
		return new Text(getShell().bot(), "Portlet ICEFaces");
	}

	public Text getPortletJsf() {
		return new Text(getShell().bot(), "Portlet JSF");
	}

	public Text getPortletLiferayFacesAlloy() {
		return new Text(getShell().bot(), "Portlet Liferay Faces Alloy");
	}

	public Text getPortletPrimefaces() {
		return new Text(getShell().bot(), "Portlet PrimeFaces");
	}

	public Text getPortletRichFaces() {
		return new Text(getShell().bot(), "Portlet RichFaces");
	}

	public Text getPortletSpringMVC() {
		return new Text(getShell().bot(), "Portlet Spring MVC");
	}

	public Text getPortletVaadin() {
		return new Text(getShell().bot(), "Portlet Vaadin");
	}

	public Text getServiceBuilder() {
		return new Text(getShell().bot(), "ServiceBuilder");
	}

	public Text getTheme() {
		return new Text(getShell().bot(), "Theme");
	}

	public Text getWeb() {
		return new Text(getShell().bot(), WEB);
	}

}