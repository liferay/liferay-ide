/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.ui.util;

import com.liferay.ide.eclipse.ui.LiferayUIPlugin;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

/**
 * @author Greg Amerson
 */
public class UIUtil {

	public static void async(Runnable runnable) {
		if (runnable != null) {
			try {
				Display.getDefault().asyncExec(runnable);
			}
			catch (Throwable t) {
				// ignore
			}
		}
	}

	public static Shell getActiveShell() {
		final Shell[] retval = new Shell[1];

		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				retval[0] = Display.getDefault().getActiveShell();
			}
		});

		return retval[0];
	}

	public static ImageDescriptor getPluginImageDescriptor(String symbolicName, String imagePath) {
		Bundle bundle = Platform.getBundle(symbolicName);

		if (bundle != null) {
			URL entry = bundle.getEntry(imagePath);

			if (entry != null) {
				return ImageDescriptor.createFromURL(entry);
			}
		}

		return null;
	}

	public static void postInfo(final String title, final String msg) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, msg);
			}

		});
	}

	public static void postInfoWithToggle(
		final String title, final String msg, final String toggleMessage, final boolean toggleState,
		final IPersistentPreferenceStore store, final String key) {

		if (store == null || key == null || store.getString(key).equals(MessageDialogWithToggle.NEVER)) {
			return;
		}

		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				MessageDialogWithToggle dialog =
					MessageDialogWithToggle.openInformation(
						Display.getDefault().getActiveShell(), title, msg, toggleMessage, toggleState, store, key);

				try {
					if (dialog.getToggleState()) {
						store.setValue(key, MessageDialogWithToggle.NEVER);
						store.save();
					}
				}
				catch (IOException e) {
				}
			}

		});
	}

	public static boolean promptQuestion(final String title, final String message) {
		final boolean[] retval = new boolean[1];

		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				retval[0] = MessageDialog.openQuestion(getActiveShell(), title, message);
			}
		});

		return retval[0];
	}

	public static IViewPart showView(String viewId) {
		try {
			IViewPart view = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().showView(viewId);
			return view;
		}
		catch (PartInitException e) {
			LiferayUIPlugin.logError(e);
		}

		return null;
	}

}
