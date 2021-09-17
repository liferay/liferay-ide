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

package com.liferay.ide.functional.liferay.action;

import com.liferay.ide.functional.liferay.UIAction;
import com.liferay.ide.functional.liferay.page.dialog.AddRepositoryDialog;
import com.liferay.ide.functional.liferay.page.dialog.AddUrlDialog;
import com.liferay.ide.functional.liferay.page.dialog.ProjectSelectionDialog;
import com.liferay.ide.functional.liferay.page.dialog.SwitchUpgradePlanDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.AddAndRemoveDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.AvailableSoftwareSitesPreferencesDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.GradlePreferencesDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.KaleoDesigerPreferencesDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.KaleoFileDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.KaleoWorkflowValidationPreferencesDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.LiferayPreferencesDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.MavenPreferencesDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.PluginValidationPreferencesDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.PreferencesDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.ServerRuntimeEnvironmentsPreferencesDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.TextDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.TextTableDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.TreeDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.UpdateMavenProjectDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.UpgradePlannerPreferencesDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.UpgradeProblemsPreferencesDialog;
import com.liferay.ide.functional.swtbot.eclipse.page.XmlSearchPreferencesDialog;
import com.liferay.ide.functional.swtbot.page.Button;
import com.liferay.ide.functional.swtbot.page.CLabel;
import com.liferay.ide.functional.swtbot.page.CheckBox;
import com.liferay.ide.functional.swtbot.page.ComboBox;
import com.liferay.ide.functional.swtbot.page.Dialog;
import com.liferay.ide.functional.swtbot.page.Label;
import com.liferay.ide.functional.swtbot.page.Menu;
import com.liferay.ide.functional.swtbot.page.Radio;
import com.liferay.ide.functional.swtbot.page.Table;
import com.liferay.ide.functional.swtbot.page.Text;
import com.liferay.ide.functional.swtbot.page.TextInGroup;
import com.liferay.ide.functional.swtbot.page.Tree;
import com.liferay.ide.functional.swtbot.util.CoreUtil;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Rui Wang
 */
public class DialogAction extends UIAction {

	public static DialogAction getInstance(SWTWorkbenchBot bot) {
		if (_dialogAction == null) {
			_dialogAction = new DialogAction(bot);
		}

		return _dialogAction;
	}

	public void cancel() {
		String title = _getDialog().getLabel();

		_getDialog().cancel();

		_jobAction.waitForShellClosed(title);
	}

	public void confirm() {
		String title = _getDialog().getLabel();

		_getDialog().confirm();

		_jobAction.waitForShellClosed(title);
	}

	public void confirm(String confirmLabel) {
		String title = _getDialog().getLabel();

		_getDialog().confirm(confirmLabel);

		_jobAction.waitForShellClosed(title);
	}

	public void deleteRuntimeFromPreferences(int runtimeIndex) {
		openPreferencesDialog();

		preferences.openServerRuntimeEnvironmentsTry();

		serverRuntimeEnvironments.deleteRuntimeTryConfirm(runtimeIndex);

		preferences.confirm();
	}

	public void deleteRuntimeFromPreferences(String runtimeName) {
		openPreferencesDialog();

		preferences.openServerRuntimeEnvironmentsTry();

		serverRuntimeEnvironments.deleteRuntimeTryConfirm(runtimeName);

		preferences.confirm();
	}

	public Button getConfirmBtn() {
		return _getDialog().confirmBtn();
	}

	public String getValidationMsg(int validationMsgIndex) {
		ide.sleep();

		return _getDialog().getValidationMsg(validationMsgIndex);
	}

	public void openPreferencesDialog() {
		_jobAction.waitForShellAppeared(ide.getLabel());

		if (CoreUtil.isMac()) {
			Text access = ide.getQuickAccess();

			access.setText("Preferences Preferences");

			access.setFocus();

			ide.sleep();

			_keyboardAction.pressKeyEnter();
		}
		else {
			Menu prefsMenu = ide.getPreferencesMenu();

			prefsMenu.click();
		}
	}

	public void prepareText(String text) {
		Text textArea = _textDialog.getText();

		textArea.setText(text);

		ide.sleep();
	}

	public void selectItems(String... items) {
		Tree tree = _treeDialog.getItems();

		tree.select(items);
	}

	public void selectOverrideFile(String... items) {
		Tree tree = _treeDialog.getItems();

		tree.selectTreeItem(items);

		confirm();
	}

	public void selectTableItem(String item) {
		_textTableDialog.getItems();
	}

	public AddAndRemoveDialogAction addAndRemove = new AddAndRemoveDialogAction();
	public AddRepositoryDialogAction addRepository = new AddRepositoryDialogAction();
	public AvailableSoftwareSitesDialogAction availableSoftwareSites = new AvailableSoftwareSitesDialogAction();
	public GradleDialogAction gradle = new GradleDialogAction();
	public KaleoDesignerAction kaleoDesign = new KaleoDesignerAction();
	public KaleoWorkflowValidationAction kaleoWorkflowValidation = new KaleoWorkflowValidationAction();
	public LiferayDialogAction liferay = new LiferayDialogAction();
	public MavenDialogAction maven = new MavenDialogAction();
	public AddPlannerOutlineDialogAction plannerOutline = new AddPlannerOutlineDialogAction();
	public PluginValidationDialogAction pluginValidation = new PluginValidationDialogAction();
	public PreferencesDialogAction preferences = new PreferencesDialogAction();
	public ProjectSelectionDialogAction projectSelection = new ProjectSelectionDialogAction();
	public ServerRuntimeEnvironmentsDialogAction serverRuntimeEnvironments =
		new ServerRuntimeEnvironmentsDialogAction();
	public SwitchUpgradePlanDialogAction switchUpgradePlan = new SwitchUpgradePlanDialogAction();
	public UpdateMavenProjectDialogAction updateMavenProject = new UpdateMavenProjectDialogAction();
	public UpgradePlannerDialogAction upgradePlanner = new UpgradePlannerDialogAction();
	public UpgradeProblemsDialogAction upgradeProblems = new UpgradeProblemsDialogAction();
	public WorkspaceFileDialogAction workspaceFile = new WorkspaceFileDialogAction();
	public XmlSearchDialogAction xmlSearch = new XmlSearchDialogAction();

	public class AddAndRemoveDialogAction {

		public void addModule(String projectName) {
			assertTitle(_getDialog(), _addAndRemoveDialog);

			ide.sleep();

			Tree availableTree = _addAndRemoveDialog.getAvailables();

			availableTree.select(projectName);

			ide.sleep();

			_addAndRemoveDialog.clickAddBtn();

			ide.sleep();
		}

		private final AddAndRemoveDialog _addAndRemoveDialog = new AddAndRemoveDialog(bot);

	}

	public class AddPlannerOutlineDialogAction {

		public void addUrl(String url) {
			_plannerUrl.setUrl(url);

			_plannerUrl.confirm();
		}

		private final AddUrlDialog _plannerUrl = new AddUrlDialog(bot);

	}

	public class AddRepositoryDialogAction {

		public void addLocation(String updatesiteUrl) {
			_addRepositoryDialog.setLocation(updatesiteUrl);

			String eclipseZipName = System.getProperty("eclipse.zip.name");

			if (eclipseZipName.contains("oxygen")) {
				_addRepositoryDialog.clickConfirmBtn();
			}
			else {
				_addRepositoryDialog.clickAddBtn();
			}
		}

		private final AddRepositoryDialog _addRepositoryDialog = new AddRepositoryDialog(bot);

	}

	public class AvailableSoftwareSitesDialogAction {

		public Table getSites() {
			assertTitle(_getDialog(), _availableSoftwareSitesPreferencesDialog);

			return _availableSoftwareSitesPreferencesDialog.getSites();
		}

		private final AvailableSoftwareSitesPreferencesDialog _availableSoftwareSitesPreferencesDialog =
			new AvailableSoftwareSitesPreferencesDialog(bot);

	}

	public class GradleDialogAction {

		public void checkAutomaticSync() {
			_gradleDialog.selectAutoSync();
		}

		private final GradlePreferencesDialog _gradleDialog = new GradlePreferencesDialog(bot);

	}

	public class KaleoDesignerAction {

		public Radio getAlwaysRadio() {
			return _kaleoDesignPreferencesDialog.getAlwaysRadio();
		}

		public Radio getNeverRadio() {
			return _kaleoDesignPreferencesDialog.getNeverRadio();
		}

		public Radio getPromptRadio() {
			return _kaleoDesignPreferencesDialog.getPromptRadio();
		}

		private final KaleoDesigerPreferencesDialog _kaleoDesignPreferencesDialog = new KaleoDesigerPreferencesDialog(
			bot);

	}

	public class KaleoWorkflowValidationAction {

		public boolean getConfigureProjectSpecificSettings(String link) {
			return _kaleoWorkflowValidationPreferencesDialog.getConfigureProjectSpecificSettings(link);
		}

		public ComboBox getDefaultWorkflowValidationLogical() {
			return _kaleoWorkflowValidationPreferencesDialog.getDefaultWorkflowValidationLogical();
		}

		public Label getSelectTheSeverityLevelForTheFollowingValidationProblems() {
			return _kaleoWorkflowValidationPreferencesDialog.
				getSelectTheSeverityLevelForTheFollowingValidationProblems();
		}

		public Label getWorkflowValidation() {
			return _kaleoWorkflowValidationPreferencesDialog.getWorkflowValidation();
		}

		private final KaleoWorkflowValidationPreferencesDialog _kaleoWorkflowValidationPreferencesDialog =
			new KaleoWorkflowValidationPreferencesDialog(bot);

	}

	public class LiferayDialogAction {

		public SWTBotTreeItem clickLiferay() {
			SWTBotTreeItem liferay = _liferayPreferencesDialog.getTreeItem(LIFERAY);

			return liferay.expand();
		}

		public List<String> expandLiferay(String nodes) {
			return clickLiferay().getNodes();
		}

		public Label getClearAllDoNotShowAgainSettingsAndShowAllHiddenDialogsAgain() {
			return _liferayPreferencesDialog.getClearAllDoNotShowAgainSettingsAndShowAllHiddenDialogsAgain();
		}

		public Button getClearBtn() {
			return _liferayPreferencesDialog.getClearBtn();
		}

		public CLabel getLiferay() {
			return _liferayPreferencesDialog.getLiferay();
		}

		public CLabel getMessageDialogs() {
			return _liferayPreferencesDialog.getMessageDialogs();
		}

		private final LiferayPreferencesDialog _liferayPreferencesDialog = new LiferayPreferencesDialog(bot);

	}

	public class MavenDialogAction {

		public CheckBox getAddPluginTypeSuffixForMavenProjectContextRoot() {
			return _mavenPreferencesDialog.getAddPluginTypeSuffixForMavenProjectContextRoot();
		}

		public TextInGroup getDefaultArchetypesForNewLiferayPluginProjectWizard() {
			return _mavenPreferencesDialog.getDefaultArchetypesForNewLiferayPluginProjectWizard();
		}

		public CheckBox getDisableCustomJspValidationChecking() {
			return _mavenPreferencesDialog.getDisableCustomJspValidationChecking();
		}

		public Text getExt() {
			return _mavenPreferencesDialog.getExt();
		}

		public Text getHook() {
			return _mavenPreferencesDialog.getHook();
		}

		public Text getLayoutTemplate() {
			return _mavenPreferencesDialog.getLayoutTemplate();
		}

		public CLabel getMaven() {
			return _mavenPreferencesDialog.getMaven();
		}

		public Text getPortlet() {
			return _mavenPreferencesDialog.getPortlet();
		}

		public Text getPortletICEfaces() {
			return _mavenPreferencesDialog.getPortletICEfaces();
		}

		public Text getPortletJsf() {
			return _mavenPreferencesDialog.getPortletJsf();
		}

		public Text getPortletLiferayFacesAlloy() {
			return _mavenPreferencesDialog.getPortletLiferayFacesAlloy();
		}

		public Text getPortletPrimefaces() {
			return _mavenPreferencesDialog.getPortletPrimefaces();
		}

		public Text getPortletRichFaces() {
			return _mavenPreferencesDialog.getPortletRichFaces();
		}

		public Text getPortletSpringMVC() {
			return _mavenPreferencesDialog.getPortletSpringMVC();
		}

		public Text getPortletVaadin() {
			return _mavenPreferencesDialog.getPortletVaadin();
		}

		public Text getServiceBuilder() {
			return _mavenPreferencesDialog.getServiceBuilder();
		}

		public Text getTheme() {
			return _mavenPreferencesDialog.getTheme();
		}

		public Text getWeb() {
			return _mavenPreferencesDialog.getWeb();
		}

		private final MavenPreferencesDialog _mavenPreferencesDialog = new MavenPreferencesDialog(bot);

	}

	public class PluginValidationDialogAction {

		public boolean getConfigureProjectSpecificSettings(String link) {
			return _pluginValidationPreferencesDialog.getConfigureProjectSpecificSettings(link);
		}

		public ComboBox getHierarchyOfTypeClassOrInterfaceIncorrect() {
			return _pluginValidationPreferencesDialog.getHierarchyOfTypeClassOrInterfaceIncorrect();
		}

		public Label getLiferayDisplayXmlDescriptor() {
			return _pluginValidationPreferencesDialog.getLiferayDisplayXmlDescriptor();
		}

		public Label getLiferayHookXmlDescriptor() {
			return _pluginValidationPreferencesDialog.getLiferayHookXmlDescriptor();
		}

		public Label getLiferayJspFiles() {
			return _pluginValidationPreferencesDialog.getLiferayJspFiles();
		}

		public Label getLiferayLayoutTemplatesDescriptor() {
			return _pluginValidationPreferencesDialog.getLiferayLayoutTemplatesDescriptor();
		}

		public Label getLiferayPortletXmlDescriptor() {
			return _pluginValidationPreferencesDialog.getLiferayPortletXmlDescriptor();
		}

		public Label getPortletXmlDescriptor() {
			return _pluginValidationPreferencesDialog.getPortletXmlDescriptor();
		}

		public ComboBox getReferenceToXmlElementNotFound() {
			return _pluginValidationPreferencesDialog.getReferenceToXmlElementNotFound();
		}

		public ComboBox getResourceNotFound() {
			return _pluginValidationPreferencesDialog.getResourceNotFound();
		}

		public Label getSelectTheSeverityLevelForTheFollowingValidationProblems() {
			return _pluginValidationPreferencesDialog.getSelectTheSeverityLevelForTheFollowingValidationProblems();
		}

		public Label getServiceXmlDescriptor() {
			return _pluginValidationPreferencesDialog.getServiceXmlDescriptor();
		}

		public ComboBox getSyntaxInvalid() {
			return _pluginValidationPreferencesDialog.getSyntaxInvalid();
		}

		public ComboBox getTypeClassOrInterfaceNotFound() {
			return _pluginValidationPreferencesDialog.getTypeClassOrInterfaceNotFound();
		}

		private final PluginValidationPreferencesDialog _pluginValidationPreferencesDialog =
			new PluginValidationPreferencesDialog(bot);

	}

	public class PreferencesDialogAction {

		public void confirm() {
			assertTitle(_getDialog(), _preferencesDialog);

			_preferencesDialog.confirm();
		}

		public void openAvailableSoftwareSites() {
			assertTitle(_getDialog(), _preferencesDialog);

			_openPreferenceType(INSTALL_UPDATE, AVAILABLE_SOFTWARE_SITES);
		}

		public void openGradleTry() {
			assertTitle(_getDialog(), _preferencesDialog);

			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 500;

			try {
				_openPreferenceType(GRADLE);
			}
			catch (Exception exception) {
			}

			SWTBotPreferences.TIMEOUT = origin;
		}

		public void openKaleoDesigner() {
			assertTitle(_getDialog(), _preferencesDialog);

			_openPreferenceType(LIFERAY, KALEO_DESIGNER);
		}

		public void openKaleoWorkflowValidation() {
			assertTitle(_getDialog(), _preferencesDialog);

			_openPreferenceType(LIFERAY, KALEO_WORKFLOW_VALIDATION);
		}

		public void openLiferay() {
			assertTitle(_getDialog(), _preferencesDialog);

			_openPreferenceType(LIFERAY);
		}

		public void openLiferayUpgradePlannerTry() {
			assertTitle(_getDialog(), _preferencesDialog);

			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 500;

			try {
				_openPreferenceType(LIFERAY, UPGRADE_PLANNER);
			}
			catch (Exception exception) {
			}

			SWTBotPreferences.TIMEOUT = origin;
		}

		public void openMaven() {
			assertTitle(_getDialog(), _preferencesDialog);

			_openPreferenceType(LIFERAY, MAVEN);
		}

		public void openPluginValidation() {
			assertTitle(_getDialog(), _preferencesDialog);

			_openPreferenceType(LIFERAY, PLUGIN_VALIDATION);
		}

		public void openServerRuntimeEnvironmentsTry() {
			assertTitle(_getDialog(), _preferencesDialog);

			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 500;

			try {
				_openPreferenceType(SERVER, RUNTIME_ENVIRONMENTS);
			}
			catch (Exception exception) {
			}

			SWTBotPreferences.TIMEOUT = origin;
		}

		public void openUpgradePlanner() {
			assertTitle(_getDialog(), _preferencesDialog);

			_openPreferenceType(LIFERAY, UPGRADE_PLANNER);
		}

		public void openUpgradeProblems() {
			assertTitle(_getDialog(), _preferencesDialog);

			_openPreferenceType(LIFERAY, UPGRADE_PROBLEMS);
		}

		public void openXmlSearch() {
			assertTitle(_getDialog(), _preferencesDialog);

			_openPreferenceType(LIFERAY, XML_SEARCH);
		}

		private void _openPreferenceType(String categroy) {
			Tree preferencesTypes = _preferencesDialog.getPreferencesTypes();

			preferencesTypes.selectTreeItem(categroy);
		}

		private void _openPreferenceType(String categroy, String type) {
			Tree preferencesTypes = _preferencesDialog.getPreferencesTypes();

			preferencesTypes.selectTreeItem(categroy, type);
		}

		private final PreferencesDialog _preferencesDialog = new PreferencesDialog(bot);

	}

	public class ProjectSelectionDialogAction {

		public void selectProject(String projectName) {
			_projectSelectionDialog.selectProject(projectName);
		}

		private final ProjectSelectionDialog _projectSelectionDialog = new ProjectSelectionDialog(bot);

	}

	public class ServerRuntimeEnvironmentsDialogAction {

		public void deleteRuntimeTryConfirm(int row) {
			assertTitle(_getDialog(), _serverRuntimeEnvironmentsDialog);

			ide.sleep(2000);

			_serverRuntimeEnvironmentsDialog.clickRuntime(row);

			ide.sleep(2000);

			_serverRuntimeEnvironmentsDialog.clickRemoveBtn();

			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 500;

			try {
				confirm();
			}
			catch (Exception exception) {
			}

			SWTBotPreferences.TIMEOUT = origin;
		}

		public void deleteRuntimeTryConfirm(String runtimeName) {
			assertTitle(_getDialog(), _serverRuntimeEnvironmentsDialog);

			ide.sleep(2000);

			_serverRuntimeEnvironmentsDialog.clickRuntime(runtimeName);

			ide.sleep(2000);

			_serverRuntimeEnvironmentsDialog.clickRemoveBtn();

			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 500;

			try {
				confirm();
			}
			catch (Exception exception) {
			}

			SWTBotPreferences.TIMEOUT = origin;
		}

		public Table getRuntimes() {
			return _serverRuntimeEnvironmentsDialog.getRuntimes();
		}

		public void openNewRuntimeWizard() {
			assertTitle(_getDialog(), _serverRuntimeEnvironmentsDialog);

			ide.sleep(5000);

			_serverRuntimeEnvironmentsDialog.clickAddBtn();
		}

		private final ServerRuntimeEnvironmentsPreferencesDialog _serverRuntimeEnvironmentsDialog =
			new ServerRuntimeEnvironmentsPreferencesDialog(bot);

	}

	public class SwitchUpgradePlanDialogAction {

		public void chooseUpgradePlan(String upgradePlanName) {
			_switchUpgradePlanDialog.select(upgradePlanName);
		}

		public void close() {
			_switchUpgradePlanDialog.clickClose();
		}

		public Boolean containsUpgradePlan(String upgradePlanName) {
			if (_switchUpgradePlanDialog.upgradePlanRow(upgradePlanName) == -1) {
				return false;
			}

			return true;
		}

		public Button getRemovePlanBtn() {
			return _switchUpgradePlanDialog.removePlanBtn();
		}

		public Button getStartPlanBtn() {
			return _switchUpgradePlanDialog.startPlanBtn();
		}

		public String[] getUpgradePlan(String upgradePlanName) {
			return _switchUpgradePlanDialog.upgradePlanMessage(upgradePlanName);
		}

		public void removePlan() {
			_switchUpgradePlanDialog.clickRemovePlan();
		}

		public void selectUpgradePlan(String upgradePlanName) {
			_switchUpgradePlanDialog.doubleClick(upgradePlanName);
		}

		public void startPlan() {
			_switchUpgradePlanDialog.clickStartPlan();
		}

		private final SwitchUpgradePlanDialog _switchUpgradePlanDialog = new SwitchUpgradePlanDialog(bot);

	}

	public class UpdateMavenProjectDialogAction {

		public void selectAll() {

			// assertTitle(_getDialog(), _updateMavenProjectDialog);

			try {
				_updateMavenProjectDialog.clickSelectAllBtn();
			}
			catch (Exception exception) {
			}
		}

		private final UpdateMavenProjectDialog _updateMavenProjectDialog = new UpdateMavenProjectDialog(bot);

	}

	public class UpgradePlannerDialogAction {

		public void addOutline() {
			_upgradePlannerDialog.clickAddBtn();
		}

		public void deleteOutline(int row) {
			assertTitle(_getDialog(), _upgradePlannerDialog);

			_upgradePlannerDialog.clickUrl(row);

			_upgradePlannerDialog.clickRemoveBtn();
		}

		public Button getAddBtn() {
			return _upgradePlannerDialog.getAddBtn();
		}

		public Button getRemoveBtn() {
			return _upgradePlannerDialog.getRemoveBtn();
		}

		public CLabel getUpgradePlanner() {
			return _upgradePlannerDialog.getUpgradePlanner();
		}

		public Table getUrl() {
			assertTitle(_getDialog(), _upgradePlannerDialog);

			return _upgradePlannerDialog.getUrl();
		}

		private final UpgradePlannerPreferencesDialog _upgradePlannerDialog = new UpgradePlannerPreferencesDialog(bot);

	}

	public class UpgradeProblemsDialogAction {

		public Button getRemoveBtn() {
			return _upgradeProblemsPreferencesDialog.getRemoveBtn();
		}

		public CLabel getUpgradeProblemsForIgnoreBreakingChangeProblems() {
			return _upgradeProblemsPreferencesDialog.getUpgradeProblemsForIgnoreBreakingChangeProblems();
		}

		private final UpgradeProblemsPreferencesDialog _upgradeProblemsPreferencesDialog =
			new UpgradeProblemsPreferencesDialog(bot);

	}

	public class WorkspaceFileDialogAction {

		public void addFiles(String projectName, String fileName) {
			ide.sleep();

			Tree kaleoFile = _kaleoFileDialog.getKaleoFile();

			kaleoFile.selectTreeItem(projectName, fileName);
		}

		private final KaleoFileDialog _kaleoFileDialog = new KaleoFileDialog(bot);

	}

	public class XmlSearchDialogAction {

		public Text getSpecifyTheListOfProjectsToIgnoreWhileSearchingXmlFiles() {
			return _xmlSearchPreferencesDialog.getSpecifyTheListOfProjectsToIgnoreWhileSearchingXmlFiles();
		}

		public CLabel getXmlSearch() {
			return _xmlSearchPreferencesDialog.getXmlSearch();
		}

		private final XmlSearchPreferencesDialog _xmlSearchPreferencesDialog = new XmlSearchPreferencesDialog(bot);

	}

	private DialogAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	private Dialog _getDialog() {
		return new Dialog(bot);
	}

	private static DialogAction _dialogAction;

	private final JobAction _jobAction = JobAction.getInstance(bot);
	private final KeyboardAction _keyboardAction = KeyboardAction.getInstance(bot);
	private final TextDialog _textDialog = new TextDialog(bot);
	private final TextTableDialog _textTableDialog = new TextTableDialog(bot);
	private final TreeDialog _treeDialog = new TreeDialog(bot);

}