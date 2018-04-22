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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.util.WebServicesHelper;
import com.liferay.ide.ui.dialog.StringsFilteredDialog;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.ws.internal.explorer.LaunchOption;
import org.eclipse.wst.ws.internal.explorer.LaunchOptions;
import org.eclipse.wst.ws.internal.explorer.WSExplorerLauncherCommand;
import org.eclipse.wst.ws.internal.explorer.plugin.ExplorerPlugin;
import org.eclipse.wst.ws.internal.monitor.GetMonitorCommand;
import org.eclipse.wst.ws.internal.parser.wsil.WebServicesParser;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings({"restriction", "rawtypes", "unchecked"})
public class TestWebServicesAction extends AbstractServerRunningAction {

	public TestWebServicesAction() {
	}

	public void run(IAction action) {
		if ((selectedServer == null) && (selectedModule == null)) {
			return;
		}

		URL webServicesListURL = null;
		String[] names = null;
		WebServicesHelper helper = null;

		if (selectedServer != null) {
			ILiferayServer portalServer = (ILiferayServer)selectedServer.loadAdapter(ILiferayServer.class, null);

			webServicesListURL = portalServer.getWebServicesListURL();

			helper = new WebServicesHelper(webServicesListURL);

			names = helper.getWebServiceNames();
		}
		else if (selectedModule != null) {
			IModule module = selectedModule.getModule()[0];

			module.getProject();

			IServer server = selectedModule.getServer();

			ILiferayServer portalServer = (ILiferayServer)server.loadAdapter(ILiferayServer.class, null);

			try {
				webServicesListURL = new URL(portalServer.getPortalHomeUrl(), module.getName() + "/axis");
			}
			catch (MalformedURLException murle) {
				LiferayServerUI.logError(murle);

				return;
			}

			helper = new WebServicesHelper(webServicesListURL);

			names = helper.getWebServiceNames();

			if (ListUtil.isEmpty(names)) {
				try {
					webServicesListURL = new URL(portalServer.getPortalHomeUrl(), module.getName() + "/api/axis");
				}
				catch (MalformedURLException murle) {
					LiferayServerUI.logError(murle);

					return;
				}

				helper = new WebServicesHelper(webServicesListURL);

				names = helper.getWebServiceNames();
			}
		}

		StringsFilteredDialog dialog = new StringsFilteredDialog(getActiveShell());

		dialog.setTitle(Msgs.webServiceSelection);
		dialog.setMessage(Msgs.selectWebService);
		dialog.setInput(names);

		int retval = dialog.open();

		if (retval == Window.OK) {
			Object serviceName = dialog.getFirstResult();

			String url = helper.getWebServiceWSDLURLByName(serviceName.toString());

			ExplorerPlugin explorerPlugin = ExplorerPlugin.getInstance();

			String stateLocation = explorerPlugin.getPluginStateLocation();
			String defaultFavoritesLocation = explorerPlugin.getDefaultFavoritesLocation();

			WSExplorerLauncherCommand command = new WSExplorerLauncherCommand();

			command.setForceLaunchOutsideIDE(false);

			Vector launchOptions = new Vector();

			addLaunchOptions(launchOptions, url, stateLocation, defaultFavoritesLocation);

			command.setLaunchOptions((LaunchOption[])launchOptions.toArray(new LaunchOption[0]));

			command.execute();
		}
	}

	protected void addLaunchOptions(
		Vector launchOptions, String wsdlURL, String stateLocation, String defaultFavoritesLocation) {

		GetMonitorCommand getMonitorCmd = new GetMonitorCommand();

		getMonitorCmd.setMonitorService(true);
		getMonitorCmd.setCreate(false);
		getMonitorCmd.setWebServicesParser(new WebServicesParser());
		getMonitorCmd.setWsdlURI(wsdlURL);
		getMonitorCmd.execute(null, null);

		List endpoints = getMonitorCmd.getEndpoints();

		for (Iterator endpointsIt = endpoints.iterator(); endpointsIt.hasNext();) {
			launchOptions.add(new LaunchOption(LaunchOptions.WEB_SERVICE_ENDPOINT, (String)endpointsIt.next()));
		}

		launchOptions.add(new LaunchOption(LaunchOptions.WSDL_URL, wsdlURL));
		launchOptions.add(new LaunchOption(LaunchOptions.STATE_LOCATION, stateLocation));
		launchOptions.add(new LaunchOption(LaunchOptions.DEFAULT_FAVORITES_LOCATION, defaultFavoritesLocation));
	}

	@Override
	protected int getRequiredServerState() {
		return IServer.STATE_STARTED;
	}

	private static class Msgs extends NLS {

		public static String selectWebService;
		public static String webServiceSelection;

		static {
			initializeMessages(TestWebServicesAction.class.getName(), Msgs.class);
		}

	}

}