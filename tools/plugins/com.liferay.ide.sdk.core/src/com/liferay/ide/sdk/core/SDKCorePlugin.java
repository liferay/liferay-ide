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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.internal.IMemento;
import org.eclipse.wst.server.core.internal.XMLMemento;

import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class SDKCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "com.liferay.ide.sdk.core";

	public static final String PREF_KEY_OVERWRITE_USER_BUILD_FILE = "OVERWRITE_USER_BUILD_FILE";

	public static final String PREF_KEY_SDK_NAME = "sdk-name";

	public static final String PREFERENCE_ID = "com.liferay.ide.eclipse.sdk";

	public static IStatus createErrorStatus(String msg) {
		return LiferayCore.createErrorStatus(PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg) {
		return new Status(IStatus.ERROR, pluginId, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg, Throwable e) {
		return new Status(IStatus.ERROR, pluginId, msg, e);
	}

	public static IStatus createErrorStatus(Throwable t) {
		return LiferayCore.createErrorStatus(PLUGIN_ID, t);
	}

	public static SDKCorePlugin getDefault() {
		return _plugin;
	}

	public static void logError(Exception e) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

	public static void logError(String msg, Throwable t) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(PLUGIN_ID, msg, t));
	}

	public SDKCorePlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		_plugin = this;

		_sdkListener = new ISDKListener() {

			@Override
			public void sdksAdded(SDK[] sdks) {
				_saveGlobalSDKSettings(sdks);
			}

			@Override
			public void sdksChanged(SDK[] sdks) {
				_saveGlobalSDKSettings(sdks);
			}

			@Override
			public void sdksRemoved(SDK[] sdks) {
				_saveGlobalSDKSettings(sdks);
			}

		};

		SDKManager.getInstance().addSDKListener(_sdkListener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		IPath sdkPluginLocation = getDefault().getStateLocation();

		File createDir = sdkPluginLocation.append("create").toFile();

		if (createDir.exists()) {
			FileUtil.deleteDir(createDir, true);
		}

		_plugin = null;
		super.stop(context);
	}

	private void _addSDKToMemento(SDK sdk, IMemento memento) {
		memento.putString("name", sdk.getName());
		memento.putString("location", sdk.getLocation().toPortableString());
		memento.putBoolean("default", sdk.isDefault());
	}

	private void _copyMemento(IMemento from, IMemento to) {
		for (String name : from.getNames()) {
			to.putString(name, from.getString(name));
		}
	}

	private synchronized void _saveGlobalSDKSettings(SDK[] sdks) {
		try {
			LiferayCore.GLOBAL_SETTINGS_PATH.toFile().mkdirs();

			File sdkGlobalFile = LiferayCore.GLOBAL_SETTINGS_PATH.append("sdks.xml").toFile();

			Set<IMemento> existing = new HashSet<>();

			if (sdkGlobalFile.exists()) {
				try {
					try (InputStream newInputStream = Files.newInputStream(sdkGlobalFile.toPath())) {
						IMemento existingMemento = XMLMemento.loadMemento(newInputStream);

						if (existingMemento != null) {
							IMemento[] children = existingMemento.getChildren("sdk");

							if (ListUtil.isNotEmpty(children)) {
								for (IMemento child : children) {
									IPath loc = Path.fromPortableString(child.getString("location"));

									if ((loc != null) && loc.toFile().exists()) {
										boolean duplicate = false;

										for (SDK sdk : sdks) {
											IPath sdkLocation = sdk.getLocation();

											if (sdkLocation.toFile().equals(loc.toFile())) {
												duplicate = true;
												break;
											}
										}

										if (!duplicate) {
											existing.add(child);
										}
									}
								}
							}
						}
					}
				}
				catch (Exception e) {
				}
			}

			XMLMemento sdkMementos = XMLMemento.createWriteRoot("sdks");

			for (IMemento exist : existing) {
				_copyMemento(exist, sdkMementos.createChild("sdk"));
			}

			for (SDK sdk : sdks) {
				IMemento memento = sdkMementos.createChild("sdk");

				_addSDKToMemento(sdk, memento);
			}

			OutputStream fos = Files.newOutputStream(sdkGlobalFile.toPath());

			sdkMementos.save(fos);
		}
		catch (Exception e) {
			logError("Unable to save global sdk settings", e);
		}
	}

	private static SDKCorePlugin _plugin;

	private ISDKListener _sdkListener;

}