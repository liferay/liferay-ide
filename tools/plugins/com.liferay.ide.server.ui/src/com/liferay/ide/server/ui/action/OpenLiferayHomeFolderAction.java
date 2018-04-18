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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.navigator.PropertiesFile;
import com.liferay.ide.server.ui.util.ServerUIUtil;

import java.io.IOException;

import java.util.Iterator;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Terry Jia
 */
public class OpenLiferayHomeFolderAction extends SelectionProviderAction {

	public OpenLiferayHomeFolderAction(ISelectionProvider sp) {
		super(sp, "Open Liferay Home Folder");
	}

	public OpenLiferayHomeFolderAction(ISelectionProvider selectionProvider, String text) {
		this(null, selectionProvider, text);
	}

	public OpenLiferayHomeFolderAction(Shell shell, ISelectionProvider selectionProvider, String text) {
		super(selectionProvider, text);

		_shell = shell;

		setEnabled(false);
	}

	public boolean accept(Object node) {
		return node instanceof PropertiesFile;
	}

	public Shell getShell() {
		return _shell;
	}

	public void perform(Object entry) {
		if (entry instanceof PropertiesFile) {
			PropertiesFile workflowEntry = (PropertiesFile)entry;

			String path = workflowEntry.getPath();

			try {
				ServerUIUtil.openFileInSystemExplorer(new Path(path));
			}
			catch (IOException ioe) {
				LiferayServerUI.logError("Error opening portal home folder.", ioe);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void run() {
		Iterator iterator = getStructuredSelection().iterator();

		if (!iterator.hasNext()) {
			return;
		}

		Object obj = iterator.next();

		if (accept(obj)) {
			perform(obj);
		}

		selectionChanged(getStructuredSelection());
	}

	@SuppressWarnings("rawtypes")
	public void selectionChanged(IStructuredSelection sel) {
		if (sel.isEmpty()) {
			setEnabled(false);

			return;
		}

		boolean enabled = false;

		Iterator iterator = sel.iterator();

		while (iterator.hasNext()) {
			Object obj = iterator.next();

			if (obj instanceof PropertiesFile) {
				PropertiesFile node = (PropertiesFile)obj;

				String path = node.getPath();

				try {
					if (!CoreUtil.isNullOrEmpty(ServerUIUtil.getSystemExplorerCommand(new Path(path).toFile())) &&
						accept(node)) {

						enabled = true;
					}
				}
				catch (IOException ioe) {
				}
			}
			else {
				setEnabled(false);

				return;
			}
		}

		setEnabled(enabled);
	}

	private Shell _shell;

}