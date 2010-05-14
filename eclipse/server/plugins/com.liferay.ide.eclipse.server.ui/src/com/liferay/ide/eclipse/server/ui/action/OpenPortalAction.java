/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.ui.action;

import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

import com.liferay.ide.eclipse.server.core.IPortalServer;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class OpenPortalAction implements IObjectActionDelegate {

	private IServer selectedServer;
	
	private IWorkbenchPart targetPart;

	public OpenPortalAction() {
		super();
	}

	public void run(IAction action) {
		if (selectedServer != null) {
			IPortalServer portalServer = (IPortalServer) selectedServer.getAdapter(IPortalServer.class);
			
			URL portalHome = portalServer.getPortalHomeUrl();
			
			try {
				IWorkbenchBrowserSupport browserSupport =
					ServerUIPlugin.getInstance().getWorkbench().getBrowserSupport();
				
				IWebBrowser browser =
					browserSupport.createBrowser(IWorkbenchBrowserSupport.LOCATION_BAR |
						IWorkbenchBrowserSupport.NAVIGATION_BAR, null, "Liferay Portal", null);
				
				browser.openURL(portalHome);
			}
			catch (Exception e) {
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		selectedServer = null;
		
		if (!selection.isEmpty()) {
			if (selection instanceof IStructuredSelection) {
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				
				if (obj instanceof IServer) {
					selectedServer = (IServer) obj;
					
					action.setEnabled(selectedServer.getServerState() == IServer.STATE_STARTED);
				}
			}
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

}
