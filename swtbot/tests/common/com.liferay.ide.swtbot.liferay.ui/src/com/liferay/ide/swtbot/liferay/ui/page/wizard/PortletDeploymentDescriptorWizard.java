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

import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ashley Yuan
 */
public class PortletDeploymentDescriptorWizard extends Wizard {

	public PortletDeploymentDescriptorWizard(SWTWorkbenchBot bot) {
		super(bot, 5);

		_name = new Text(bot, NAME);
		_displayName = new Text(bot, DISPLAY_NAME);
		_title = new Text(bot, TITLE);
		_view = new CheckBox(bot, VIEW);
		_edit = new CheckBox(bot, EDIT);
		_help = new CheckBox(bot, HELP);
		_about = new CheckBox(bot, ABOUT);
		_config = new CheckBox(bot, CONFIG);
		_editDefaults = new CheckBox(bot, EDIT_DEFAULTS);
		_eitGuest = new CheckBox(bot, EDIT_GUEST);
		_preview = new CheckBox(bot, PREVIEW);
		_print = new CheckBox(bot, PRINT);
		_ceateJspFiles = new CheckBox(bot, CREATE_JSP_FILES);
		_jspFolder = new Text(bot, JSP_FOLDER);
		_createResourceBundleFile = new CheckBox(bot, CREATE_RESOURCE_BUNDLE_FILE);
		_resourceBundleFilePath = new Text(bot, RESOURCE_BUNDLE_FILE_PATH);
	}

	public CheckBox getAbout() {
		return _about;
	}

	public CheckBox getConfig() {
		return _config;
	}

	public CheckBox getCreateJspFiles() {
		return _ceateJspFiles;
	}

	public CheckBox getCreateResourceBundleFile() {
		return _createResourceBundleFile;
	}

	public Text getDisplayName() {
		return _displayName;
	}

	public CheckBox getEdit() {
		return _edit;
	}

	public CheckBox getEditDefaults() {
		return _editDefaults;
	}

	public CheckBox getEditGuest() {
		return _eitGuest;
	}

	public CheckBox getHelp() {
		return _help;
	}

	public Text getJspFolder() {
		return _jspFolder;
	}

	public Text getPortletName() {
		return _name;
	}

	public Text getPortletTitle() {
		return _title;
	}

	public CheckBox getPreview() {
		return _preview;
	}

	public CheckBox getPrint() {
		return _print;
	}

	public Text getResourceBundleFilePath() {
		return _resourceBundleFilePath;
	}

	public CheckBox getView() {
		return _view;
	}

	public void specifyResources(
		boolean createJspFilesValue, String jspFolderValue, boolean createResourceBundleFileValue,
		String resourceBundleFilePathValue) {

		if (createJspFilesValue) {
			_ceateJspFiles.select();

			if (_jspFolder != null) {
				_jspFolder.setText(jspFolderValue);
			}
		}
		else {
			_ceateJspFiles.deselect();
		}

		if (createResourceBundleFileValue) {
			_createResourceBundleFile.select();

			if (_resourceBundleFilePath != null) {
				_resourceBundleFilePath.setText(resourceBundleFilePathValue);
			}
		}
		else {
			_createResourceBundleFile.deselect();
		}
	}

	public void speficyLiferayPortletModes(
		boolean aboutLiferayMode, boolean configLiferayMode, boolean editDefaultsLiferayMode,
		boolean editGuestLiferayMode, boolean previewLiferayMode, boolean printLiferayMode) {

		if (aboutLiferayMode) {
			_about.select();
		}
		else {
			_about.deselect();
		}

		if (configLiferayMode) {
			_config.select();
		}
		else {
			_config.deselect();
		}

		if (editDefaultsLiferayMode) {
			_editDefaults.select();
		}
		else {
			_editDefaults.deselect();
		}

		if (editGuestLiferayMode) {
			_eitGuest.select();
		}
		else {
			_eitGuest.deselect();
		}

		if (previewLiferayMode) {
			_preview.select();
		}
		else {
			_preview.deselect();
		}

		if (printLiferayMode) {
			_print.select();
		}
		else {
			_print.deselect();
		}
	}

	public void speficyPortletInfo(String portletNameValue, String displayNameValue, String portletTitleValue) {
		if (portletNameValue != null) {
			_name.setText(portletNameValue);
		}

		if (_displayName != null) {
			_displayName.setText(displayNameValue);
		}

		if (_title != null) {
			_title.setText(portletTitleValue);
		}
	}

	public void speficyPortletModes(boolean editMode, boolean helpMode) {
		if (editMode) {
			_edit.select();
		}
		else {
			_edit.deselect();
		}

		if (helpMode) {
			_help.select();
		}
		else {
			_help.deselect();
		}
	}

	private CheckBox _about;
	private CheckBox _ceateJspFiles;
	private CheckBox _config;
	private CheckBox _createResourceBundleFile;
	private Text _displayName;
	private CheckBox _edit;
	private CheckBox _editDefaults;
	private CheckBox _eitGuest;
	private CheckBox _help;
	private Text _jspFolder;
	private Text _name;
	private CheckBox _preview;
	private CheckBox _print;
	private Text _resourceBundleFilePath;
	private Text _title;
	private CheckBox _view;

}