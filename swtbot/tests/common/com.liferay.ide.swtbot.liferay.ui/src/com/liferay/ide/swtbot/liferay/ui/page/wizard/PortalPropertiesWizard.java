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

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 */
public class PortalPropertiesWizard extends Wizard {

	public PortalPropertiesWizard(SWTWorkbenchBot bot) {
		super(bot, 1);

		_portalPropertiesFile = new Text(bot, PORTAL_PROPERTIES_FILE);
		_defineActionsOnPortalEvents = new Table(bot, DEFINE_ACTIONS_ON_PORTAL_EVENTS);
		_specifyProperties = new Table(bot, SPECIFY_PROPERTIES_TO_OVERRIDE);
		_addEventActionDialog = new Dialog(bot, ADD_EVENT_ACTION);
		_addPropertyOverrideDialog = new Dialog(bot, ADD_PROPERTY_OVERRIDE);
		_newLiferayHookConfigurationDialog = new Dialog(
			bot, NEW_LIFERAY_HOOK, BACK_WITH_LEFT_BRACKET, NEXT_WITH_BRACKET);

		_browseBtn = new Button(bot, BROWSE_WITH_DOT, 0);
		_eventAddBtn = new Button(bot, ADD_WITH_DOT, 0);
		_eventEditBtn = new Button(bot, EDIT_WITH_DOT, 0);
		_eventRemoveBtn = new Button(bot, REMOVE_WITH_DOT, 0);
		_propertyAddBtn = new Button(bot, ADD_WITH_DOT, 1);
		_propertyEditBtn = new Button(bot, EDIT_WITH_DOT, 1);
		_propertyRemoveBtn = new Button(bot, REMOVE_WITH_DOT, 1);
	}

	public Dialog getAddEventActionDialog() {
		return _addEventActionDialog;
	}

	public Dialog getAddPropertyOverrideDialog() {
		return _addPropertyOverrideDialog;
	}

	public Button getBrowseBtn() {
		return _browseBtn;
	}

	public Table getDefineActionsOnPortalEvents() {
		return _defineActionsOnPortalEvents;
	}

	public Button getEventAddBtn() {
		return _eventAddBtn;
	}

	public Button getEventEditBtn() {
		return _eventEditBtn;
	}

	public Button getEventRemoveBtn() {
		return _eventRemoveBtn;
	}

	public Dialog getNewLiferayHookConfigurationDialog() {
		return _newLiferayHookConfigurationDialog;
	}

	public Text getPortalPropertiesFile() {
		return _portalPropertiesFile;
	}

	public Button getPropertyAddBtn() {
		return _propertyAddBtn;
	}

	public Button getPropertyEditBtn() {
		return _propertyEditBtn;
	}

	public Button getPropertyRemoveBtn() {
		return _propertyRemoveBtn;
	}

	public Table getSpecifyProperties() {
		return _specifyProperties;
	}

	private Dialog _addEventActionDialog;
	private Dialog _addPropertyOverrideDialog;
	private Button _browseBtn;
	private Table _defineActionsOnPortalEvents;
	private Button _eventAddBtn;
	private Button _eventEditBtn;
	private Button _eventRemoveBtn;
	private Dialog _newLiferayHookConfigurationDialog;
	private Text _portalPropertiesFile;
	private Button _propertyAddBtn;
	private Button _propertyEditBtn;
	private Button _propertyRemoveBtn;
	private Table _specifyProperties;

}