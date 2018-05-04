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

package com.liferay.ide.ui.dialog;

import com.liferay.ide.core.util.ListUtil;

import java.io.File;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.internal.ide.ChooseWorkspaceDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.preferences.SettingsTransfer;

/**
 * @author Andy Wu
 */
@SuppressWarnings("restriction")
public class ChooseWorkspaceWithPreferenceDialog extends ChooseWorkspaceDialog {

	public ChooseWorkspaceWithPreferenceDialog(
		Shell parentShell, ChooseWorkspaceData launchData, boolean suppressAskAgain, boolean centerOnMonitor) {

		super(parentShell, launchData, suppressAskAgain, centerOnMonitor);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control top = super.createDialogArea(parent);

		IWorkbench workbench = PlatformUI.getWorkbench();

		workbench.getHelpSystem().setHelp(parent, IIDEHelpContextIds.SWITCH_WORKSPACE_ACTION);

		_createButtons((Composite)top);
		applyDialogFont(parent);

		return top;
	}

	@Override
	protected int getDialogBoundsStrategy() {
		return DIALOG_PERSISTLOCATION;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected void okPressed() {
		Iterator settingsIterator = _selectedSettings.iterator();
		MultiStatus result = new MultiStatus(
			PlatformUI.PLUGIN_ID, IStatus.OK,
			IDEWorkbenchMessages.ChooseWorkspaceWithSettingsDialog_ProblemsTransferTitle, null);

		IPath path = new Path(getWorkspaceLocation());
		String[] selectionIDs = new String[_selectedSettings.size()];
		int index = 0;

		while (settingsIterator.hasNext()) {
			IConfigurationElement elem = (IConfigurationElement)settingsIterator.next();

			result.add(_transferSettings(elem, path));
			selectionIDs[index++] = elem.getAttribute(_ATT_ID);
		}

		if (result.getSeverity() != IStatus.OK) {
			ErrorDialog.openError(
				getShell(), IDEWorkbenchMessages.ChooseWorkspaceWithSettingsDialog_TransferFailedMessage,
				IDEWorkbenchMessages.ChooseWorkspaceWithSettingsDialog_SaveSettingsFailed, result);
			return;
		}

		_saveSettings(selectionIDs);
		super.okPressed();
	}

	@SuppressWarnings("unchecked")
	private boolean _createButtons(Composite parent) {
		IConfigurationElement[] settings = SettingsTransfer.getSettingsTransfers();

		IDEWorkbenchPlugin idePlugin = IDEWorkbenchPlugin.getDefault();

		String[] enabledSettings = _getEnabledSettings(idePlugin.getDialogSettings().getSection(_WORKBENCH_SETTINGS));

		Composite panel = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(1, false);

		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		panel.setLayout(layout);

		panel.setFont(parent.getFont());

		Group group = new Group(panel, SWT.NONE);

		group.setText("Copy Settings");
		group.setLayout(layout);
		group.setFont(parent.getFont());

		for (int i = 0; i < settings.length; i++) {
			IConfigurationElement settingsTransfer = settings[i];

			Button button = new Button(group, SWT.CHECK);

			button.setText(settings[i].getAttribute(_ATT_NAME));

			String helpId = settings[i].getAttribute(_ATT_HELP_CONTEXT);

			if (helpId != null) {
				IWorkbench workbench = PlatformUI.getWorkbench();

				workbench.getHelpSystem().setHelp(button, helpId);
			}

			if (ListUtil.isNotEmpty(enabledSettings)) {
				String id = settings[i].getAttribute(_ATT_ID);

				for (int j = 0; j < enabledSettings.length; j++) {
					if ((enabledSettings[j] != null) && enabledSettings[j].equals(id)) {
						button.setSelection(true);
						_selectedSettings.add(settingsTransfer);

						break;
					}
				}
			}

			button.setBackground(parent.getBackground());
			button.addSelectionListener(
				new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						if (button.getSelection()) {
							_selectedSettings.add(settingsTransfer);
						}
						else {
							_selectedSettings.remove(settingsTransfer);
						}
					}

				});
		}

		if (ListUtil.isNotEmpty(enabledSettings)) {
			return true;
		}

		return false;
	}

	/**
	 * Get the settings for the receiver based on the entries in section.
	 *
	 * @param section
	 * @return String[] or <code>null</code>
	 */
	private String[] _getEnabledSettings(IDialogSettings section) {
		if (section == null) {
			return null;
		}

		return section.getArray(_ENABLED_TRANSFERS);
	}

	private IPath _getNewWorkbenchStateLocation(IPath newWorkspaceRoot) {
		IPath currentWorkspaceRoot = Platform.getLocation();

		IPath dataLocation = WorkbenchPlugin.getDefault().getDataLocation();

		if (dataLocation == null) {
			return null;
		}

		int segmentsToRemove = dataLocation.matchingFirstSegments(currentWorkspaceRoot);

		// Strip it down to the extension

		dataLocation = dataLocation.removeFirstSegments(segmentsToRemove);

		// Now add in the

		dataLocation = newWorkspaceRoot.append(dataLocation);

		return dataLocation;
	}

	private void _patchWorkingSets(IConfigurationElement element, IPath path) {
		String name = element.getAttribute(_ATT_NAME);

		if (name.trim().equals("Working Sets")) {
			IPath dataLocation = _getNewWorkbenchStateLocation(path);

			if (dataLocation == null) {
				return;
			}

			File dir = new File(dataLocation.toOSString());

			dir.mkdirs();
		}
	}

	/**
	 * Save the ids of the selected elements.
	 *
	 * @param selectionIDs
	 */
	private void _saveSettings(String[] selectionIDs) {
		IDEWorkbenchPlugin idePlugin = IDEWorkbenchPlugin.getDefault();

		IDialogSettings settings = idePlugin.getDialogSettings().getSection(_WORKBENCH_SETTINGS);

		if (settings == null) {
			settings = idePlugin.getDialogSettings().addNewSection(_WORKBENCH_SETTINGS);
		}

		settings.put(_ENABLED_TRANSFERS, selectionIDs);
	}

	/**
	 * Take the values from element and execute the class for path.
	 *
	 * @param elem
	 * @param path
	 * @return IStatus the result of the settings transfer.
	 */
	private IStatus _transferSettings(IConfigurationElement element, IPath path) {
		IStatus[] exceptions = new IStatus[1];

		SafeRunner.run(
			new ISafeRunnable() {

				@Override
				public void handleException(Throwable exception) {
					exceptions[0] = StatusUtil.newStatus(
						IStatus.ERROR,
						NLS.bind(
							IDEWorkbenchMessages.ChooseWorkspaceWithSettingsDialog_ClassCreationFailed,
							element.getAttribute(_ATT_CLASS)),
						exception);
				}

				@Override
				public void run() throws Exception {
					try {
						SettingsTransfer transfer = (SettingsTransfer)WorkbenchPlugin.createExtension(
							element, _ATT_CLASS);

						_patchWorkingSets(element, path);

						transfer.transferSettings(path);
					}
					catch (CoreException ce) {
						exceptions[0] = ce.getStatus();
					}
				}

			});

		if (exceptions[0] != null) {
			return exceptions[0];
		}

		return Status.OK_STATUS;
	}

	/**
	 * The class attribute for a settings transfer.
	 */
	private static final String _ATT_CLASS = "class";

	private static final String _ATT_HELP_CONTEXT = "helpContext";

	/**
	 * The id attribute for the settings transfer.
	 */
	private static final String _ATT_ID = "id";

	/**
	 * The name attribute for the settings transfer.
	 */
	private static final String _ATT_NAME = "name";

	private static final String _ENABLED_TRANSFERS = "ENABLED_TRANSFERS";

	private static final String _WORKBENCH_SETTINGS = "WORKBENCH_SETTINGS";

	@SuppressWarnings("rawtypes")
	private Collection _selectedSettings = new HashSet();

}