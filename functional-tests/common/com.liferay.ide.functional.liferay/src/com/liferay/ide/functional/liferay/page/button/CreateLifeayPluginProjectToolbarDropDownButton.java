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

package com.liferay.ide.functional.liferay.page.button;

import com.liferay.ide.functional.swtbot.page.MenuItem;
import com.liferay.ide.functional.swtbot.page.ToolbarDropDownButton;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley
 */
public class CreateLifeayPluginProjectToolbarDropDownButton extends ToolbarDropDownButton {

	public CreateLifeayPluginProjectToolbarDropDownButton(SWTBot bot) {
		super(bot, CREATE_A_NEW_LIFERAY_PLUGIN_PROJECT);

		_newLiferayPlugin = new MenuItem(bot, this, NEW_LIFERAY_PLUGIN_PROJECT);
		_newLiferayPluginProjectFromExistingSource = new MenuItem(
			bot, this, NEW_LIFERAY_PLUGIN_PROJECTS_FROM_EXISING_SOURCE);

		_newLiferayHookConfiguration = new MenuItem(bot, this, NEW_LIFERAY_HOOK_CONFIGURATION);

		_newLiferayServiceBuilder = new MenuItem(bot, this, NEW_LIFERAY_SERVICE_BUILDER);
		_newLiferayLayouTemplate = new MenuItem(bot, this, NEW_LIFERAY_LAYOUT_TMEPLATE);

		_newLiferayPortlet = new MenuItem(bot, this, NEW_LIFERAY_PORTLET);
		_newLiferayJSFPortlet = new MenuItem(bot, this, NEW_LIFERAY_JSF_PORTLET);
		_newLiferayVaadinPortlet = new MenuItem(bot, this, NEW_LIFERAY_VAADIN_PORTLET);

		_newLiferayKaleoWorkflow = new MenuItem(bot, this, NEW_LIFERAY_KALEO_WORKFLOW);
		_newLiferayServer = new MenuItem(bot, this, NEW_LIFERAY_SERVER);
	}

	public MenuItem getNewLiferayHookConfiguration() {
		return _newLiferayHookConfiguration;
	}

	public MenuItem getNewLiferayJSFPortlet() {
		return _newLiferayJSFPortlet;
	}

	public MenuItem getNewLiferayKaleoWorkflow() {
		return _newLiferayKaleoWorkflow;
	}

	public MenuItem getNewLiferayLayoutTemplate() {
		return _newLiferayLayouTemplate;
	}

	public MenuItem getNewLiferayPlugin() {
		return _newLiferayPlugin;
	}

	public MenuItem getNewLiferayPluginProjectFromExistingSource() {
		return _newLiferayPluginProjectFromExistingSource;
	}

	public MenuItem getNewLiferayPortlet() {
		return _newLiferayPortlet;
	}

	public MenuItem getNewLiferayServer() {
		return _newLiferayServer;
	}

	public MenuItem getNewLiferayServiceBuilder() {
		return _newLiferayServiceBuilder;
	}

	public MenuItem getNewLiferayVaadinPortlet() {
		return _newLiferayVaadinPortlet;
	}

	private MenuItem _newLiferayHookConfiguration;
	private MenuItem _newLiferayJSFPortlet;
	private MenuItem _newLiferayKaleoWorkflow;
	private MenuItem _newLiferayLayouTemplate;
	private MenuItem _newLiferayPlugin;
	private MenuItem _newLiferayPluginProjectFromExistingSource;
	private MenuItem _newLiferayPortlet;
	private MenuItem _newLiferayServer;
	private MenuItem _newLiferayServiceBuilder;
	private MenuItem _newLiferayVaadinPortlet;

}