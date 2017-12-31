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
import com.liferay.ide.ui.swtbot.eclipse.page.AddAndRemoveDialog;
import com.liferay.ide.ui.swtbot.eclipse.page.PreferencesDialog;
import com.liferay.ide.ui.swtbot.eclipse.page.ServerRuntimeEnvironmentsPreferencesDialog;
import com.liferay.ide.ui.swtbot.eclipse.page.TextDialog;
import com.liferay.ide.ui.swtbot.eclipse.page.TextTableDialog;
import com.liferay.ide.ui.swtbot.eclipse.page.TreeDialog;
import com.liferay.ide.ui.swtbot.page.Button;
import com.liferay.ide.ui.swtbot.page.Dialog;
import com.liferay.ide.ui.swtbot.util.CoreUtil;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;

/**
 * @author Terry Jia
 * @author Ying Xu
 */
public class DialogAction extends UIAction {

	public static DialogAction getInstance(SWTWorkbenchBot bot) {
		if (_dialogAction == null) {
			_dialogAction = new DialogAction(bot);
		}

		return _dialogAction;
	}

	public void cancel() {
		_dialog.cancel();
	}

	public void confirm() {
		_dialog.confirm();
	}

	public void confirm(String confirmLabel) {
		_dialog.confirm(confirmLabel);
	}

	public Button getConfirmBtn() {
		return _dialog.confirmBtn();
	}

	public void openPreferencesDialog() {
		if (CoreUtil.isMac()) {
			_keyboradAction.pressKeysPreferencesDialogMac();
		}
		else {
			_jobAction.waitForShellAppeared(ide.getLabel());

			ide.getPreferencesMenu().click();
		}
	}

	public void prepareText(String text) {
		_textDialog.getText().setText(text);

		ide.sleep();
	}

	public void selectItems(String... items) {
		_treeDialog.getItems().select(items);
	}

	public void selectTableItem(String item) {
		_textTableDialog.getItems();
	}

	public AddAndRemoveDialogAction addAndRemove = new AddAndRemoveDialogAction();
	public PreferencesDialogAction preferences = new PreferencesDialogAction();
	public ServerRuntimeEnvironmentsDialogAction serverRuntimeEnvironments =
		new ServerRuntimeEnvironmentsDialogAction();

	public class AddAndRemoveDialogAction {

		public void addModule(String projectName) {
			_addAndRemoveDialog.getAvailables().select(projectName);

			_addAndRemoveDialog.getAddBtn().click();
		}

		private final AddAndRemoveDialog _addAndRemoveDialog = new AddAndRemoveDialog(bot);

	}

	public class PreferencesDialogAction {

		public void confirm() {
			_preferencesDialog.confirm();
		}

		public void openPreferenceType(String categroy, String type) {
			_preferencesDialog.getPreferencesTypes().selectTreeItem(categroy, type);
		}

		public void openServerRuntimeEnvironmentsTry() {
			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 500;

			try {
				openPreferenceType(SERVER, RUNTIME_ENVIRONMENTS);
			}
			catch (Exception e) {
			}

			SWTBotPreferences.TIMEOUT = origin;
		}

		private final PreferencesDialog _preferencesDialog = new PreferencesDialog(bot);

	}

	public class ServerRuntimeEnvironmentsDialogAction {

		public void deleteRuntimeTryConfirm(String runtimeName) {
			ide.sleep(3000);

			_serverRuntimeEnvironmentsDialog.getRuntimes().click(runtimeName);

			ide.sleep(3000);

			_serverRuntimeEnvironmentsDialog.getRemoveBtn().click();

			long origin = SWTBotPreferences.TIMEOUT;

			SWTBotPreferences.TIMEOUT = 500;

			try {
				confirm();
			}
			catch (Exception e) {
			}

			SWTBotPreferences.TIMEOUT = origin;
		}

		public void openNewRuntimeWizard() {
			ide.sleep(5000);

			_serverRuntimeEnvironmentsDialog.getAddBtn().click();
		}

		private final ServerRuntimeEnvironmentsPreferencesDialog _serverRuntimeEnvironmentsDialog =
			new ServerRuntimeEnvironmentsPreferencesDialog(bot);

	}

	private DialogAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	private static DialogAction _dialogAction;

	private final Dialog _dialog = new Dialog(bot);
	private final JobAction _jobAction = JobAction.getInstance(bot);
	private final KeyboardAction _keyboradAction = KeyboardAction.getInstance(bot);
	private final TextDialog _textDialog = new TextDialog(bot);
	private final TextTableDialog _textTableDialog = new TextTableDialog(bot);
	private final TreeDialog _treeDialog = new TreeDialog(bot);

}