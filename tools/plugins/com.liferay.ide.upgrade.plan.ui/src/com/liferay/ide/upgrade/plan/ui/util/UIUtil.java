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

package com.liferay.ide.upgrade.plan.ui.util;

import com.liferay.ide.upgrade.plan.ui.internal.UpgradePlanUIPlugin;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Terry Jia
 */
public class UIUtil {

	public static void async(Runnable runnable) {
		if (runnable != null) {
			try {
				Display display = Display.getDefault();

				display.asyncExec(runnable);
			}
			catch (Throwable t) {
			}
		}
	}

	public static void async(Runnable runnable, long delay) {
		Runnable delayer = new Runnable() {

			public void run() {
				try {
					Thread.sleep(delay);
				}
				catch (InterruptedException ie) {
				}

				async(runnable);
			}

		};

		async(delayer);
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbench workbench = PlatformUI.getWorkbench();

		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

		return workbenchWindow.getActivePage();
	}

	public static Shell getActiveShell() {
		Shell[] retval = new Shell[1];

		Display display = Display.getDefault();

		display.syncExec(
			new Runnable() {

				public void run() {
					Display display = Display.getDefault();

					retval[0] = display.getActiveShell();
				}

			});

		return retval[0];
	}

	public static IWorkbenchBrowserSupport getBrowserSupport() {
		IWorkbench workbench = PlatformUI.getWorkbench();

		return workbench.getBrowserSupport();
	}

	public static PreferenceManager getPreferenceManager() {
		IWorkbench workbench = PlatformUI.getWorkbench();

		return workbench.getPreferenceManager();
	}

	public static void postInfo(String title, String msg) {
		Display display = Display.getDefault();

		display.asyncExec(
			new Runnable() {

				public void run() {
					Display display = Display.getDefault();

					MessageDialog.openInformation(display.getActiveShell(), title, msg);
				}

			});
	}

	public static void refreshCommonView(String viewId) {
		try {
			async(
				new Runnable() {

					public void run() {
						IViewPart viewPart = showView(viewId);

						if (viewPart != null) {
							CommonViewer viewer = (CommonViewer)viewPart.getAdapter(CommonViewer.class);

							viewer.refresh(true);
						}
					}

				});
		}
		catch (Exception e) {
			UpgradePlanUIPlugin.logError("Unable to refresh view " + viewId, e);
		}
	}

	public static IViewPart showView(String viewId) {
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();

			IWorkbenchWindow workbenchWindow = workbench.getWorkbenchWindows()[0];

			IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();

			return workbenchPage.showView(viewId);
		}
		catch (PartInitException pie) {
			UpgradePlanUIPlugin.logError(pie);
		}

		return null;
	}

	public static void sync(Runnable runnable) {
		if (runnable != null) {
			try {
				Display display = Display.getDefault();

				display.syncExec(runnable);
			}
			catch (Throwable t) {
			}
		}
	}

}