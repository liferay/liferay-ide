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
import com.liferay.ide.ui.liferay.page.button.CreateLifeayProjectToolbarDropDownButton;
import com.liferay.ide.ui.liferay.page.button.NewToolbarDropDownButton;
import com.liferay.ide.ui.liferay.page.wizard.ChooseAssignmentTypeWizard;
import com.liferay.ide.ui.liferay.page.wizard.LiferayProjectFromExistSourceWizard;
import com.liferay.ide.ui.liferay.page.wizard.MakeTaskAssignActionWizard;
import com.liferay.ide.ui.liferay.page.wizard.MakeTaskAssignRoleByIdWizard;
import com.liferay.ide.ui.liferay.page.wizard.MakeTaskAssignRoleTypeWizard;
import com.liferay.ide.ui.liferay.page.wizard.MakeTaskAssignScriptWizard;
import com.liferay.ide.ui.liferay.page.wizard.MakeTaskAssignUserWizard;
import com.liferay.ide.ui.liferay.page.wizard.NewFragmentFilesWizard;
import com.liferay.ide.ui.liferay.page.wizard.NewKaleoWorkflowWizard;
import com.liferay.ide.ui.liferay.page.wizard.NewLiferayComponentWizard;
import com.liferay.ide.ui.liferay.page.wizard.NewRuntime62Wizard;
import com.liferay.ide.ui.liferay.page.wizard.NewRuntime7Wizard;
import com.liferay.ide.ui.liferay.page.wizard.project.ImportLiferayWorkspaceWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewFragmentInfoWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewFragmentWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayJsfWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayModuleInfoWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayModuleWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayModulesExtWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayPluginWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayWorkspaceWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.NewProjectWizard;
import com.liferay.ide.ui.liferay.page.wizard.project.SetSDKLocationWizard;
import com.liferay.ide.ui.swtbot.eclipse.page.ImportProjectWizard;
import com.liferay.ide.ui.swtbot.eclipse.page.InstallNewSoftwareWizard;
import com.liferay.ide.ui.swtbot.eclipse.page.NewRuntimeWizard;
import com.liferay.ide.ui.swtbot.eclipse.page.NewServerWizard;
import com.liferay.ide.ui.swtbot.page.Button;
import com.liferay.ide.ui.swtbot.page.CheckBox;
import com.liferay.ide.ui.swtbot.page.ComboBox;
import com.liferay.ide.ui.swtbot.page.MenuItem;
import com.liferay.ide.ui.swtbot.page.Radio;
import com.liferay.ide.ui.swtbot.page.Table;
import com.liferay.ide.ui.swtbot.page.Text;
import com.liferay.ide.ui.swtbot.page.ToolbarButtonWithTooltip;
import com.liferay.ide.ui.swtbot.page.Tree;
import com.liferay.ide.ui.swtbot.page.Wizard;
import com.liferay.ide.ui.swtbot.util.StringPool;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
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

	public Button getBackBtn() {
		return _getWizard().backBtn();
	}

	public Button getCancelBtn() {
		return _getWizard().cancelBtn();
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
		ide.clickFileMenu(NEW, LIFERAY_MODULE_FRAGMENT_FILES);
	}

	public void openFileMenuLiferayComponentClassWizard() {
		ide.clickFileMenu(NEW, LIFERAY_COMPONENT_CLASS);
	}

	public void openInstallNewSoftwareWizard() {
		ide.clickInstallMenu();
	}

	public void openNewBtnFragmentFilesWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _newToolbarDropDownBtn().getLiferayMoudleFragmentFiles();

		menu.click();
	}

	public void openNewBtnLiferayComponentClassWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _newToolbarDropDownBtn().getLiferayComponentClass();

		menu.click();
	}

	public void openNewFragmentWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayModuleFragment();

		menu.click();
	}

	public void openNewLiferayComponentClassWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayComponentClass();

		menu.click();
	}

	public void openNewLiferayJsfProjectWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayJSFProject();

		menu.click();
	}

	public void openNewLiferayKaleoWorkflowWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayKaleoWorkflow();

		menu.click();
	}

	public void openNewLiferayLayoutTemplate() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayLayoutTemplate();

		menu.click();
	}

	public void openNewLiferayModulesExtWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayModulesExt();

		menu.click();
	}

	public void openNewLiferayModuleWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayModule();

		menu.click();
	}

	public void openNewLiferayPluginProjectsFromExistingSourceWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayPluginProjectFromExistingSource();

		menu.click();
	}

	public void openNewLiferayPluginProjectWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayPlugin();

		menu.click();
	}

	public void openNewLiferayPortletWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayPortlet();

		menu.click();
	}

	public void openNewLiferayServerWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayServer();

		menu.click();
	}

	public void openNewLiferayWorkspaceWizard() {
		assertTitleStartBy(_getWizard(), ide.getShell());

		MenuItem menu = _createLiferayProjectToolbar().getNewLiferayWorkspaceProject();

		menu.click();
	}

	public ChooseAssignmentTypeWizardAction chooseAssignmentType = new ChooseAssignmentTypeWizardAction();
	public ImportLiferayWorkspaceWizardAction importLiferayWorkspace = new ImportLiferayWorkspaceWizardAction();
	public ImportProjectWizardAction importProject = new ImportProjectWizardAction();
	public InstallNewSoftwareWizardAction installNewSoftware = new InstallNewSoftwareWizardAction();
	public MakeTaskAssignActionWizardAction makeTaskAssignAction = new MakeTaskAssignActionWizardAction();
	public MakeTaskAssignRoleByIdWizardAction makeTaskAssignRoleById = new MakeTaskAssignRoleByIdWizardAction();
	public MakeTaskAssignRoleTypeWizardAction makeTaskAssignRoleType = new MakeTaskAssignRoleTypeWizardAction();
	public MakeTaskAssignScriptWizardAction makeTaskAssignScript = new MakeTaskAssignScriptWizardAction();
	public MakeTaskAssignUserWizardAction makeTaskAssignUser = new MakeTaskAssignUserWizardAction();
	public NewFragmentWizardAction newFragment = new NewFragmentWizardAction();
	public NewFragmentFilesWizardAction newFragmentFiles = new NewFragmentFilesWizardAction();
	public NewFragmentInfoWizardAction newFragmentInfo = new NewFragmentInfoWizardAction();
	public NewKaleoWorkflowWizardAction newKaleoWorkflow = new NewKaleoWorkflowWizardAction();
	public NewLiferayComponentWizardAction newLiferayComponent = new NewLiferayComponentWizardAction();
	public NewLiferayJsfWizardAction newLiferayJsf = new NewLiferayJsfWizardAction();
	public NewLiferayWorkspaceWizardAction newLiferayWorkspace = new NewLiferayWorkspaceWizardAction();
	public NewModuleWizardAction newModule = new NewModuleWizardAction();
	public NewModuleInfoWizardAction newModuleInfo = new NewModuleInfoWizardAction();
	public NewLiferayModulesExtWizardAction newModulesExt = new NewLiferayModulesExtWizardAction();
	public NewPluginWizardAction newPlugin = new NewPluginWizardAction();
	public NewProjectWizardAction newProject = new NewProjectWizardAction();
	public NewRuntimeWizardAction newRuntime = new NewRuntimeWizardAction();
	public NewRuntime7WizardAction newRuntime7 = new NewRuntime7WizardAction();
	public NewRuntime62WizardAction newRuntime62 = new NewRuntime62WizardAction();
	public NewServerWizardAction newServer = new NewServerWizardAction();
	public ProjectFromExistSourceWizardAction projectFromExistSource = new ProjectFromExistSourceWizardAction();
	public SetSdkLocationWizardAction setSdkLocation = new SetSdkLocationWizardAction();

	public class ChooseAssignmentTypeWizardAction {

		public void selectAssignCreator() {
			_chooseAssignmentTypeWizard.clickAssignCreator();
		}

		public void selectAssignResourceActions() {
			_chooseAssignmentTypeWizard.clickAssignResourceActions();
		}

		public void selectAssignRoleById() {
			_chooseAssignmentTypeWizard.clickAssignRoleById();
		}

		public void selectAssignRoleType() {
			_chooseAssignmentTypeWizard.clickAssignRoleType();
		}

		public void selectAssignScriptedAssignment() {
			_chooseAssignmentTypeWizard.clickAssignScriptedAssignment();
		}

		public void selectAssignUser() {
			_chooseAssignmentTypeWizard.clickAssignUser();
		}

		private final ChooseAssignmentTypeWizard _chooseAssignmentTypeWizard = new ChooseAssignmentTypeWizard(bot);

	}

	public class ImportLiferayWorkspaceWizardAction {

		public CheckBox addProjectToWorkingSet() {
			return _importLiferayWorkspaceProjectWizard.getAddProjectToWorkingSet();
		}

		public ToolbarButtonWithTooltip browseLocationBtn() {
			return _importLiferayWorkspaceProjectWizard.getBrowseLocationBtn();
		}

		public Text buildType() {
			return _importLiferayWorkspaceProjectWizard.getBuildTypeText();
		}

		public void deselectDownloadLiferayBundle() {
			downloadLiferayBundle().deselect();
		}

		public CheckBox downloadLiferayBundle() {
			return _importLiferayWorkspaceProjectWizard.getDownloadLiferayBundle();
		}

		public void prepare(String location, boolean downloadLiferayBundle) {
			workspaceLocation().setText(location);

			if (downloadLiferayBundle) {
				_importLiferayWorkspaceProjectWizard.selectDownloadLiferayBundle();
			}
		}

		public void prepare(String location, boolean downloadLiferayBundle, String serverName) {
			workspaceLocation().setText(location);

			if (!downloadLiferayBundle) {
				return;
			}

			_importLiferayWorkspaceProjectWizard.selectDownloadLiferayBundle();

			if (!serverName.equals(StringPool.EMPTY)) {
				serverName().setText(serverName);
			}
		}

		public void prepareBundleUrl(String bundleUrl) {
			_importLiferayWorkspaceProjectWizard.setBundleUrl(bundleUrl);
		}

		public void prepareLocation(String location) {
			workspaceLocation().setText(location);
		}

		public void prepareServerName(String serverName) {
			serverName().setText(serverName);
		}

		public void selectDownloadLiferayBundle() {
			downloadLiferayBundle().select();
		}

		public Text serverName() {
			return _importLiferayWorkspaceProjectWizard.getServerName();
		}

		public Text workspaceLocation() {
			return _importLiferayWorkspaceProjectWizard.getWorkspaceLocation();
		}

		private final ImportLiferayWorkspaceWizard _importLiferayWorkspaceProjectWizard =
			new ImportLiferayWorkspaceWizard(bot);

	}

	public class ImportProjectWizardAction {

		public void openImportLiferayWorkspaceWizard() {
			ide.clickFileMenu(IMPORT);

			_prepare(LIFERAY, LIFERAY_WORKSPACE_PROJECT);

			next();
		}

		private void _prepare(String category, String type) {
			_importProjectWizard.selectType(category, type);
		}

		private final ImportProjectWizard _importProjectWizard = new ImportProjectWizard(bot);

	}

	public class InstallNewSoftwareWizardAction {

		public Radio acceptTerms() {
			return _installNewSoftwareWizard.acceptTermsOfLicenseAgreement();
		}

		public void addRepository() {
			_installNewSoftwareWizard.clickAddBtn();
		}

		public CheckBox contactAllUpdateSites() {
			return _installNewSoftwareWizard.contactAllUpdateSites();
		}

		public void deselectContactAllUpdateSites() {
			contactAllUpdateSites().deselect();
		}

		public void selectAcceptTerms() {
			acceptTerms().click();
		}

		public void selectAll() {
			_installNewSoftwareWizard.clickSelectAllBtn();
		}

		private final InstallNewSoftwareWizard _installNewSoftwareWizard = new InstallNewSoftwareWizard(bot);

	}

	public class MakeTaskAssignActionWizardAction {

		public void addResourceAction(String resourceAction) {
			_makeTaskAssignActionWizard.clickAddResourceActionBtn();
			_makeTaskAssignActionWizard.setResourceAction(resourceAction);
		}

		private final MakeTaskAssignActionWizard _makeTaskAssignActionWizard = new MakeTaskAssignActionWizard(bot);

	}

	public class MakeTaskAssignRoleByIdWizardAction {

		public void prepareRoleId(String roleId) {
			_makeTaskAssignRoleByIdWizard.setRoleId(roleId);
		}

		private final MakeTaskAssignRoleByIdWizard _makeTaskAssignRoleByIdWizard = new MakeTaskAssignRoleByIdWizard(
			bot);

	}

	public class MakeTaskAssignRoleTypeWizardAction {

		public void addRole() {
			_makeTaskAssignRoleTypeWizard.clickAddRoleBtn();
		}

		public void deleteRole() {
			_makeTaskAssignRoleTypeWizard.clickDeleteRoleBtn();
		}

		private final MakeTaskAssignRoleTypeWizard _makeTaskAssignRoleTypeWizard = new MakeTaskAssignRoleTypeWizard(
			bot);

	}

	public class MakeTaskAssignScriptWizardAction {

		public void prepareScriptLanguage(String scriptLanguage) {
			_makeTaskAssignScriptWizard.setScriptLanguage(scriptLanguage);
		}

		private final MakeTaskAssignScriptWizard _makeTaskAssignScriptWizard = new MakeTaskAssignScriptWizard(bot);

	}

	public class MakeTaskAssignUserWizardAction {

		public void prepareEmailAddress(String emailAddress) {
			_makeTaskAssignUserWizard.setEmailAddress(emailAddress);
		}

		public void prepareSreenName(String screenName) {
			_makeTaskAssignUserWizard.setScreenName(screenName);
		}

		public void prepareUserId(String userId) {
			_makeTaskAssignUserWizard.setUserId(userId);
		}

		private final MakeTaskAssignUserWizard _makeTaskAssignUserWizard = new MakeTaskAssignUserWizard(bot);

	}

	public class NewFragmentFilesWizardAction extends NewProjectWizardAction {

		public ToolbarButtonWithTooltip addOverrideFilesBtn() {
			return _newFragmentFilesWizard.getAddOverrideFilesBtn();
		}

		public ToolbarButtonWithTooltip deleteBtn() {
			return _newFragmentFilesWizard.getDeleteBtn();
		}

		public void deleteFile() {
			deleteBtn().click();
		}

		public Text hostOsgiBundle() {
			return _newFragmentFilesWizard.getHostOsgiBundle();
		}

		public ToolbarButtonWithTooltip newRuntimeBtn() {
			return _newFragmentFilesWizard.getNewRuntimeBtn();
		}

		public void openAddOverrideFilesDialog() {
			addOverrideFilesBtn().click();
		}

		public ComboBox runtimeName() {
			return _newFragmentFilesWizard.getLiferyRuntimes();
		}

		public void selectFile(String file) {
			_newFragmentFilesWizard.clickFile(file);
		}

		private final NewFragmentFilesWizard _newFragmentFilesWizard = new NewFragmentFilesWizard(bot);

	}

	public class NewFragmentInfoWizardAction extends NewProjectWizardAction {

		public ToolbarButtonWithTooltip addOverrideFilesBtn() {
			return _newFragmentInfoWizard.getAddOverrideFilesBtn();
		}

		public ToolbarButtonWithTooltip browseOsgiBtn() {
			return _newFragmentInfoWizard.getBrowseOsgiBtn();
		}

		public ToolbarButtonWithTooltip deleteBtn() {
			return _newFragmentInfoWizard.getDeleteBtn();
		}

		public void deleteFile() {
			_newFragmentInfoWizard.clickDeleteBtn();
		}

		public ToolbarButtonWithTooltip getAddOverrideFilesBtn() {
			return _newFragmentInfoWizard.getAddOverrideFilesBtn();
		}

		public ToolbarButtonWithTooltip getBrowseOsgiBtn() {
			return _newFragmentInfoWizard.getBrowseOsgiBtn();
		}

		public Text getHostOsgiBundle() {
			return _newFragmentInfoWizard.getHostOsgiBundle();
		}

		public Text hostOsgiBundle() {
			return _newFragmentInfoWizard.getHostOsgiBundle();
		}

		public void openAddOverrideFilesDialog() {
			_newFragmentInfoWizard.clickAddOverrideFilesBtn();
		}

		public void openBrowseOsgiBundleDialog() {
			browseOsgiBtn().click();
		}

		public void selectFile(String file) {
			_newFragmentInfoWizard.clickGetFiles(file);
		}

		private final NewFragmentInfoWizard _newFragmentInfoWizard = new NewFragmentInfoWizard(bot);

	}

	public class NewFragmentWizardAction extends NewProjectWizardAction {

		public void openNewRuntimeWizard() {
			_newFragmentWizard.clickNewRuntimeBtn();
		}

		public void prepare(String projectName) {
			_prepare(projectName);

			ide.sleep();
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

		private void _prepare(String projectName) {
			_newFragmentWizard.setProjectName(projectName);
		}

		private void _prepare(String projectName, String location, String buildType) {
			_newFragmentWizard.setProjectName(projectName);

			if (!location.equals(StringPool.BLANK)) {
				_newFragmentWizard.setLocation(location);
			}

			_newFragmentWizard.setBuildType(buildType);
		}

		private final NewFragmentWizard _newFragmentWizard = new NewFragmentWizard(bot);

	}

	public class NewKaleoWorkflowWizardAction {

		public void openSelectProjectDialog() {
			_newKaleoWorkflowWizard.clickBrowseBtn();
		}

		private final NewKaleoWorkflowWizard _newKaleoWorkflowWizard = new NewKaleoWorkflowWizard(bot);

	}

	public class NewLiferayComponentWizardAction {

		public ToolbarButtonWithTooltip browsePackageBtn() {
			return _newLiferayComponentWizard.getPackageBrowseBtn();
		}

		public Text componentClassName() {
			return _newLiferayComponentWizard.getComponentClassName();
		}

		public ComboBox componentClassTemplate() {
			return _newLiferayComponentWizard.getComponentClassTemplates();
		}

		public void openSelectModelClassAndServiceDialog() {
			_newLiferayComponentWizard.clickBrowseBtn();
		}

		public void openSelectPackageNameDialog() {
			_newLiferayComponentWizard.clickPackageBrowseBtn();
		}

		public Text packageName() {
			return _newLiferayComponentWizard.getPackageName();
		}

		public void prepare(String template) {
			componentClassTemplate().setSelection(template);

			ide.sleep();
		}

		public void prepare(String projectName, String packageName) {
			projectName().setSelection(projectName);

			ide.sleep();

			packageName().setText(packageName);

			ide.sleep();
		}

		public void prepare(String projectName, String template, String className, String packageName) {
			projectName().setSelection(projectName);

			ide.sleep();

			_newLiferayComponentWizard.setComponentClassTemplates(template);

			ide.sleep();

			prepareComponentClass(className);

			ide.sleep();

			packageName().setText(packageName);

			ide.sleep();
		}

		public void prepareComponentClass(String componentClass) {
			componentClassName().setText(componentClass);
		}

		public void prepareModelClass(String modelClass) {
			_newLiferayComponentWizard.setModelClassName(modelClass);

			ide.sleep();
		}

		public void preparePackage(String packageName) {
			packageName().setText(packageName);
		}

		public void prepareProjectName(String projectName) {
			projectName().setSelection(projectName);

			ide.sleep();
		}

		public void prepareServiceName(String serviceName) {
			_newLiferayComponentWizard.setServiceName(serviceName);

			ide.sleep();
		}

		public ComboBox projectName() {
			return _newLiferayComponentWizard.getProjectNames();
		}

		public ComboBox projectNames() {
			return _newLiferayComponentWizard.getProjectNames();
		}

		private final NewLiferayComponentWizard _newLiferayComponentWizard = new NewLiferayComponentWizard(bot);

	}

	public class NewLiferayJsfWizardAction extends NewProjectWizardAction {

		public void prepareGradle(String projectName) {
			_prepare(projectName, GRADLE);
		}

		public void prepareGradle(String projectName, String componentSuite) {
			_prepare(projectName, GRADLE, componentSuite);
		}

		public void prepareMaven(String projectName, String componentSuite) {
			_prepare(projectName, MAVEN, componentSuite);
		}

		public ComboBox projectComponentSuite() {
			return _newJsfProjectWizard.getComponentSuite();
		}

		private void _prepare(String projectName, String buildType) {
			_newJsfProjectWizard.setProjectName(projectName);
			_newJsfProjectWizard.setBuildType(buildType);
		}

		private void _prepare(String projectName, String buildType, String componentSuite) {
			_newJsfProjectWizard.setProjectName(projectName);
			_newJsfProjectWizard.setBuildType(buildType);
			_newJsfProjectWizard.setComponentSuite(componentSuite);
		}

		private final NewLiferayJsfWizard _newJsfProjectWizard = new NewLiferayJsfWizard(bot);

	}

	public class NewLiferayModulesExtWizardAction extends NewProjectWizardAction {

		public ToolbarButtonWithTooltip browseBtn() {
			return _newModulesExtWizard.getBrowseBtn();
		}

		public void openSelectBrowseDialog() {
			browseBtn().click();
		}

		public void prepare(String projectName) {
			_newModulesExtWizard.setProjectName(projectName);
		}

		private final NewLiferayModulesExtWizard _newModulesExtWizard = new NewLiferayModulesExtWizard(bot);

	}

	public class NewLiferayWorkspaceWizardAction extends NewProjectWizardAction {

		public void deselectDownloadLiferayBundle() {
			downloadLiferayBundle().deselect();
		}

		public CheckBox downloadLiferayBundle() {
			return _newLiferayWorkspaceWizard.getDownloadLiferayBundle();
		}

		public Text getBundleUrl() {
			return _newLiferayWorkspaceWizard.getBundleUrl();
		}

		public ComboBox getTargetPlatform() {
			return _newLiferayWorkspaceWizard.getTargetPlatform();
		}

		public void prepareGradle(String projectName) {
			_prepare(projectName, GRADLE);
		}

		public void prepareGradle(
			String projectName, boolean downloadLiferayBundle, String serverName, boolean useDefaultBundleUrl,
			String bundleUrl) {

			_prepare(projectName, GRADLE, downloadLiferayBundle, serverName, useDefaultBundleUrl, bundleUrl);
		}

		public void prepareGradle(String projectName, String version) {
			_prepare(projectName, GRADLE, version);
		}

		public void prepareMaven(String projectName) {
			_prepare(projectName, MAVEN);
		}

		public void prepareMaven(String projectName, String version) {
			_prepare(projectName, MAVEN, version);
		}

		public void selectDownloadLiferayBundle() {
			downloadLiferayBundle().select();
		}

		public void setBundleUrl(String bundleUrl) {
			_newLiferayWorkspaceWizard.setBundleUrl(bundleUrl);
		}

		public void setServerName(String serverName) {
			_newLiferayWorkspaceWizard.setServerName(serverName);
		}

		private void _prepare(String projectName, String buildType) {
			_newLiferayWorkspaceWizard.setProjectName(projectName);
			_newLiferayWorkspaceWizard.setBuildType(buildType);
		}

		private void _prepare(
			String projectName, String buildType, boolean downloadLiferayBundle, String serverName,
			boolean useDefaultBundleUrl, String bundleUrl) {

			_prepare(projectName, buildType);

			if (downloadLiferayBundle) {
				downloadLiferayBundle().select();

				_newLiferayWorkspaceWizard.setServerName(serverName);

				if (!useDefaultBundleUrl) {
					_newLiferayWorkspaceWizard.setBundleUrl(bundleUrl);
				}
			}
			else {
				downloadLiferayBundle().deselect();
			}
		}

		private void _prepare(String projectName, String buildType, String version) {
			_newLiferayWorkspaceWizard.setProjectName(projectName);
			ide.sleep(800);
			_newLiferayWorkspaceWizard.setBuildType(buildType);
			_newLiferayWorkspaceWizard.setLiferayVersion(version);
			ide.sleep(800);
		}

		private final NewLiferayWorkspaceWizard _newLiferayWorkspaceWizard = new NewLiferayWorkspaceWizard(bot);

	}

	public class NewModuleInfoWizardAction {

		public void clickDeleteBtn() {
			_newModuleInfoWizard.clickDeleteBtn();
		}

		public Text componentClassName() {
			return _newModuleInfoWizard.getComponentClassName();
		}

		public ToolbarButtonWithTooltip deleteBtn() {
			return _newModuleInfoWizard.getDeleteBtn();
		}

		public void openSelectServiceDialog() {
			_newModuleInfoWizard.clickBrowseBtn();
		}

		public Text packageName() {
			return _newModuleInfoWizard.getPackageName();
		}

		public void prepare(String className, String packageName) {
			componentClassName().setText(className);
			packageName().setText(packageName);
		}

		public void preparePackageName(String packageName) {
			packageName().setText(packageName);
		}

		public void prepareProperties(String propertiesName, String propertiesValue) {
			_newModuleInfoWizard.clickAddPropertyKeyBtn();

			Table propertyTable = _newModuleInfoWizard.getProperties();

			propertyTable.setText(2, propertiesName);
			propertyTable.doubleClick(0, 1);
			propertyTable.setText(2, propertiesValue);
			propertyTable.setFocus();
		}

		private final NewLiferayModuleInfoWizard _newModuleInfoWizard = new NewLiferayModuleInfoWizard(bot);

	}

	public class NewModuleWizardAction extends NewProjectWizardAction {

		public void prepare(String projectName) {
			_newModuleWizard.setProjectName(projectName);
		}

		public void prepare(String projectName, String buildType) {
			_newModuleWizard.setProjectName(projectName);
			_newModuleWizard.setBuildType(buildType);
		}

		public void prepare(
			String projectName, String buildType, String template, boolean useDefaultLocation, String location) {

			_prepare(projectName, buildType, template);

			if (useDefaultLocation) {
				_newModuleWizard.deselectUseDefaultLocation();
				_newModuleWizard.setLocation(location);
			}
			else {
				_newModuleWizard.selectUseDefaultLocation();
			}
		}

		public void prepareGradle(String projectName) {
			_prepare(projectName, GRADLE, MVC_PORTLET);
		}

		public void prepareGradle(String projectName, String template) {
			_prepare(projectName, GRADLE, template, "7.0");
		}

		public void prepareGradle(String projectName, String template, String version) {
			_prepare(projectName, GRADLE, template, version);
		}

		public void prepareGradle(String projectName, String template, String location, String version) {
			_prepare(projectName, GRADLE, template, version);
			_newModuleWizard.deselectUseDefaultLocation();
			_newModuleWizard.setLocation(location);
		}

		public void prepareMaven(String projectName) {
			_prepare(projectName, MAVEN, MVC_PORTLET);
		}

		public void prepareMaven(String projectName, String template) {
			_prepare(projectName, MAVEN, template);
		}

		public ComboBox projectTemplateName() {
			return _newModuleWizard.getProjectTemplates();
		}

		private void _prepare(String projectName, String buildType, String template) {
			_newModuleWizard.setProjectName(projectName);
			_newModuleWizard.setBuildType(buildType);
			projectTemplateName().setSelection(template);
		}

		private void _prepare(String projectName, String buildType, String template, String version) {
			_newModuleWizard.setProjectName(projectName);
			_newModuleWizard.setBuildType(buildType);
			projectTemplateName().setSelection(template);
			_newModuleWizard.setLiferayVersion(version);
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
			_newPluginProjectWizard.setProjectName(projectName);

			_newPluginProjectWizard.setBuildType(buildType);
		}

		private void _prepare(String projectName, String pluginType, String buildType) {
			_newPluginProjectWizard.setProjectName(projectName);

			_newPluginProjectWizard.setPluginType(pluginType);

			_newPluginProjectWizard.setBuildType(buildType);
		}

		private final NewLiferayPluginWizard _newPluginProjectWizard = new NewLiferayPluginWizard(bot);

	}

	public class NewProjectWizardAction {

		public ComboBox buildType() {
			return _newProjectWizard.getBuildTypes();
		}

		public String[] buildTypes() {
			return buildType().items();
		}

		public String defaultVersions() {
			return liferayVersion().getText();
		}

		public void deselectUseDefaultLocation() {
			useDefaultLocation().deselect();
		}

		public String getLocation() {
			return location().getText();
		}

		public ComboBox liferayVersion() {
			return _newProjectWizard.getLiferayVersions();
		}

		public String[] liferayVersions() {
			return liferayVersion().items();
		}

		public Text location() {
			return _newProjectWizard.getLocation();
		}

		public void prepareLocation(String location) {
			location().setText(location);
		}

		public void prepareProjectName(String projectName) {
			projectName().setText(projectName);
		}

		public Text projectName() {
			return _newProjectWizard.getProjectName();
		}

		public void selectUseDefaultLocation() {
			useDefaultLocation().select();
		}

		public CheckBox useDefaultLocation() {
			return _newProjectWizard.getUseDefaultLocation();
		}

		private final NewProjectWizard _newProjectWizard = new NewProjectWizard(bot);

	}

	public class NewRuntime7WizardAction {

		public void prepare(String location) {
			Text serverLocation = _newLiferay7RuntimeWizard.getLocation();

			serverLocation.setText(location);
		}

		public void prepare(String name, String location) {
			Text serverName = _newLiferay7RuntimeWizard.getName();

			Text serverLocation = _newLiferay7RuntimeWizard.getLocation();

			serverName.setText(name);
			serverLocation.setText(location);
		}

		private final NewRuntime7Wizard _newLiferay7RuntimeWizard = new NewRuntime7Wizard(bot);

	}

	public class NewRuntime62WizardAction {

		public void prepare(String name, String location) {
			Text serverName = _newLiferay62RuntimeWizard.getName();

			Text serverLocation = _newLiferay62RuntimeWizard.getLocation();

			serverName.setText(name);
			serverLocation.setText(location);
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
			ide.sleep(3000);

			Tree serverTree = _newRuntimeWizard.getServerTypes();

			serverTree.selectTreeItem(category, type);
		}

		private final NewRuntimeWizard _newRuntimeWizard = new NewRuntimeWizard(bot);

	}

	public class NewServerWizardAction {

		public void prepare(String serverName) {
			ide.sleep(500);

			Text name = _newServerWizard.getServerName();

			name.setText(serverName);

			ide.sleep(500);

			Tree serverTree = _newServerWizard.getServerTypes();

			serverTree.selectTreeItem(LIFERAY_INC, LIFERAY_7_X);
		}

		public void prepare62(String serverName) {
			ide.sleep(500);

			Text name = _newServerWizard.getServerName();

			name.setText(serverName);

			ide.sleep(500);

			Tree serverTree = _newServerWizard.getServerTypes();

			serverTree.selectTreeItem(LIFERAY_INC, LIFERAY_V_62_SERVER_TOMCAT_7);
		}

		private final NewServerWizard _newServerWizard = new NewServerWizard(bot);

	}

	public class ProjectFromExistSourceWizardAction {

		public ToolbarButtonWithTooltip browseSdkDirectoryBtn() {
			return _projectFromExistSourceWizard.getBrowseSdkDirectoryBtn();
		}

		public Button deselectAllBtn() {
			return _projectFromExistSourceWizard.getDeselectAllBtn();
		}

		public Button refreshBtn() {
			return _projectFromExistSourceWizard.getRefreshBtn();
		}

		public Text sdkDirectory() {
			return _projectFromExistSourceWizard.getSdkDirectory();
		}

		public Text sdkVersion() {
			return _projectFromExistSourceWizard.getSdkVersion();
		}

		public Button selectAllBtn() {
			return _projectFromExistSourceWizard.getSelectAllBtn();
		}

		private final LiferayProjectFromExistSourceWizard _projectFromExistSourceWizard =
			new LiferayProjectFromExistSourceWizard(bot);

	}

	public class SetSdkLocationWizardAction {

		public void prepare(String location) {
			Text sdkLocation = _setSDKLocationWizard.getSdkLocation();

			sdkLocation.setText(location);
		}

		private final SetSDKLocationWizard _setSDKLocationWizard = new SetSDKLocationWizard(bot);

	}

	private WizardAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	private CreateLifeayProjectToolbarDropDownButton _createLiferayProjectToolbar() {
		return ide.getCreateLiferayProjectToolbar();
	}

	private Wizard _getWizard() {
		return new Wizard(bot);
	}

	private NewToolbarDropDownButton _newToolbarDropDownBtn() {
		return ide.getNewBtn();
	}

	private static WizardAction _wizardAction;

	private final JobAction _jobAction = JobAction.getInstance(bot);

}