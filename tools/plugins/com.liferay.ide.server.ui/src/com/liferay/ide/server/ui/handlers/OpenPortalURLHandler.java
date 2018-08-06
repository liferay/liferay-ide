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

package com.liferay.ide.server.ui.handlers;

import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.ui.LiferayServerUI;

import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

/**
 * @author Eric Min
 */
@SuppressWarnings("restriction")
public abstract class OpenPortalURLHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)selection;

			Object selected = structuredSelection.getFirstElement();

			if (selected != null) {
				if (selected instanceof IServer) {
					ILiferayServer portalServer = getLiferayServer(selected);

					new Job(Msgs.openPortalUrl) {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							openPortalURL(portalServer, selected);

							return Status.OK_STATUS;
						}

					}.schedule();
				}
			}
		}

		return null;
	}

	protected IWorkbenchPart getActivePart() {
		return activePart;
	}

	protected Shell getActiveShell() {
		if (getActivePart() != null) {
			IWorkbenchPart ap = getActivePart();

			IWorkbenchPartSite site = ap.getSite();

			return site.getShell();
		}
		else {
			IWorkbench workbnech = PlatformUI.getWorkbench();

			IWorkbenchWindow activeWorkbenchWindow = workbnech.getActiveWorkbenchWindow();

			return activeWorkbenchWindow.getShell();
		}
	}

	protected ILiferayServer getLiferayServer(Object selected) {
		IServer iServer = null;

		if (selected instanceof IServer) {
			iServer = (IServer)selected;
		}

		return (ILiferayServer)iServer.loadAdapter(ILiferayServer.class, null);
	}

	protected abstract URL getPortalURL(Object selected);

	protected abstract String getPortalURLTitle();

	protected int getRequiredServerState() {
		return IServer.STATE_STARTED;
	}

	protected void openBrowser(URL url, String browserTitle) {
		Display display = Display.getDefault();

		display.asyncExec(
			new Runnable() {

				public void run() {
					try {
						ServerUIPlugin serverUIPlugin = ServerUIPlugin.getInstance();

						IWorkbench workbench = serverUIPlugin.getWorkbench();

						IWorkbenchBrowserSupport browserSupport = workbench.getBrowserSupport();

						IWebBrowser browser = browserSupport.createBrowser(
							IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR, null,
							browserTitle, null);

						browser.openURL(url);
					}
					catch (Exception e) {
						LiferayServerUI.logError(e);
					}
				}

			});
	}

	protected void openPortalURL(ILiferayServer portalServer, Object seleted) {
		URL portalUrl = getPortalURL(seleted);

		if (portalUrl == null) {
			MessageDialog.openError(getActiveShell(), Msgs.openPortalURL, Msgs.notDeterminePortalURL);

			return;
		}

		openBrowser(portalUrl, getPortalURLTitle());
	}

	protected IWorkbenchPart activePart;

	private static class Msgs extends NLS {

		public static String notDeterminePortalURL;
		public static String openPortalUrl;
		public static String openPortalURL;

		static {
			initializeMessages(OpenPortalURLHandler.class.getName(), Msgs.class);
		}

	}

}