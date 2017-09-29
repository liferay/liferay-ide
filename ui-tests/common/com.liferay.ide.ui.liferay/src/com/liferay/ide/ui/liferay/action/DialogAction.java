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
import com.liferay.ide.ui.swtbot.eclipse.page.TreeDialog;
import com.liferay.ide.ui.swtbot.page.Dialog;
import com.liferay.ide.ui.swtbot.page.TreeItem;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class DialogAction extends UIAction {

	public DialogAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	public void addModule(String projectName) {
		TreeItem projectTree = _addAndRemoveDialog.getAvailables().getTreeItem(projectName);

		projectTree.select();

		_addAndRemoveDialog.getAddBtn().click();
	}

	public void confirm() {
		_dialog.confirm();
	}

	public void deleteRuntime(String runtimeName) {
		_serverRuntimeEnvironmentsDialog.getRuntimes().click(runtimeName);

		_serverRuntimeEnvironmentsDialog.getRemoveBtn().click();
	}

	public void openNewRuntimeWizard() {
		_serverRuntimeEnvironmentsDialog.getAddBtn().click();
	}

	public void openPreferencesDialog() {
		ide.getPreferencesMenu().click();
	}

	public void openPreferenceTypeDialog(String categroy, String type) {
		TreeItem categroyItem = _preferencesDialog.getPreferencesTypes().getTreeItem(categroy);

		TreeItem typeItem = categroyItem.getTreeItem(type);

		typeItem.select();
	}

	public void openServerRuntimeEnvironmentsDialog() {
		openPreferenceTypeDialog(SERVER, RUNTIME_ENVIRONMENTS);
	}

	public void prepareText(String text) {
		_textDialog.getText().setText(text);
	}

	public void selectItem(String item) {
		_treeDialog.getItems().select(item);
	}

	public void selectItems(String... items) {
		_treeDialog.getItems().select(items);
	}

	private AddAndRemoveDialog _addAndRemoveDialog = new AddAndRemoveDialog(bot);
	private Dialog _dialog = new Dialog(bot);
	private PreferencesDialog _preferencesDialog = new PreferencesDialog(bot);
	private ServerRuntimeEnvironmentsPreferencesDialog _serverRuntimeEnvironmentsDialog =
		new ServerRuntimeEnvironmentsPreferencesDialog(bot);
	private TextDialog _textDialog = new TextDialog(bot);
	private TreeDialog _treeDialog = new TreeDialog(bot);

}