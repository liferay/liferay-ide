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

package com.liferay.ide.server.ui;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Method;

import java.util.Collections;
import java.util.Date;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.core.INotificationService;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.commons.notifications.ui.NotificationsUi;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.Base;
import org.eclipse.wst.server.core.internal.IMemento;
import org.eclipse.wst.server.core.internal.IStartup;
import org.eclipse.wst.server.core.internal.ResourceManager;
import org.eclipse.wst.server.core.internal.Runtime;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.XMLMemento;
import org.eclipse.wst.server.core.model.ServerDelegate;

import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class ServerStartup implements IStartup {

	public void startup() {
		if (_shouldCheckForGlobalSettings() && _hasGlobalSettings()) {
			INotificationService notifacationService = NotificationsUi.getService();

			notifacationService.notify(Collections.singletonList(_createImportGlobalSettingsNotification()));

			try {
				IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(LiferayServerUI.PLUGIN_ID);

				prefs.putBoolean(_GLOBAL_SETTINGS_CHECKED, true);
				prefs.flush();
			}
			catch (BackingStoreException bse) {
				LiferayServerUI.logError("Unable to persist global-setting-checked pref", bse);
			}
		}
	}

	private AbstractNotification _createImportGlobalSettingsNotification() {
		Date date = new Date();

		return new AbstractUiNotification("com.liferay.ide.server.ui.importglobalsettings") {

			@SuppressWarnings({"rawtypes", "unchecked"})
			public Object getAdapter(Class adapter) {
				return null;
			}

			@Override
			public Date getDate() {
				return date;
			}

			@Override
			public String getDescription() {
				return "Click above to learn more about importing those settings.";
			}

			@Override
			public String getLabel() {
				return "Previous Liferay IDE settings have been detected";
			}

			@Override
			public Image getNotificationImage() {
				LiferayServerUI serverUi = LiferayServerUI.getDefault();

				ImageRegistry imageRegistry = serverUi.getImageRegistry();

				return imageRegistry.get(LiferayServerUI.IMG_NOTIFICATION);
			}

			@Override
			public Image getNotificationKindImage() {
				return null;
			}

			@Override
			public void open() {
				IWorkbench workbench = PlatformUI.getWorkbench();

				IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();

				boolean importSettings = MessageDialog.openQuestion(
					activeWorkbenchWindow.getShell(), "Previous Liferay IDE Settings Detected",
					"Settings from a previous Liferay IDE workspace have been detected such as: Plugins SDKS," +
						"Liferay runtimes, or Liferay servers. Do you want to import these settings now?");

				if (importSettings) {
					_importGlobalSettings();
				}
			}

		};
	}

	private SDK _createSDKfromMemento(IMemento memento) {
		SDK sdk = new SDK();

		sdk.setName(memento.getString("name"));

		IPath location = Path.fromPortableString(memento.getString("location"));

		sdk.setLocation(location.makeAbsolute());

		return sdk;
	}

	private boolean _hasGlobalSettings() {
		File globalSettingsDir = LiferayCore.GLOBAL_SETTINGS_PATH.toFile();

		if (FileUtil.exists(globalSettingsDir)) {
			File[] settings = globalSettingsDir.listFiles(
				new FilenameFilter() {

					public boolean accept(File dir, String name) {
						String lowerCaseName = name.toLowerCase();

						return lowerCaseName.endsWith(".xml");
					}

				});

			return ListUtil.isNotEmpty(settings);
		}

		return false;
	}

	private void _importGlobalRuntimes(File runtimesFile) {
		try (InputStream inputStream = new FileInputStream(runtimesFile)) {
			IMemento runtimesMemento = XMLMemento.loadMemento(inputStream);

			if (runtimesMemento != null) {
				ResourceManager resourceManager = ResourceManager.getInstance();

				IMemento[] mementos = runtimesMemento.getChildren("runtime");

				if (ListUtil.isNotEmpty(mementos)) {
					for (IMemento memento : mementos) {
						Runtime runtime = new Runtime(null);

						try {
							Method loadFromMemento = Base.class.getDeclaredMethod(
								"loadFromMemento", IMemento.class, IProgressMonitor.class);

							if (loadFromMemento != null) {
								loadFromMemento.setAccessible(true);
								loadFromMemento.invoke(runtime, memento, null);

								if (ServerCore.findRuntime(runtime.getId()) == null) {
									Method addRuntime = ResourceManager.class.getDeclaredMethod(
										"addRuntime", IRuntime.class);

									if (addRuntime != null) {
										addRuntime.setAccessible(true);
										addRuntime.invoke(resourceManager, runtime);
									}
								}
							}
						}
						catch (Exception e) {
							LiferayServerUI.logError("Unable to load runtime from memento", e);
						}
					}
				}
			}
		}
		catch (IOException ioe) {
		}
	}

	private void _importGlobalSDKs(File sdksFile) {
		try (InputStream inputStream = new FileInputStream(sdksFile)) {
			SDKManager manager = SDKManager.getInstance();

			IMemento sdksMemento = XMLMemento.loadMemento(inputStream);

			if (sdksMemento != null) {
				IMemento[] sdks = sdksMemento.getChildren("sdk");

				if (ListUtil.isNotEmpty(sdks)) {
					for (IMemento sdkMemento : sdks) {
						SDK newSDK = _createSDKfromMemento(sdkMemento);

						if (newSDK != null) {
							SDK existingSDK = manager.getSDK(newSDK.getName());

							if (existingSDK == null) {
								manager.addSDK(newSDK);
							}
						}
					}
				}
			}
		}
		catch (IOException ioe) {
		}
	}

	private void _importGlobalServers(File serversFile) {
		try (InputStream inputStream = new FileInputStream(serversFile)) {
			IMemento serversMemento = XMLMemento.loadMemento(inputStream);

			if (serversMemento != null) {
				ResourceManager resourceManager = ResourceManager.getInstance();

				IMemento[] mementos = serversMemento.getChildren("server");

				if (ListUtil.isNotEmpty(mementos)) {
					for (IMemento memento : mementos) {
						Server server = new Server(null);

						try {
							Method loadFromMemento = Base.class.getDeclaredMethod(
								"loadFromMemento", IMemento.class, IProgressMonitor.class);

							if (loadFromMemento != null) {
								loadFromMemento.setAccessible(true);
								loadFromMemento.invoke(server, memento, null);

								if (ServerCore.findServer(server.getId()) == null) {
									Method addServer = ResourceManager.class.getDeclaredMethod(
										"addServer", IServer.class);

									if (addServer != null) {
										addServer.setAccessible(true);
										addServer.invoke(resourceManager, server);

										IServerWorkingCopy wc = server.createWorkingCopy();

										ServerDelegate delegate = (ServerDelegate)wc.loadAdapter(
											ServerDelegate.class, null);

										delegate.importRuntimeConfiguration(wc.getRuntime(), null);

										wc.save(true, null);
									}
								}
							}
						}
						catch (Exception e) {
							LiferayServerUI.logError("Unable to load server from memento", e);
						}
					}
				}
			}
		}
		catch (IOException ioe) {
		}
	}

	private void _importGlobalSettings() {
		File settingsDir = LiferayCore.GLOBAL_SETTINGS_PATH.toFile();

		if (FileUtil.exists(settingsDir)) {
			File sdks = new File(settingsDir, "sdks.xml");

			if (FileUtil.exists(sdks)) {
				_importGlobalSDKs(sdks);
			}

			File runtimes = new File(settingsDir, "runtimes.xml");

			if (FileUtil.exists(runtimes)) {
				_importGlobalRuntimes(runtimes);
			}

			File servers = new File(settingsDir, "servers.xml");

			if (FileUtil.exists(servers)) {
				_importGlobalServers(servers);
			}
		}
	}

	private boolean _shouldCheckForGlobalSettings() {
		return false;
	}

	private static final String _GLOBAL_SETTINGS_CHECKED = "global-settings-checked";

}