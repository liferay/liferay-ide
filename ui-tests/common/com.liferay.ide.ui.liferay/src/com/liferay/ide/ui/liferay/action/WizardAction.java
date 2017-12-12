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

package com.liferay.ide.ui.liferay.action;

import com.liferay.ide.ui.liferay.UIAction;
import com.liferay.ide.ui.liferay.page.wizard.ImportLiferayWorkspaceProjectWizard;
import com.liferay.ide.ui.liferay.page.wizard.NewLiferay62RuntimeWizard;
import com.liferay.ide.ui.liferay.page.wizard.NewLiferay7RuntimeWizard;
import com.liferay.ide.ui.liferay.page.wizard.NewLiferayComponentWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewFragmentWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayJsfProjectWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayModuleInfoWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayModuleWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayPluginSdkProjectWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayWorkspaceWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewModuleFragmentInfoWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewProjectWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.SetSDKLocationWizard;
import com.liferay.ide.ui.swtbot.eclipse.page.ImportProjectWizard;
import com.liferay.ide.ui.swtbot.eclipse.page.NewRuntimeWizard;
import com.liferay.ide.ui.swtbot.eclipse.page.NewServerWizard;
import com.liferay.ide.ui.swtbot.page.Button;
import com.liferay.ide.ui.swtbot.page.CheckBox;
import com.liferay.ide.ui.swtbot.page.MenuItem;
import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.Wizard;
import com.liferay.ide.ui.swtbot.util.StringPool;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Ashley Yuan
 * @author Lily Li
 * @author Rui Wang
 */
public class WizardAction extends UIAction {

	public WizardAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	public void cancel() {
		_wizard.cancel();
	}

	public void deleteFragmentFile() {
		_newFragmentInfoWizard.getDeleteBtn().click();
	}

	public void deselectUseDefaultLocation() {
		_newProjectWizard.getUseDefaultLocation().deselect();
	}

	public void finish() {
		ide.sleep();

		String title = _wizard.getLabel();

		_wizard.finish();

		_jobAction.waitForShellClosed(title);
	}

	public Text getBundleUrl() {
		return _newWorkspaceWizard.getBundleUrl();
	}

	public CheckBox getDownloadLiferayBundle() {
		return _newWorkspaceWizard.getDownloadLiferayBundle();
	}

	public Button getFinishBtn() {
		return _wizard.finishBtn();
	}

	public Text getLocation() {
		return _newProjectWizard.getLocation();
	}

	public Button getNextBtn() {
		return _wizard.nextBtn();
	}

	public Text getServerName() {
		return _newWorkspaceWizard.getServerName();
	}

	public CheckBox getUseDefaultLocation() {
		return _newProjectWizard.getUseDefaultLocation();
	}

	public String getValidationMsg() {
		ide.sleep();

		return _wizard.getValidationMsg();
	}

	public String getValidationMsg(int validationMsgIndex) {
		ide.sleep();

		return _wizard.getValidationMsg(validationMsgIndex);
	}

	public void next() {
		ide.sleep();

		_wizard.next();
	}

	public void openAddOverrideFilesDialog() {
		_newFragmentInfoWizard.getAddOverrideFilesBtn().click();
	}

	public void openBrowseOsgiBundleDialog() {
		_newFragmentInfoWizard.getBrowseOsgiBtn().click();
	}

	public void openFileMenuFragmentFilesWizard() {
		ide.getFileMenu().clickMenu(NEW, LIFERAY_MODULE_FRAGMENT_FILES);
	}

	public void openImportLiferayWorkspaceWizard() {
		ide.getFileMenu().clickMenu(IMPORT);

		prepareImportType(LIFERAY, LIFERAY_WORKSPACE_PROJECT);

		next();
	}

	public void openNewBtnFragmentFilesWizard() {
		MenuItem menu = ide.getNewBtn().getLiferayMoudleFragmentFiles();

		menu.click();
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

	public void openNewLiferayLayoutTemplate() {
		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayLayoutTemplate();

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

	public void openNewLiferayPluginProjectWizard() {
		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayPlugin();

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

	public void prepareComponentClass(String projectName, String template) {
		_newLiferayComponentWizard.getProjectNames().setSelection(projectName);
		_newLiferayComponentWizard.getComponentClassTemplates().setSelection(template);

		ide.sleep();
	}

	public void prepareComponentClass(String projectName, String template, String className) {
		_newLiferayComponentWizard.getProjectNames().setSelection(projectName);
		_newLiferayComponentWizard.getComponentClassTemplates().setSelection(template);
		_newLiferayComponentWizard.getComponentClassName().setText(className);

		ide.sleep();
	}

	public void prepareComponentClass(String projectName, String template, String className, String packageName) {
		_newLiferayComponentWizard.getProjectNames().setSelection(projectName);

		ide.sleep();

		_newLiferayComponentWizard.getComponentClassTemplates().setSelection(template);

		ide.sleep();

		_newLiferayComponentWizard.getComponentClassName().setText(className);

		ide.sleep();

		_newLiferayComponentWizard.getPackageName().setText(packageName);

		ide.sleep();
	}

	public void prepareFragment(String projectName, String location, String buildType) {
		_newFragmentWizard.getProjectName().setText(projectName);

		if (!location.equals(StringPool.BLANK)) {
			_newFragmentWizard.getLocation().setText(location);
		}

		_newFragmentWizard.getBuildTypes().setSelection(buildType);
	}

	public void prepareFragmentGradle(String projectName) {
		prepareFragment(projectName, StringPool.BLANK, GRADLE);

		ide.sleep();
	}

	public void prepareFragmentGradle(String projectName, String location) {
		prepareFragment(projectName, location, GRADLE);
	}

	public void prepareFragmentMaven(String projectName) {
		prepareFragment(projectName, StringPool.BLANK, MAVEN);
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

		if (!downloadLiferayBundle) {
			return;
		}

		_importLiferayWorkspaceProjectWizard.getDownloadLiferaybundle().select();

		if (!serverName.equals(StringPool.EMPTY)) {
			_importLiferayWorkspaceProjectWizard.getServerName().setText(serverName);
		}
	}

	public void prepareImportType(String category, String type) {
		_importProjectWizard.getTypes().selectTreeItem(category, type);
	}

	public void prepareImportType(String filterText, String category, String type) {
		Text filter = _importProjectWizard.getFilter();

		filter.setText(filterText);

		_importProjectWizard.getTypes().selectTreeItem(category, type);
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

	public void prepareLiferay62RuntimeInfo(String name, String location) {
		_newLiferay62RuntimeWizard.getName().setText(name);
		_newLiferay62RuntimeWizard.getLocation().setText(location);
	}

	public void prepareLiferay62RuntimeType() {
		prepareRuntimeType(LIFERAY_INC, LIFERAY_V_62_TOMCAT_7);
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
		ide.sleep(500);

		_newServerWizard.getServerName().setText(serverName);
	}

	public void prepareNewServer62(String serverName) {
		ide.sleep(500);

		_newServerWizard.getServerName().setText(serverName);

		ide.sleep(500);

		_newServerWizard.getServerTypes().selectTreeItem(LIFERAY_INC, LIFERAY_V_62_SERVER_TOMCAT_7);
	}

	public void preparePlugin(String projectName, String buildType) {
		_newPluginSdkWizard.getProjectName().setText(projectName);

		_newPluginSdkWizard.getBuildTypes().setSelection(buildType);
	}

	public void preparePlugin(String projectName, String pluginType, String buildType) {
		_newPluginSdkWizard.getProjectName().setText(projectName);

		_newPluginSdkWizard.getPluginTypes().setSelection(pluginType);

		_newPluginSdkWizard.getBuildTypes().setSelection(buildType);
	}

	public void preparePluginHookSdk(String projectName) {
		preparePlugin(projectName, HOOK, ANT_LIFERAY_PLUGINS_SDK);
	}

	public void preparePluginLayoutTemplateSdk(String projectName) {
		preparePlugin(projectName, LAYOUT_TEMPLATE_UPCASE, ANT_LIFERAY_PLUGINS_SDK);
	}

	public void preparePluginPortletSdk(String projectName) {
		preparePlugin(projectName, PORTLET_UPCASE, ANT_LIFERAY_PLUGINS_SDK);
	}

	public void preparePluginSdk(String projectName) {
		preparePlugin(projectName, ANT_LIFERAY_PLUGINS_SDK);
	}

	public void preparePluginSdkLocation(String location) {
		_setSdkLocationWizard.getSdkLocation().setText(location);
	}

	public void preparePluginServiceBuilderPortletSdk(String projectName) {
		preparePlugin(projectName, SERVICE_BUILDER_PORTLET, ANT_LIFERAY_PLUGINS_SDK);
	}

	public void preparePluginThemeSdk(String projectName) {
		preparePlugin(projectName, THEME_UPCASE, ANT_LIFERAY_PLUGINS_SDK);
	}

	public void prepareRuntimeType(String category, String type) {
		ide.sleep(5000);

		_newRuntimeWizard.getServerTypes().selectTreeItem(category, type);
	}

	public void selectFragmentFile(String file) {
		_newFragmentInfoWizard.getFiles().click(file);
	}

	private final ImportLiferayWorkspaceProjectWizard _importLiferayWorkspaceProjectWizard =
		new ImportLiferayWorkspaceProjectWizard(bot);
	private final ImportProjectWizard _importProjectWizard = new ImportProjectWizard(bot);
	private final JobAction _jobAction = new JobAction(bot);
	private final NewModuleFragmentInfoWizard _newFragmentInfoWizard = new NewModuleFragmentInfoWizard(bot);
	private final NewFragmentWizard _newFragmentWizard = new NewFragmentWizard(bot);
	private final NewLiferayJsfProjectWizard _newJsfProjectWizard = new NewLiferayJsfProjectWizard(bot);
	private final NewLiferay7RuntimeWizard _newLiferay7RuntimeWizard = new NewLiferay7RuntimeWizard(bot);
	private final NewLiferay62RuntimeWizard _newLiferay62RuntimeWizard = new NewLiferay62RuntimeWizard(bot);
	private final NewLiferayComponentWizard _newLiferayComponentWizard = new NewLiferayComponentWizard(bot);
	private final NewLiferayModuleInfoWizard _newModuleInfoWizard = new NewLiferayModuleInfoWizard(bot);
	private final NewLiferayModuleWizard _newModuleWizard = new NewLiferayModuleWizard(bot);
	private final NewLiferayPluginSdkProjectWizard _newPluginSdkWizard = new NewLiferayPluginSdkProjectWizard(bot);
	private final NewProjectWizard _newProjectWizard = new NewProjectWizard(bot);
	private final NewRuntimeWizard _newRuntimeWizard = new NewRuntimeWizard(bot);
	private final NewServerWizard _newServerWizard = new NewServerWizard(bot);
	private final NewLiferayWorkspaceWizard _newWorkspaceWizard = new NewLiferayWorkspaceWizard(bot);
	private final SetSDKLocationWizard _setSdkLocationWizard = new SetSDKLocationWizard(bot);
	private final Wizard _wizard = new Wizard(bot);

}