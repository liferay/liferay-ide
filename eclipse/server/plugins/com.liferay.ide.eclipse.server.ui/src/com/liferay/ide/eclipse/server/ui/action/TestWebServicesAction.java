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

import com.liferay.ide.eclipse.server.core.IPortalServer;
import com.liferay.ide.eclipse.server.util.PortalServicesHelper;

import java.net.URL;

import org.eclipse.jface.action.IAction;

/**
 * @author Greg Amerson
 */
public class TestWebServicesAction extends AbstractServerRunningAction {

	public TestWebServicesAction() {
		super();
	}

	public void run(IAction action) {
		if (selectedServer == null) {
			return; // can't do anything if server has not been selected
		}

		IPortalServer portalServer = (IPortalServer) selectedServer.getAdapter(IPortalServer.class);

		URL webServicesListURL = portalServer.getWebServicesListURL();

		PortalServicesHelper helper = new PortalServicesHelper(webServicesListURL);

		String[] names = helper.getWebServiceNames();


	}

}
