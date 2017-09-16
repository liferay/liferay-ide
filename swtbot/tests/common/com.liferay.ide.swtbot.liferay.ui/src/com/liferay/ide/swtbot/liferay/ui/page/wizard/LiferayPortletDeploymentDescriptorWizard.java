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
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ashley Yuan
 * @author Li Lu
 */
public class LiferayPortletDeploymentDescriptorWizard extends Wizard {

	public LiferayPortletDeploymentDescriptorWizard(SWTWorkbenchBot bot) {
		super(bot, 6);

		_icon = new Text(bot, ICON);
		_allowMultipleInstances = new CheckBox(bot, ALLOW_MULTIPLE_INSTANCES);
		_css = new Text(bot, CSS);
		_javaScript = new Text(bot, JAVASCRIPT);
		_cssClassWrapper = new Text(bot, CSS_CLASS_WRAPPER);
		_displayCategories = new ComboBox(bot, DISPLAY_CATEGORY);
		_addToControlPanel = new CheckBox(bot, ADD_TO_CONTROL_PANEL);
		_entryCategories = new ComboBox(bot, ENTRY_CATEGORY);
		_entryWeight = new Text(bot, ENTRY_WEIGHT);
		_createEntryClass = new CheckBox(bot, CREATE_ENTRY_CLASS);
		_entryClass = new Text(bot, ENTRY_CLASS);
		_browseIconBtn = new Button(bot, BROWSE_WITH_DOT, 0);
		_browseCssBtn = new Button(bot, BROWSE_WITH_DOT, 1);
		_browseJavaScriptBtn = new Button(bot, BROWSE_WITH_DOT, 2);
	}

	public CheckBox getAddToControlPanel() {
		return _addToControlPanel;
	}

	public CheckBox getAllowMultipleInstances() {
		return _allowMultipleInstances;
	}

	public Button getBrowseCssBtn() {
		return _browseCssBtn;
	}

	public Button getBrowseIconBtn() {
		return _browseIconBtn;
	}

	public Button getBrowseJavaScriptBtn() {
		return _browseJavaScriptBtn;
	}

	public CheckBox getCreateEntryClass() {
		return _createEntryClass;
	}

	public Text getCss() {
		return _css;
	}

	public Text getCssClassWrapper() {
		return _cssClassWrapper;
	}

	public ComboBox getDisplayCategory() {
		return _displayCategories;
	}

	public ComboBox getEntryCategory() {
		return _entryCategories;
	}

	public Text getEntryClass() {
		return _entryClass;
	}

	public Text getEntryWeight() {
		return _entryWeight;
	}

	public Text getIcon() {
		return _icon;
	}

	public Text getJavaScript() {
		return _javaScript;
	}

	public void setDisplayCategoryCombobox(String displayCategoryCombobox) {
		String[] avilableDisplayCategoryValues = _displayCategories.items();

		for (String mycategory : avilableDisplayCategoryValues) {
			if (mycategory.equals(displayCategoryCombobox)) {
				_displayCategories.setSelection(displayCategoryCombobox);

				return;
			}
		}

		_displayCategories.setText(displayCategoryCombobox);
	}

	public void setEntryCategoryCombobox(String entryCategoryCombobox) {
		String[] avilableEntryCategoryValues = _entryCategories.items();

		for (String myEnteryCategory : avilableEntryCategoryValues) {
			if (myEnteryCategory.equals(entryCategoryCombobox)) {
				_entryCategories.setSelection(entryCategoryCombobox);

				return;
			}
		}

		_entryCategories.setText(entryCategoryCombobox);
	}

	public void specifyLiferayDisplay(
		String displayCategoryValue, boolean addToControlPanelValue, String entryCategoryValue, String entryWeightValue,
		boolean createEntryClassValue, String entryClassValue) {

		String[] avilableDisplayCategoryValues = _displayCategories.items();

		if (_displayCategories != null) {
			for (String myDisplayCategory : avilableDisplayCategoryValues) {
				if (myDisplayCategory.equals(_displayCategories)) {
					_displayCategories.setSelection(displayCategoryValue);
				}
			}

			_displayCategories.setText(displayCategoryValue);
		}

		if (addToControlPanelValue) {
			_addToControlPanel.select();

			if (_entryCategories != null) {
				String[] avilableEntryCategoryValues = _displayCategories.items();

				for (String myEntryCategory : avilableEntryCategoryValues) {
					if (myEntryCategory.equals(_entryCategories)) {
						_entryCategories.setSelection(entryCategoryValue);
					}
				}

				_entryCategories.setText(entryCategoryValue);
			}

			if (_entryWeight != null) {
				_entryWeight.setText(entryWeightValue);
			}

			if (createEntryClassValue) {
				_createEntryClass.select();

				if (_entryClass != null) {
					_entryClass.setText(entryClassValue);
				}
			}
			else {
				_createEntryClass.deselect();
			}
		}
		else {
			_addToControlPanel.deselect();
		}
	}

	public void specifyLiferayPortletInfo(
		String liferayPortletIcon, boolean allowMultipleInstancesVaule, String liferayPortletCss,
		String liferayPortletJavaScript, String cssClassWrapperValue) {

		if (liferayPortletIcon != null) {
			_icon.setText(liferayPortletIcon);
		}

		if (allowMultipleInstancesVaule) {
			_allowMultipleInstances.select();
		}
		else {
			_allowMultipleInstances.deselect();
		}

		if (liferayPortletCss != null) {
			_css.setText(liferayPortletCss);
		}

		if (liferayPortletJavaScript != null) {
			_javaScript.setText(liferayPortletJavaScript);
		}

		if (_cssClassWrapper != null) {
			_cssClassWrapper.setText(cssClassWrapperValue);
		}
	}

	private CheckBox _addToControlPanel;
	private CheckBox _allowMultipleInstances;
	private Button _browseCssBtn;
	private Button _browseIconBtn;
	private Button _browseJavaScriptBtn;
	private CheckBox _createEntryClass;
	private Text _css;
	private Text _cssClassWrapper;
	private ComboBox _displayCategories;
	private ComboBox _entryCategories;
	private Text _entryClass;
	private Text _entryWeight;
	private Text _icon;
	private Text _javaScript;

}