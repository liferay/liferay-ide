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
 * @author Ying Xu
 */
public class ServiceBuilderWizard extends Wizard {

	public ServiceBuilderWizard(SWTWorkbenchBot bot) {
		super(bot, NEW_SERVICE_BUILDER, 2);

		_packagePath = new Text(bot, PACKAGE_PATH);
		_namespace = new Text(bot, NAMESPACE);
		_author = new Text(bot, AUTHOR);
		_serviceFile = new Text(bot, SERVICE_FILE);
		_includeSampleEntity = new CheckBox(bot, INCLUDE_SAMPLE_ENTITY_IN_NEW_FILE);
		_pluginProjects = new ComboBox(bot, PLUGIN_PROJECT);
		_browseButton = new Button(bot, BROWSE_WITH_DOT);
	}

	public void createServiceBuilder(String packagePathText, String namespaceText) {
		createServiceBuilder(packagePathText, namespaceText, true);
	}

	public void createServiceBuilder(String packagePathText, String namespaceText, boolean includeSampleEntityValue) {
		_packagePath.setText(packagePathText);
		_namespace.setText(namespaceText);

		if (includeSampleEntityValue) {
			_includeSampleEntity.select();
		}
		else {
			_includeSampleEntity.deselect();
		}
	}

	public Text getAuthor() {
		return _author;
	}

	public Button getBrowseButton() {
		return _browseButton;
	}

	public CheckBox getIncludeSampleEntity() {
		return _includeSampleEntity;
	}

	public Text getNamespace() {
		return _namespace;
	}

	public Text getPackagePath() {
		return _packagePath;
	}

	public ComboBox getPluginProjects() {
		return _pluginProjects;
	}

	public Text getServiceFile() {
		return _serviceFile;
	}

	private Text _author;
	private Button _browseButton;
	private CheckBox _includeSampleEntity;
	private Text _namespace;
	private Text _packagePath;
	private ComboBox _pluginProjects;
	private Text _serviceFile;

}