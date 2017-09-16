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

import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ying Xu
 */
public class NewLiferayModuleInfoWizard extends Wizard {

	public NewLiferayModuleInfoWizard(SWTWorkbenchBot bot) {
		super(bot, 2);

		_componentClassName = new Text(bot, COMPONENT_CLASS_NAME);
		_packageName = new Text(bot, PACKAGE_NAME);
		_serviceName = new Text(bot, SERVICE_NAME);
		_properties = new Table(bot, PROPERTIES);
		_browseBtn = new ToolbarButtonWithTooltip(bot, BROWSE);
		_addPropertyKeyBtn = new ToolbarButtonWithTooltip(bot, ADD_PROPERTY_KEY);
		_moveUpBtn = new ToolbarButtonWithTooltip(bot, MOVE_UP);
		_moveDownBtn = new ToolbarButtonWithTooltip(bot, MOVE_DOWN);
		_deleteBtn = new ToolbarButtonWithTooltip(bot, DELETE);
	}

	public ToolbarButtonWithTooltip getAddPropertyKeyBtn() {
		return _addPropertyKeyBtn;
	}

	public ToolbarButtonWithTooltip getBrowseBtn() {
		return _browseBtn;
	}

	public Text getComponentClassName() {
		return _componentClassName;
	}

	public ToolbarButtonWithTooltip getDeleteBtn() {
		return _deleteBtn;
	}

	public ToolbarButtonWithTooltip getMoveDownBtn() {
		return _moveDownBtn;
	}

	public ToolbarButtonWithTooltip getMoveUpBtn() {
		return _moveUpBtn;
	}

	public Text getPackageName() {
		return _packageName;
	}

	public Table getProperties() {
		return _properties;
	}

	public Text getServiceName() {
		return _serviceName;
	}

	private ToolbarButtonWithTooltip _addPropertyKeyBtn;
	private ToolbarButtonWithTooltip _browseBtn;
	private Text _componentClassName;
	private ToolbarButtonWithTooltip _deleteBtn;
	private ToolbarButtonWithTooltip _moveDownBtn;
	private ToolbarButtonWithTooltip _moveUpBtn;
	private Text _packageName;
	private Table _properties;
	private Text _serviceName;

}