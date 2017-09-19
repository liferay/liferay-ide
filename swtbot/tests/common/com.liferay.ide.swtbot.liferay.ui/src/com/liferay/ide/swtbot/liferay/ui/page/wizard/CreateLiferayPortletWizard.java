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
import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

/**
 * @author Ashley Yuan
 */
public class CreateLiferayPortletWizard extends Wizard {

	public CreateLiferayPortletWizard(SWTWorkbenchBot bot, int validationMsgIndex) {
		this(bot, StringPool.BLANK, validationMsgIndex);
	}

	public CreateLiferayPortletWizard(SWTWorkbenchBot bot, String title, int validationMsgIndex) {
		super(bot, title, validationMsgIndex);

		_portletPluginProjects = new ComboBox(bot, PORTLET_PLUGIN_PROJECT);
		_sourceFolder = new Text(bot, SOURCE_FOLDER);
		_newPortlet = new Radio(bot, CREATE_NEW_PORTLET);
		_useDefault = new Radio(bot, USE_DEFAULT_PORTLET_MVCPORTLET);
		_portletClass = new Text(bot, PORTLET_CLASS);
		_javaPackage = new Text(bot, JAVA_PACKAGE);
		_superClasses = new ComboBox(bot, SUPERCLASS);
		_browseSourceBtn = new Button(bot, BROWSE_WITH_DOT);
		_browsePackageBtn = new Button(bot, BROWSE_WITH_DOT, 1);
		_browseSuperClassBtn = new Button(bot, BROWSE_WITH_DOT, 2);
	}

	public void createLiferayPortlet(boolean defaultMvc) {
		createLiferayPortlet(StringPool.BLANK, null, defaultMvc, null, null, null);
	}

	public void createLiferayPortlet(String projectName) {
		createLiferayPortlet(projectName, null, false, null, null, null);
	}

	public void createLiferayPortlet(String projectName, boolean defaultMvc) {
		createLiferayPortlet(projectName, null, defaultMvc, null, null, null);
	}

	public void createLiferayPortlet(String projectName, String srcFolder) {
		createLiferayPortlet(projectName, srcFolder, false, null, null, null);
	}

	public void createLiferayPortlet(
		String projectName, String srcFolder, boolean defaultMvc, String portletClassValue, String javaPackageValue,
		String superClass) {

		if ((projectName != null) && !projectName.equals(StringPool.BLANK)) {
			_portletPluginProjects.setSelection(projectName);
		}

		if (srcFolder != null) {
			_sourceFolder.setText(srcFolder);
		}

		if (defaultMvc) {
			_useDefault.click();

			Assert.assertFalse(_newPortlet.isSelected());
			Assert.assertTrue(_useDefault.isSelected());
		}
		else {
			_newPortlet.click();

			Assert.assertTrue(_newPortlet.isSelected());
			Assert.assertFalse(_useDefault.isSelected());

			if (_portletClass != null) {
				_portletClass.setText(portletClassValue);
			}

			if (_javaPackage != null) {
				_javaPackage.setText(javaPackageValue);
			}

			if (superClass != null) {
				setSuperClassCombobox(superClass);
			}
		}
	}

	public void createLiferayPortlet(String projectName, String portletClass, String javaPackage, String superClass) {
		createLiferayPortlet(projectName, null, false, portletClass, javaPackage, superClass);
	}

	public String[] getAvailableSuperClasses() {
		return _superClasses.items();
	}

	public Button getBrowsePackageBtn() {
		return _browsePackageBtn;
	}

	public Button getBrowseSourceBtn() {
		return _browseSourceBtn;
	}

	public Button getBrowseSuperClassBtn() {
		return _browseSuperClassBtn;
	}

	public Text getJavaPackage() {
		return _javaPackage;
	}

	public Radio getNewPortlet() {
		return _newPortlet;
	}

	public Text getPortletClass() {
		return _portletClass;
	}

	public ComboBox getPortletPluginProjects() {
		return _portletPluginProjects;
	}

	public Text getSourceFolder() {
		return _sourceFolder;
	}

	public ComboBox getSuperClasses() {
		return _superClasses;
	}

	public Radio getUseDefault() {
		return _useDefault;
	}

	public void setSuperClassCombobox(String superclassName) {
		String[] avilableSuperclasses = _superClasses.items();

		for (String mySuperclass : avilableSuperclasses) {
			if (mySuperclass.equals(superclassName)) {
				this._superClasses.setSelection(superclassName);
				return;
			}
		}

		_superClasses.setText(superclassName);
	}

	private Button _browsePackageBtn;
	private Button _browseSourceBtn;
	private Button _browseSuperClassBtn;
	private Text _javaPackage;
	private Radio _newPortlet;
	private Text _portletClass;
	private ComboBox _portletPluginProjects;
	private Text _sourceFolder;
	private ComboBox _superClasses;
	private Radio _useDefault;

}