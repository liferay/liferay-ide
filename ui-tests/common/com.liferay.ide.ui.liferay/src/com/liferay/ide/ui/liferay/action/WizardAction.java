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
import com.liferay.ide.ui.liferay.page.wizard.NewFragmentFilesWizard;
import com.liferay.ide.ui.liferay.page.wizard.NewLiferayComponentWizard;
import com.liferay.ide.ui.liferay.page.wizard.NewRuntime62Wizard;
import com.liferay.ide.ui.liferay.page.wizard.NewRuntime7Wizard;
import com.liferay.ide.ui.liferay.page.wizard.project.ImportLiferayWorkspaceWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewFragmentInfoWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewFragmentWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayJsfWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayModuleInfoWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayModuleWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayPluginWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayWorkspaceWizard;
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

	public static WizardAction getInstance(SWTWorkbenchBot bot) {
		if (_wizardAction == null) {
			_wizardAction = new WizardAction(bot);
		}

		return _wizardAction;
	}

	public void cancel() {
		_getWizard().cancel();
	}

	public void finish() {
		ide.sleep();

		String title = _getWizard().getLabel();

		_getWizard().finish();

		_jobAction.waitForShellClosed(title);

		ide.sleepLinux(2000);
	}

	public Button getFinishBtn() {
		return _getWizard().finishBtn();
	}

	public Button getNextBtn() {
		return _getWizard().nextBtn();
	}

	public String getValidationMsg() {
		ide.sleep();

		return _getWizard().getValidationMsg();
	}

	public String getValidationMsg(int validationMsgIndex) {
		ide.sleep();

		return _getWizard().getValidationMsg(validationMsgIndex);
	}

	public void next() {
		ide.sleep();

		_getWizard().next();
	}

	public void openFileMenuFragmentFilesWizard() {
		ide.getFileMenu().clickMenu(NEW, LIFERAY_MODULE_FRAGMENT_FILES);
	}

	public void openFileMenuLiferayComponentClassWizard() {
		ide.getFileMenu().clickMenu(NEW, LIFERAY_COMPONENT_CLASS);
	}

	public void openNewBtnFragmentFilesWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getNewBtn().getLiferayMoudleFragmentFiles();

		menu.click();
	}

	public void openNewBtnLiferayComponentClassWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getNewBtn().getLiferayComponentClass();

		menu.click();
	}

	public void openNewFragmentWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayModuleFragment();

		menu.click();
	}

	public void openNewLiferayComponentClassWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayComponentClass();

		menu.click();
	}

	public void openNewLiferayJsfProjectWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayJSFProject();

		menu.click();
	}

	public void openNewLiferayKaleoWorkflowWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayKaleoWorkflow();

		menu.click();
	}

	public void openNewLiferayLayoutTemplate() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayLayoutTemplate();

		menu.click();
	}

	public void openNewLiferayModuleWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayModule();

		menu.click();
	}

	public void openNewLiferayPluginProjectsFromExistingSourceWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayPluginProjectFromExistingSource();

		menu.click();
	}

	public void openNewLiferayPluginProjectWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayPlugin();

		menu.click();
	}

	public void openNewLiferayServerWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayServer();

		menu.click();
	}

	public void openNewLiferayWorkspaceWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject();

		menu.click();
	}

	public ImportLiferayWorkspaceWizardAction importLiferayWorkspace = new ImportLiferayWorkspaceWizardAction();
	public ImportProjectWizardAction importProject = new ImportProjectWizardAction();
	public NewFragmentWizardAction newFragment = new NewFragmentWizardAction();
	public NewFragmentFilesWizardAction newFragmentFiles = new NewFragmentFilesWizardAction();
	public NewFragmentInfoWizardAction newFragmentInfo = new NewFragmentInfoWizardAction();
	public NewLiferayComponentWizardAction newLiferayComponent = new NewLiferayComponentWizardAction();
	public NewLiferayJsfWizardAction newLiferayJsf = new NewLiferayJsfWizardAction();
	public NewLiferayWorkspaceWizardAction newLiferayWorkspace = new NewLiferayWorkspaceWizardAction();
	public NewModuleWizardAction newModule = new NewModuleWizardAction();
	public NewModuleInfoWizardAction newModuleInfo = new NewModuleInfoWizardAction();
	public NewPluginWizardAction newPlugin = new NewPluginWizardAction();
	public NewProjectWizardAction newProject = new NewProjectWizardAction();
	public NewRuntimeWizardAction newRuntime = new NewRuntimeWizardAction();
	public NewRuntime7WizardAction newRuntime7 = new NewRuntime7WizardAction();
	public NewRuntime62WizardAction newRuntime62 = new NewRuntime62WizardAction();
	public NewServerWizardAction newServer = new NewServerWizardAction();
	public SetSdkLocationWizardAction setSdkLocation = new SetSdkLocationWizardAction();

	public class ImportLiferayWorkspaceWizardAction {

		public void prepare(String location) {
			_importLiferayWorkspaceProjectWizard.getWorkspaceLocation().setText(location);
		}

		public void prepare(String location, boolean downloadLiferayBundle) {
			_importLiferayWorkspaceProjectWizard.getWorkspaceLocation().setText(location);

			if (downloadLiferayBundle) {
				_importLiferayWorkspaceProjectWizard.getDownloadLiferaybundle().select();
			}
		}

		public void prepare(String location, boolean downloadLiferayBundle, String serverName) {
			_importLiferayWorkspaceProjectWizard.getWorkspaceLocation().setText(location);

			if (!downloadLiferayBundle) {
				return;
			}

			_importLiferayWorkspaceProjectWizard.getDownloadLiferaybundle().select();

			if (!serverName.equals(StringPool.EMPTY)) {
				_importLiferayWorkspaceProjectWizard.getServerName().setText(serverName);
			}
		}

		public void prepareBundleUrl(String bundleUrl) {
			_importLiferayWorkspaceProjectWizard.getBundleUrl().setText(bundleUrl);
		}

		private final ImportLiferayWorkspaceWizard _importLiferayWorkspaceProjectWizard =
			new ImportLiferayWorkspaceWizard(bot);

	}

	public class ImportProjectWizardAction {

		public void openImportLiferayWorkspaceWizard() {
			ide.getFileMenu().clickMenu(IMPORT);

			_prepare(LIFERAY, LIFERAY_WORKSPACE_PROJECT);

			next();
		}

		private void _prepare(String category, String type) {
			_importProjectWizard.getTypes().selectTreeItem(category, type);
		}

		private final ImportProjectWizard _importProjectWizard = new ImportProjectWizard(bot);

	}

	public class NewFragmentFilesWizardAction {

		public void deleteFile() {
			_newFragmentFilesWizard.getDeleteBtn().click();
		}

		public void openAddOverrideFilesDialog() {
			_newFragmentFilesWizard.getAddOverrideFilesBtn().click();
		}

		public void selectFile(String file) {
			_newFragmentFilesWizard.getFiles().click(file);
		}

		private final NewFragmentFilesWizard _newFragmentFilesWizard = new NewFragmentFilesWizard(bot);

	}

	public class NewFragmentInfoWizardAction {

		public void deleteFile() {
			_newFragmentInfoWizard.getDeleteBtn().click();
		}

		public void openAddOverrideFilesDialog() {
			_newFragmentInfoWizard.getAddOverrideFilesBtn().click();
		}

		public void openBrowseOsgiBundleDialog() {
			_newFragmentInfoWizard.getBrowseOsgiBtn().click();
		}

		public void selectFile(String file) {
			_newFragmentInfoWizard.getFiles().click(file);
		}

		private final NewFragmentInfoWizard _newFragmentInfoWizard = new NewFragmentInfoWizard(bot);

	}

	public class NewFragmentWizardAction extends NewProjectWizardAction {

		public void openNewRuntimeWizard() {
			_newFragmentWizard.getNewRuntimeBtn().click();
		}

		public void prepareGradle(String projectName) {
			_prepare(projectName, StringPool.BLANK, GRADLE);

			ide.sleep();
		}

		public void prepareGradle(String projectName, String location) {
			_prepare(projectName, location, GRADLE);
		}

		public void prepareMaven(String projectName) {
			_prepare(projectName, StringPool.BLANK, MAVEN);
		}

		private void _prepare(String projectName, String location, String buildType) {
			_newFragmentWizard.getProjectName().setText(projectName);

			if (!location.equals(StringPool.BLANK)) {
				_newFragmentWizard.getLocation().setText(location);
			}

			_newFragmentWizard.getBuildTypes().setSelection(buildType);
		}

		private final NewFragmentWizard _newFragmentWizard = new NewFragmentWizard(bot);

	}

	public class NewLiferayComponentWizardAction {

		public void openSelectModelClassAndServiceDialog() {
			_newLiferayComponentWizard.getBrowseBtn().click();
		}

		public void openSelectPackageNameDialog() {
			_newLiferayComponentWizard.getPackageBrowseBtn().click();
		}

		public void prepare(String template) {
			_newLiferayComponentWizard.getComponentClassTemplates().setSelection(template);

			ide.sleep();
		}

		public void prepare(String projectName, String packageName) {
			_newLiferayComponentWizard.getProjectNames().setSelection(projectName);

			ide.sleep();

			_newLiferayComponentWizard.getPackageName().setText(packageName);

			ide.sleep();
		}

		public void prepare(String projectName, String template, String className, String packageName) {
			_newLiferayComponentWizard.getProjectNames().setSelection(projectName);

			ide.sleep();

			_newLiferayComponentWizard.getComponentClassTemplates().setSelection(template);

			ide.sleep();

			_newLiferayComponentWizard.getComponentClassName().setText(className);

			ide.sleep();

			_newLiferayComponentWizard.getPackageName().setText(packageName);

			ide.sleep();
		}

		public void prepareModelClass(String modelClass) {
			_newLiferayComponentWizard.getModelClassName().setText(modelClass);

			ide.sleep();
		}

		public void prepareProjectName(String projectName) {
			_newLiferayComponentWizard.getProjectNames().setSelection(projectName);

			ide.sleep();
		}

		public void prepareServiceName(String serviceName) {
			_newLiferayComponentWizard.getServiceName().setText(serviceName);

			ide.sleep();
		}

		private final NewLiferayComponentWizard _newLiferayComponentWizard = new NewLiferayComponentWizard(bot);

	}

	public class NewLiferayJsfWizardAction {

		public void prepareGradle(String projectName, String componentSuite) {
			_prepare(projectName, GRADLE, componentSuite);
		}

		public void prepareMaven(String projectName, String componentSuite) {
			_prepare(projectName, MAVEN, componentSuite);
		}

		private void _prepare(String projectName, String buildType, String componentSuite) {
			_newJsfProjectWizard.getProjectName().setText(projectName);
			_newJsfProjectWizard.getBuildTypes().setSelection(buildType);
			_newJsfProjectWizard.getComponentSuite().setSelection(componentSuite);
		}

		private final NewLiferayJsfWizard _newJsfProjectWizard = new NewLiferayJsfWizard(bot);

	}

	public class NewLiferayWorkspaceWizardAction extends NewProjectWizardAction {

		public void prepareGradle(String projectName) {
			_prepare(projectName, GRADLE);
		}

		public void prepareGradle(
			String projectName, boolean downloadLiferayBundle, String serverName, boolean useDefaultBundleUrl,
			String bundleUrl) {

			_prepare(projectName, GRADLE, downloadLiferayBundle, serverName, useDefaultBundleUrl, bundleUrl);
		}

		public void prepareMaven(String projectName) {
			_prepare(projectName, MAVEN);
		}

		public void selectDownloadLiferayBundle() {
			_newLiferayWorkspaceWizard.getDownloadLiferayBundle().select();
		}

		public void setBundleUrl(String bundleUrl) {
			_newLiferayWorkspaceWizard.getBundleUrl().setText(bundleUrl);
		}

		public void setServerName(String serverName) {
			_newLiferayWorkspaceWizard.getServerName().setText(serverName);
		}

		private void _prepare(String projectName, String buildType) {
			_newLiferayWorkspaceWizard.getProjectName().setText(projectName);
			_newLiferayWorkspaceWizard.getBuildTypes().setSelection(buildType);
		}

		private void _prepare(
			String projectName, String buildType, boolean downloadLiferayBundle, String serverName,
			boolean useDefaultBundleUrl, String bundleUrl) {

			_prepare(projectName, buildType);

			if (downloadLiferayBundle) {
				_newLiferayWorkspaceWizard.getDownloadLiferayBundle().select();

				_newLiferayWorkspaceWizard.getServerName().setText(serverName);

				if (!useDefaultBundleUrl) {
					_newLiferayWorkspaceWizard.getBundleUrl().setText(bundleUrl);
				}
			}
			else {
				_newLiferayWorkspaceWizard.getDownloadLiferayBundle().deselect();
			}
		}

		private final NewLiferayWorkspaceWizard _newLiferayWorkspaceWizard = new NewLiferayWorkspaceWizard(bot);

	}

	public class NewModuleInfoWizardAction {

		public void openSelectServiceDialog() {
			_newModuleInfoWizard.getBrowseBtn().click();
		}

		public void prepare(String packageName) {
			_newModuleInfoWizard.getPackageName().setText(packageName);
		}

		public void prepare(String className, String packageName) {
			_newModuleInfoWizard.getComponentClassName().setText(className);
			_newModuleInfoWizard.getPackageName().setText(packageName);
		}

		public void prepareProperties(String propertiesName, String propertiesValue) {
			_newModuleInfoWizard.getAddPropertyKeyBtn().click();
			_newModuleInfoWizard.getProperties().setText(2, propertiesName);
			_newModuleInfoWizard.getProperties().doubleClick(0, 1);
			_newModuleInfoWizard.getProperties().setText(2, propertiesValue);
			_newModuleInfoWizard.getProperties().setFocus();
		}

		private final NewLiferayModuleInfoWizard _newModuleInfoWizard = new NewLiferayModuleInfoWizard(bot);

	}

	public class NewModuleWizardAction extends NewProjectWizardAction {

		public void prepare(String projectName) {
			_newModuleWizard.getProjectName().setText(projectName);
		}

		public void prepare(String projectName, String buildType) {
			_newModuleWizard.getProjectName().setText(projectName);
			_newModuleWizard.getBuildTypes().setSelection(buildType);
		}

		public void prepare(
			String projectName, String buildType, String template, boolean useDefaultLocation, String location) {

			_prepare(projectName, buildType, template);

			if (useDefaultLocation) {
				_newModuleWizard.getUseDefaultLocation().deselect();
				_newModuleWizard.getLocation().setText(location);
			}
			else {
				_newModuleWizard.getUseDefaultLocation().select();
			}
		}

		public void prepareGradle(String projectName) {
			_prepare(projectName, GRADLE, MVC_PORTLET);
		}

		public void prepareGradle(String projectName, String template) {
			_prepare(projectName, GRADLE, template);
		}

		public void prepareMaven(String projectName) {
			_prepare(projectName, MAVEN, MVC_PORTLET);
		}

		public void prepareMaven(String projectName, String template) {
			_prepare(projectName, MAVEN, template);
		}

		private void _prepare(String projectName, String buildType, String template) {
			_newModuleWizard.getProjectName().setText(projectName);
			_newModuleWizard.getBuildTypes().setSelection(buildType);
			_newModuleWizard.getProjectTemplates().setSelection(template);
		}

		private final NewLiferayModuleWizard _newModuleWizard = new NewLiferayModuleWizard(bot);

	}

	public class NewPluginWizardAction {

		public void prepareHookSdk(String projectName) {
			_prepare(projectName, HOOK, ANT_LIFERAY_PLUGINS_SDK);
		}

		public void prepareLayoutTemplateSdk(String projectName) {
			_prepare(projectName, LAYOUT_TEMPLATE_UPCASE, ANT_LIFERAY_PLUGINS_SDK);
		}

		public void preparePortletSdk(String projectName) {
			_prepare(projectName, PORTLET_UPCASE, ANT_LIFERAY_PLUGINS_SDK);
		}

		public void prepareSdk(String projectName) {
			_prepare(projectName, ANT_LIFERAY_PLUGINS_SDK);
		}

		public void prepareServiceBuilderPortletSdk(String projectName) {
			_prepare(projectName, SERVICE_BUILDER_PORTLET, ANT_LIFERAY_PLUGINS_SDK);
		}

		public void prepareThemeSdk(String projectName) {
			_prepare(projectName, THEME_UPCASE, ANT_LIFERAY_PLUGINS_SDK);
		}

		private void _prepare(String projectName, String buildType) {
			_newPluginProjectWizard.getProjectName().setText(projectName);

			_newPluginProjectWizard.getBuildTypes().setSelection(buildType);
		}

		private void _prepare(String projectName, String pluginType, String buildType) {
			_newPluginProjectWizard.getProjectName().setText(projectName);

			_newPluginProjectWizard.getPluginTypes().setSelection(pluginType);

			_newPluginProjectWizard.getBuildTypes().setSelection(buildType);
		}

		private final NewLiferayPluginWizard _newPluginProjectWizard = new NewLiferayPluginWizard(bot);

	}

	public class NewProjectWizardAction {

		public void deselectUseDefaultLocation() {
			_newProjectWizard.getUseDefaultLocation().deselect();
		}

		public Text getLocation() {
			return _newProjectWizard.getLocation();
		}

		public CheckBox getUseDefaultLocation() {
			return _newProjectWizard.getUseDefaultLocation();
		}

		public void setLocation(String location) {
			_newProjectWizard.getLocation().setText(location);
		}

		public void setProjectName(String projectName) {
			_newProjectWizard.getProjectName().setText(projectName);
		}

		private final NewProjectWizard _newProjectWizard = new NewProjectWizard(bot);

	}

	public class NewRuntime7WizardAction {

		public void prepare(String location) {
			_newLiferay7RuntimeWizard.getLocation().setText(location);
		}

		public void prepare(String name, String location) {
			_newLiferay7RuntimeWizard.getName().setText(name);
			_newLiferay7RuntimeWizard.getLocation().setText(location);
		}

		private final NewRuntime7Wizard _newLiferay7RuntimeWizard = new NewRuntime7Wizard(bot);

	}

	public class NewRuntime62WizardAction {

		public void prepare(String name, String location) {
			_newLiferay62RuntimeWizard.getName().setText(name);
			_newLiferay62RuntimeWizard.getLocation().setText(location);
		}

		private final NewRuntime62Wizard _newLiferay62RuntimeWizard = new NewRuntime62Wizard(bot);

	}

	public class NewRuntimeWizardAction {

		public void prepare7() {
			_prepare(LIFERAY_INC, LIFERAY_7_X);
		}

		public void prepare62() {
			_prepare(LIFERAY_INC, LIFERAY_V_62_TOMCAT_7);
		}

		private void _prepare(String category, String type) {
			ide.sleep(5000);

			_newRuntimeWizard.getServerTypes().selectTreeItem(category, type);
		}

		private final NewRuntimeWizard _newRuntimeWizard = new NewRuntimeWizard(bot);

	}

	public class NewServerWizardAction {

		public void prepare(String serverName) {
			ide.sleep(500);

			_newServerWizard.getServerName().setText(serverName);

			ide.sleep(500);

			_newServerWizard.getServerTypes().selectTreeItem(LIFERAY_INC, LIFERAY_7_X);
		}

		public void prepare62(String serverName) {
			ide.sleep(500);

			_newServerWizard.getServerName().setText(serverName);

			ide.sleep(500);

			_newServerWizard.getServerTypes().selectTreeItem(LIFERAY_INC, LIFERAY_V_62_SERVER_TOMCAT_7);
		}

		private final NewServerWizard _newServerWizard = new NewServerWizard(bot);

	}

	public class SetSdkLocationWizardAction {

		public void prepare(String location) {
			_setSdkLocationWizard.getSdkLocation().setText(location);
		}

		private final SetSDKLocationWizard _setSdkLocationWizard = new SetSDKLocationWizard(bot);

	}

	private WizardAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	private Wizard _getWizard() {
		return new Wizard(bot);
	}

	private static WizardAction _wizardAction;

	private final JobAction _jobAction = JobAction.getInstance(bot);

}