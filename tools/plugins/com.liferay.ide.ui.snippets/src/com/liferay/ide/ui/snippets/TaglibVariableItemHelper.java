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

package com.liferay.ide.ui.snippets;

import com.liferay.ide.core.util.ListUtil;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.VariableInsertionDialog;

/**
 * Class copied from VariableItemHelper.java v1.4
 *
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class TaglibVariableItemHelper {

	public static String getInsertString(Shell host, IEditorPart editor, ISnippetItem item) {
		return getInsertString(host, editor, item, true);
	}

	public static String getInsertString(Shell host, IEditorPart editor, ISnippetItem item, boolean clearModality) {
		if (item == null) {
			return "";
		}

		String insertString = null;

		if (ListUtil.isNotEmpty(item.getVariables())) {
			VariableInsertionDialog dialog = new TaglibVariableInsertionDialog(host, editor, clearModality);

			dialog.setItem(item);

			// The editor itself influences the insertion's actions, so we
			// can't
			// allow the active editor to be changed.
			// Disabling the parent shell achieves psuedo-modal behavior
			// without
			// locking the UI under Linux

			int result = Window.CANCEL;

			try {
				if (clearModality) {
					host.setEnabled(false);

					dialog.addDisposeListener(
						new DisposeListener() {

							public void widgetDisposed(DisposeEvent arg0) {

								/*
								 * The parent shell must be reenabled when the
								 * dialog disposes, otherwise it won't automatically
								 * receive focus.
								 */
								host.setEnabled(true);
							}

						});
				}

				result = dialog.open();
			}
			catch (Exception t) {
				SnippetsUIPlugin.logError(t);
			}
			finally {
				if (clearModality) {
					host.setEnabled(true);
				}
			}

			if (result == Window.OK) {
				insertString = dialog.getPreparedText();
			}
		}
		else {
			insertString = item.getContentString();
		}

		return insertString;
	}

}