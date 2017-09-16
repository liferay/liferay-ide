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
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Li Lu
 */
public class CreateLayoutTemplateWizardWizard extends Wizard {

	public CreateLayoutTemplateWizardWizard(SWTWorkbenchBot bot) {
		super(bot, 5);

		_layoutPluginProjects = new ComboBox(bot, LAYOUT_PLUGIN_PROJECT);
		_name = new Text(bot, NAME);
		_id = new Text(bot, ID);
		_templateFile = new Text(bot, TEMPLATE_FILE);
		_wapTemplateFile = new Text(bot, WAP_TEMPLATE_FILE);
		_thumbnailFile = new Text(bot, THUMBNAIL_FILE);
	}

	public void clickBrowseButton(int index) {
		new Button(bot, index).click();
	}

	public Text getId() {
		return _id;
	}

	public ComboBox getLayoutPluginProjects() {
		return _layoutPluginProjects;
	}

	public Text getName() {
		return _name;
	}

	public Text getTemplateFile() {
		return _templateFile;
	}

	public Text getThumbnailFile() {
		return _thumbnailFile;
	}

	public Text getWapTemplateFile() {
		return _wapTemplateFile;
	}

	private Text _id;
	private ComboBox _layoutPluginProjects;
	private Text _name;
	private Text _templateFile;
	private Text _thumbnailFile;
	private Text _wapTemplateFile;

}