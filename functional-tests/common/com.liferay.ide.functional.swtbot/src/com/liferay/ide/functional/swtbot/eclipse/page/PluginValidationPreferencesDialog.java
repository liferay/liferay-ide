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

import com.liferay.ide.functional.swtbot.page.ComboBox;
import com.liferay.ide.functional.swtbot.page.Dialog;
import com.liferay.ide.functional.swtbot.page.Label;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLink;

/**
 * @author Rui Wang
 */
public class PluginValidationPreferencesDialog extends Dialog {

	public PluginValidationPreferencesDialog(SWTBot bot) {
		super(bot);
	}

	public boolean getConfigureProjectSpecificSettings(String link) {
		SWTBotLink configureProjectSpecificSetting = bot.link(CONFIGURE_PROJECT_SPECIFIC_SETTINGS);

		return configureProjectSpecificSetting.isEnabled();
	}

	public ComboBox getHierarchyOfTypeClassOrInterfaceIncorrect() {
		return new ComboBox(bot, "Hierarchy of type (class or interface) incorrect");
	}

	public Label getLiferayDisplayXmlDescriptor() {
		return new Label(bot, LIFERAY_DISPLAY_XML_DESCRIPTOR);
	}

	public Label getLiferayHookXmlDescriptor() {
		return new Label(bot, LIFERAY_HOOK_XML_DESCRIPTOR);
	}

	public Label getLiferayJspFiles() {
		return new Label(bot, LIFERAY_JSP_FILES);
	}

	public Label getLiferayLayoutTemplatesDescriptor() {
		return new Label(bot, LIFERAY_LAYOUT_TEMPLATES_DESCIPTOR);
	}

	public Label getLiferayPortletXmlDescriptor() {
		return new Label(bot, LIFERAY_PORTLET_XML_DESCIPTOR);
	}

	public Label getPortletXmlDescriptor() {
		return new Label(bot, PORTLET_XML_DESCRIPTOR);
	}

	public ComboBox getReferenceToXmlElementNotFound() {
		return new ComboBox(bot, "Reference to XML element not found");
	}

	public ComboBox getResourceNotFound() {
		return new ComboBox(bot, "Resource not found");
	}

	public Label getSelectTheSeverityLevelForTheFollowingValidationProblems() {
		return new Label(getShell().bot(), SELECT_THE_SEVERITY_LEVEL_FOR_THE_FOLLOWING_VALIDATION_PROBLEMS);
	}

	public Label getServiceXmlDescriptor() {
		return new Label(bot, SERVICE_XML_DESCIPTOR);
	}

	public ComboBox getSyntaxInvalid() {
		return new ComboBox(bot, "Syntax invalid");
	}

	public ComboBox getTypeClassOrInterfaceNotFound() {
		return new ComboBox(bot, "Type (class or interface) not found");
	}

}