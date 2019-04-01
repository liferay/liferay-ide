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

package com.liferay.ide.ui.liferay.page.button;

import com.liferay.ide.ui.swtbot.page.MenuItem;
import com.liferay.ide.ui.swtbot.page.ToolbarDropDownButton;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 * @author Vicky Wang
 * @author Ying Xu
 * @author Li Lu
 */
public class CreateLifeayProjectToolbarDropDownButton extends ToolbarDropDownButton {

	public CreateLifeayProjectToolbarDropDownButton(SWTBot bot) {
		super(bot, CREATE_A_NEW_LIFERAY_MODULE_PROJECT);

		_newLiferayPlugin = new MenuItem(bot, this, NEW_LIFERAY_PLUGIN_PROJECT);
		_newLiferayPluginProjectFromExistingSource = new MenuItem(
			bot, this, NEW_LIFERAY_PLUGIN_PROJECTS_FROM_EXISING_SOURCE);

		_newLiferayPortlet = new MenuItem(bot, this, NEW_LIFERAY_PORTLET);
		_newLiferayHookConfiguration = new MenuItem(bot, this, NEW_LIFERAY_HOOK_CONFIGURATION);
		_newLiferayServer = new MenuItem(bot, this, NEW_LIFERAY_SERVER);
		_newLiferayServiceBuilder = new MenuItem(bot, this, NEW_LIFERAY_SERVICE_BUILDER);
		_newLiferayJSFPortlet = new MenuItem(bot, this, NEW_LIFERAY_JSF_PORTLET);
		_newLiferayVaadinPortlet = new MenuItem(bot, this, NEW_LIFERAY_VAADIN_PORTLET);
		_newLiferayLayouTemplate = new MenuItem(bot, this, NEW_LIFERAY_LAYOUT_TMEPLATE);
		_newLiferayModuleFragment = new MenuItem(bot, this, NEW_LIFERAY_MODULE_PROJECT_FRAGMENT);
		_newLiferayWorkspaceProject = new MenuItem(bot, this, NEW_LIFERAY_WORPSPACE_PROJECT);
		_newLiferayComponentClass = new MenuItem(bot, this, NEW_LIFERAY_COMPONENT_CLASS);
		_newLiferayModule = new MenuItem(bot, this, NEW_LIFERAY_MODULE_PROJECT);
		_newLiferayJSFProject = new MenuItem(bot, this, NEW_LIFERAY_JSF_PROJECT);
		_newLiferayKaleoWorkflow = new MenuItem(bot, this, NEW_LIFERAY_KALEO_WORKFLOW);
		_newLiferayModulesExt = new MenuItem(bot, this, NEW_LIFERAY_MODULE_EXT_PROJECT);
	}

	public MenuItem getNewLiferayComponentClass() {
		return _newLiferayComponentClass;
	}

	public MenuItem getNewLiferayHookConfiguration() {
		return _newLiferayHookConfiguration;
	}

	public MenuItem getNewLiferayJSFPortlet() {
		return _newLiferayJSFPortlet;
	}

	public MenuItem getNewLiferayJSFProject() {
		return _newLiferayJSFProject;
	}

	public MenuItem getNewLiferayKaleoWorkflow() {
		return _newLiferayKaleoWorkflow;
	}

	public MenuItem getNewLiferayLayoutTemplate() {
		return _newLiferayLayouTemplate;
	}

	public MenuItem getNewLiferayModule() {
		return _newLiferayModule;
	}

	public MenuItem getNewLiferayModuleFragment() {
		return _newLiferayModuleFragment;
	}

	public MenuItem getNewLiferayModulesExt() {
		return _newLiferayModulesExt;
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

	public MenuItem getNewLiferayWorkspaceProject() {
		return _newLiferayWorkspaceProject;
	}

	private MenuItem _newLiferayComponentClass;
	private MenuItem _newLiferayHookConfiguration;
	private MenuItem _newLiferayJSFPortlet;
	private MenuItem _newLiferayJSFProject;
	private MenuItem _newLiferayKaleoWorkflow;
	private MenuItem _newLiferayLayouTemplate;
	private MenuItem _newLiferayModule;
	private MenuItem _newLiferayModuleFragment;
	private MenuItem _newLiferayModulesExt;
	private MenuItem _newLiferayPlugin;
	private MenuItem _newLiferayPluginProjectFromExistingSource;
	private MenuItem _newLiferayPortlet;
	private MenuItem _newLiferayServer;
	private MenuItem _newLiferayServiceBuilder;
	private MenuItem _newLiferayVaadinPortlet;
	private MenuItem _newLiferayWorkspaceProject;

}