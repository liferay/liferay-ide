/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.ui.snippets;

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
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class TaglibVariableItemHelper {

	public static String getInsertString(Shell host, IEditorPart editor, ISnippetItem item) {
		return getInsertString(host, editor, item, true);
	}

	public static String getInsertString(final Shell host, IEditorPart editor, ISnippetItem item, boolean clearModality) {
		if (item == null)
			return ""; //$NON-NLS-1$
		String insertString = null;
		if (item.getVariables().length > 0) {
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
					dialog.addDisposeListener(new DisposeListener() {
						public void widgetDisposed(DisposeEvent arg0) {
							/*
							 * The parent shell must be reenabled when the
							 * dialog disposes, otherwise it won't
							 * automatically receive focus.
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
			if (result == Window.OK)
				insertString = dialog.getPreparedText();
		}
		else {
			insertString = item.getContentString();
		}
		return insertString;
	}

}
