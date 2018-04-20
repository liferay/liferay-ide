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

package com.liferay.ide.server.ui.action;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.editor.LiferayPropertiesEditor;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class CreatePortalSettingsFileAction extends AbstractServerRunningAction {

	public CreatePortalSettingsFileAction() {
	}

	public void run(IAction action) {
		IPath settingsFilePath = _getSettingsFilePath();

		if (settingsFilePath != null) {
			File newFile = settingsFilePath.toFile();

			try {
				if (newFile.createNewFile()) {
					UIUtil.refreshCommonView("org.eclipse.wst.server.ui.ServersView");

					IFileSystem efsLocalFilesSystem = EFS.getLocalFileSystem();

					FileStoreEditorInput editorInput = new FileStoreEditorInput(
						efsLocalFilesSystem.fromLocalFile(newFile));

					IWorkbench workbench = PlatformUI.getWorkbench();

					IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();

					IWorkbenchPage page = activeWorkbenchWindow.getActivePage();

					try {
						page.openEditor(editorInput, LiferayPropertiesEditor.ID);
					}
					catch (PartInitException pie) {
						LiferayServerUI.logError("Error opening properties editor.", pie);
					}
				}
			}
			catch (IOException ioe) {
				LiferayServerUI.logError("Unable to create new portal settings file", ioe);
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);

		if (action.isEnabled()) {
			IPath settingsFile = _getSettingsFilePath();

			if (FileUtil.exists(settingsFile)) {
				action.setEnabled(false);
			}
		}
	}

	@Override
	protected int getRequiredServerState() {
		return IServer.STATE_STARTED | IServer.STATE_STARTING | IServer.STATE_STOPPING | IServer.STATE_STOPPED |
			IServer.STATE_UNKNOWN;
	}

	private IPath _getSettingsFilePath() {
		IPath retval = null;

		if (selectedServer != null) {
			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(selectedServer);

			if (liferayRuntime != null) {
				IPath home = liferayRuntime.getLiferayHome();

				if (FileUtil.exists(home)) {
					retval = home.append(_PORTAL_EXT_PROPERTIES);
				}
			}
		}

		return retval;
	}

	private static final String _PORTAL_EXT_PROPERTIES = "portal-ext.properties";

}