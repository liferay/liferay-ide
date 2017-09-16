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
import com.liferay.ide.swtbot.ui.page.ToolbarButtonWithTooltip;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ying Xu
 */
public class NewLiferayComponentWizard extends Wizard {

	public NewLiferayComponentWizard(SWTWorkbenchBot bot) {
		this(bot, -1);
	}

	public NewLiferayComponentWizard(SWTWorkbenchBot bot, int validationMsgIndex) {
		super(bot, NEW_LIFERAY_COMPONENT, 4);

		_packageName = new Text(bot, PACKAGE_NAME);
		_componentClassName = new Text(bot, COMPONENT_CLASS_NAME);
		_serviceName = new Text(bot, SERVICE_NAME);
		_modelClassName = new Text(bot, MODEL_CLASS);
		_projectNames = new ComboBox(bot, PROJECT_NAME);
		_componentClassTemplates = new ComboBox(bot, COMPONENT_CLASS_TEMPLATE);
		_browseBtn = new ToolbarButtonWithTooltip(bot, BROWSE, 1);
		_packageBrowseBtn = new ToolbarButtonWithTooltip(bot, BROWSE);
	}

	public ToolbarButtonWithTooltip getBrowseBtn() {
		return _browseBtn;
	}

	public Text getComponentClassName() {
		return _componentClassName;
	}

	public ComboBox getComponentClassTemplates() {
		return _componentClassTemplates;
	}

	public Text getModelClassName() {
		return _modelClassName;
	}

	public ToolbarButtonWithTooltip getPackageBrowseBtn() {
		return _packageBrowseBtn;
	}

	public Text getPackageName() {
		return _packageName;
	}

	public ComboBox getProjectNames() {
		return _projectNames;
	}

	public Text getServiceName() {
		return _serviceName;
	}

	private ToolbarButtonWithTooltip _browseBtn;
	private Text _componentClassName;
	private ComboBox _componentClassTemplates;
	private Text _modelClassName;
	private ToolbarButtonWithTooltip _packageBrowseBtn;
	private Text _packageName;
	private ComboBox _projectNames;
	private Text _serviceName;

}