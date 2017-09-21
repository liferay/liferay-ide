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

package com.liferay.ide.swtbot.liferay.ui.action;

import com.liferay.ide.swtbot.liferay.ui.UIAction;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ImportLiferayWorkspaceProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferay7RuntimeWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayComponentWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewFragmentWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayJsfProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleInfoWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayWorkspaceWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewModuleFragmentInfoWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.ImportProjectWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.NewRuntimeWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.NewServerWizard;
import com.liferay.ide.swtbot.ui.page.MenuItem;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.TreeItem;
import com.liferay.ide.swtbot.ui.page.Wizard;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Ashley Yuan
 */
public class WizardAction extends UIAction {

	public WizardAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	public void cancel() {
		_wizard.cancel();
	}

	public void finish() {
		_wizard.finish();
	}

	public void finishToWait() {
		_wizard.finish();

		long origin = SWTBotPreferences.TIMEOUT;

		SWTBotPreferences.TIMEOUT = 1000 * 60;

		openNewLiferayModuleWizard();

		cancel();

		SWTBotPreferences.TIMEOUT = origin;
	}

	public String getValidationMsg(int validationMsgIndex) {
		return _wizard.getValidationMsg(validationMsgIndex);
	}

	public void next() {
		_wizard.next();
	}

	public void openAddOverrideFilesDialog() {
		_newFragmentInfoWizard.getAddOverrideFilesBtn().click();
	}

	public void openBrowseOsgiBundleDialog() {
		_newFragmentInfoWizard.getBrowseOsgiBtn().click();
	}

	public void openImportLiferayWorkspaceWizard() {
		ide.getFileMenu().clickMenu(IMPORT);

		prepareImportType(LIFERAY, LIFERAY_WORKSPACE_PROJECT);

		next();
	}

	public void openNewFragmentWizard() {
		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayModuleFragment();

		menu.click();
	}

	public void openNewLiferayComponentClassWizard() {
		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayComponentClass();

		menu.click();
	}

	public void openNewLiferayJsfProjectWizard() {
		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayJSFProject();

		menu.click();
	}

	public void openNewLiferayModuleWizard() {
		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayModule();

		menu.click();
	}

	public void openNewLiferayPluginProjectsFromExistingSourceWizard() {
		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayPluginProjectFromExistingSource();

		menu.click();
	}

	public void openNewLiferayServerWizard() {
		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayServer();

		menu.click();
	}

	public void openNewLiferayWorkspaceWizard() {
		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject();

		menu.click();
	}

	public void openNewRuntimeWizardFragment() {
		_newFragmentWizard.getNewRuntimeBtn().click();
	}

	public void openSelectModelClassAndServiceDialog() {
		_newLiferayComponentWizard.getBrowseBtn().click();
	}

	public void openSelectServiceDialog() {
		_newModuleInfoWizard.getBrowseBtn().click();
	}

	public void prepareComponentClass(String projectName) {
		_newLiferayComponentWizard.getProjectNames().setSelection(projectName);
	}

	public void prepareComponentClass(String projectName, String componentClassTemplate) {
		_newLiferayComponentWizard.getProjectNames().setSelection(projectName);
		_newLiferayComponentWizard.getComponentClassTemplates().setSelection(componentClassTemplate);

		ide.sleep();
	}

	public void prepareFragment(String projectName, String buildType) {
		_newFragmentWizard.getProjectName().setText(projectName);
		_newFragmentWizard.getBuildTypes().setSelection(buildType);
	}

	public void prepareFragmentGradle(String projectName) {
		prepareFragment(projectName, GRADLE);
	}

	public void prepareFragmentMaven(String projectName) {
		prepareFragment(projectName, MAVEN);
	}

	public void prepareImportLiferayWorkspace(String location) {
		_importLiferayWorkspaceProjectWizard.getWorkspaceLocation().setText(location);
	}

	public void prepareImportLiferayWorkspace(String location, boolean downloadLiferayBundle) {
		_importLiferayWorkspaceProjectWizard.getWorkspaceLocation().setText(location);

		if (downloadLiferayBundle) {
			_importLiferayWorkspaceProjectWizard.getDownloadLiferaybundle().select();
		}
	}

	public void prepareImportLiferayWorkspace(String location, boolean downloadLiferayBundle, String serverName) {
		_importLiferayWorkspaceProjectWizard.getWorkspaceLocation().setText(location);

		if (downloadLiferayBundle) {
			_importLiferayWorkspaceProjectWizard.getDownloadLiferaybundle().select();

			_importLiferayWorkspaceProjectWizard.getServerName().setText(serverName);
		}
	}

	public void prepareImportType(String category, String type) {
		prepareImportType(StringPool.BLANK, category, type);
	}

	public void prepareImportType(String filterText, String category, String type) {
		Text filter = _importProjectWizard.getFilter();

		filter.setText(filterText);

		TreeItem categoryTypeTreeItem = _importProjectWizard.getTypes().expandNode(category, type);

		categoryTypeTreeItem.select();
	}

	public void prepareJsfProject(String projectName, String buildType, String componentSuite) {
		_newJsfProjectWizard.getProjectName().setText(projectName);
		_newJsfProjectWizard.getBuildTypes().setSelection(buildType);
		_newJsfProjectWizard.getComponentSuite().setSelection(componentSuite);
	}

	public void prepareJsfProjectGradle(String projectName, String componentSuite) {
		prepareJsfProject(projectName, GRADLE, componentSuite);
	}

	public void prepareJsfProjectMaven(String projectName, String componentSuite) {
		prepareJsfProject(projectName, MAVEN, componentSuite);
	}

	public void prepareLiferay7RuntimeInfo(String location) {
		_newLiferay7RuntimeWizard.getLocation().setText(location);
	}

	public void prepareLiferay7RuntimeInfo(String name, String location) {
		_newLiferay7RuntimeWizard.getName().setText(name);
		_newLiferay7RuntimeWizard.getLocation().setText(location);
	}

	public void prepareLiferay7RuntimeType() {
		prepareRuntimeType(LIFERAY_INC, LIFERAY_7_X);
	}

	public void prepareLiferayModule(String projectName) {
		_newModuleWizard.getProjectName().setText(projectName);
	}

	public void prepareLiferayModule(String projectName, String buildType) {
		_newModuleWizard.getProjectName().setText(projectName);
		_newModuleWizard.getBuildTypes().setSelection(buildType);
	}

	public void prepareLiferayModule(String projectName, String buildType, String template) {
		_newModuleWizard.getProjectName().setText(projectName);
		_newModuleWizard.getBuildTypes().setSelection(buildType);
		_newModuleWizard.getProjectTemplates().setSelection(template);
	}

	public void prepareLiferayModule(
		String projectName, String buildType, String template, boolean useDefaultLocation, String location) {

		prepareLiferayModule(projectName, buildType, template);

		if (useDefaultLocation) {
			_newModuleWizard.getUseDefaultLocation().deselect();
			_newModuleWizard.getLocation().setText(location);
		}
		else {
			_newModuleWizard.getUseDefaultLocation().select();
		}
	}

	public void prepareLiferayModuleGradle(String projectName) {
		prepareLiferayModule(projectName, GRADLE, MVC_PORTLET);
	}

	public void prepareLiferayModuleGradle(String projectName, String template) {
		prepareLiferayModule(projectName, GRADLE, template);
	}

	public void prepareLiferayModuleInfo(String className, String packageName) {
		_newModuleInfoWizard.getComponentClassName().setText(className);
		_newModuleInfoWizard.getPackageName().setText(packageName);
	}

	public void prepareLiferayModuleInfoProperties(String propertiesName, String propertiesValue) {
		_newModuleInfoWizard.getAddPropertyKeyBtn().click();
		_newModuleInfoWizard.getProperties().setText(2, propertiesName);
		_newModuleInfoWizard.getProperties().doubleClick(0, 1);
		_newModuleInfoWizard.getProperties().setText(2, propertiesValue);
		_newModuleInfoWizard.getProperties().setFocus();
	}

	public void prepareLiferayModuleMaven(String projectName, String template) {
		prepareLiferayModule(projectName, MAVEN, template);
	}

	public void prepareLiferayWorkspace(String projectName) {
		_newWorkspaceWizard.getProjectName().setText(projectName);
	}

	public void prepareLiferayWorkspace(String projectName, String buildType) {
		_newWorkspaceWizard.getProjectName().setText(projectName);
		_newWorkspaceWizard.getBuildTypes().setSelection(buildType);
	}

	public void prepareLiferayWorkspace(
		String projectName, String buildType, boolean downloadLiferayBundle, String serverName,
		boolean useDefaultBundleUrl, String bundleUrl) {

		prepareLiferayWorkspace(projectName, buildType);

		if (downloadLiferayBundle) {
			_newWorkspaceWizard.getDownloadLiferayBundle().select();

			_newWorkspaceWizard.getServerName().setText(serverName);

			if (!useDefaultBundleUrl) {
				_newWorkspaceWizard.getBundleUrl().setText(bundleUrl);
			}
		}
		else {
			_newWorkspaceWizard.getDownloadLiferayBundle().deselect();
		}
	}

	public void prepareLiferayWorkspaceGradle(String projectName) {
		prepareLiferayWorkspace(projectName, GRADLE);
	}

	public void prepareLiferayWorkspaceGradle(
		String projectName, boolean downloadLiferayBundle, String serverName, boolean useDefaultBundleUrl,
		String bundleUrl) {

		prepareLiferayWorkspace(projectName, GRADLE, downloadLiferayBundle, serverName, useDefaultBundleUrl, bundleUrl);
	}

	public void prepareLiferayWorkspaceMaven(String projectName) {
		prepareLiferayWorkspace(projectName, MAVEN);
	}

	public void prepareNewServer(String serverName) {
		_newServerWizard.getServerName().setText(serverName);
	}

	public void prepareRuntimeType(String category, String type) {
		TreeItem categoryTreeItem = _newRuntimeWizard.getServerTypes().getTreeItem(category);

		TreeItem treeTreeItem = categoryTreeItem.getTreeItem(type);

		treeTreeItem.select();
	}

	private ImportLiferayWorkspaceProjectWizard _importLiferayWorkspaceProjectWizard =
		new ImportLiferayWorkspaceProjectWizard(bot);
	private ImportProjectWizard _importProjectWizard = new ImportProjectWizard(bot);
	private NewModuleFragmentInfoWizard _newFragmentInfoWizard = new NewModuleFragmentInfoWizard(bot);
	private NewFragmentWizard _newFragmentWizard = new NewFragmentWizard(bot);
	private NewLiferayJsfProjectWizard _newJsfProjectWizard = new NewLiferayJsfProjectWizard(bot);
	private NewLiferay7RuntimeWizard _newLiferay7RuntimeWizard = new NewLiferay7RuntimeWizard(bot);
	private NewLiferayComponentWizard _newLiferayComponentWizard = new NewLiferayComponentWizard(bot);
	private NewLiferayModuleInfoWizard _newModuleInfoWizard = new NewLiferayModuleInfoWizard(bot);
	private NewLiferayModuleWizard _newModuleWizard = new NewLiferayModuleWizard(bot);
	private NewRuntimeWizard _newRuntimeWizard = new NewRuntimeWizard(bot);
	private NewServerWizard _newServerWizard = new NewServerWizard(bot);
	private NewLiferayWorkspaceWizard _newWorkspaceWizard = new NewLiferayWorkspaceWizard(bot);
	private Wizard _wizard = new Wizard(bot);

}