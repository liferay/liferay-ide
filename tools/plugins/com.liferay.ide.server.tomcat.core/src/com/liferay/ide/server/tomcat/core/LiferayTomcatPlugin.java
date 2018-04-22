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

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.util.FileUtil;

import java.io.File;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 */
public class LiferayTomcatPlugin extends Plugin {

	public static final String PLUGIN_ID = "com.liferay.ide.server.tomcat.core";

	public static final String PREFERENCES_ADDED_EXT_PLUGIN_TOGGLE_KEY = "ADDED_EXT_PLUGIN_TOGGLE_KEY";

	public static final String PREFERENCES_ADDED_EXT_PLUGIN_WITHOUT_ZIP_TOGGLE_KEY =
		"ADDED_EXT_PLUGIN_WITHOUT_ZIP_TOGGLE_KEY";

	public static final String PREFERENCES_EE_UPGRADE_MSG_TOGGLE_KEY = "EE_UPGRADE_MSG_TOGGLE_KEY";

	public static final String PREFERENCES_REMOVE_EXT_PLUGIN_TOGGLE_KEY = "REMOVE_EXT_PLUGIN_TOGGLE_KEY";

	public static IStatus createErrorStatus(Exception e) {
		return createErrorStatus(PLUGIN_ID, e);
	}

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg) {
		return new Status(IStatus.ERROR, pluginId, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg, Throwable e) {
		return new Status(IStatus.ERROR, pluginId, msg, e);
	}

	public static IStatus createErrorStatus(String pluginId, Throwable t) {
		return new Status(IStatus.ERROR, pluginId, t.getMessage(), t);
	}

	public static IStatus createWarningStatus(String message) {
		return new Status(IStatus.WARNING, PLUGIN_ID, message);
	}

	public static IStatus createWarningStatus(String message, String id) {
		return new Status(IStatus.WARNING, id, message);
	}

	public static IStatus createWarningStatus(String message, String id, Exception e) {
		return new Status(IStatus.WARNING, id, message, e);
	}

	public static LiferayTomcatPlugin getDefault() {
		return _plugin;
	}

	public static IEclipsePreferences getPreferenceStore() {
		return DefaultScope.INSTANCE.getNode(PLUGIN_ID);
	}

	public static void logError(IStatus status) {
		ILog log = getDefault().getLog();

		log.log(status);
	}

	public static void logError(String msg) {
		logError(createErrorStatus(msg));
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(PLUGIN_ID, msg, e));
	}

	public static void logError(Throwable t) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t));
	}

	public static IStatus warning(String msg) {
		return createWarningStatus(msg, PLUGIN_ID);
	}

	public static IStatus warning(String msg, Exception e) {
		return createWarningStatus(msg, PLUGIN_ID, e);
	}

	public LiferayTomcatPlugin() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;

		/*
		 * portalSourcePartListener = new PortalSourcePartListener();
		 * PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
		 *
		 * public void run() {
		 * IWorkbenchWindow workbenchWindow =
		 * PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		 *
		 * if (workbenchWindow != null) {
		 * workbenchWindow.getPartService().addPartListener((IPartListener2)
		 * portalSourcePartListener);
		 * }
		 * }
		 * });
		 *
		 * PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
		 *
		 * public void windowActivated(IWorkbenchWindow window) {
		 * }
		 *
		 * public void windowClosed(IWorkbenchWindow window) {
		 * }
		 *
		 * public void windowDeactivated(IWorkbenchWindow window) {
		 * }
		 *
		 * public void windowOpened(IWorkbenchWindow window) {
		 * window.getPartService().addPartListener((IPartListener2)
		 * }
		 * });
		 */
		_cleanupVersionFiles();
	}

	public void stop(BundleContext context) throws Exception {
		_cleanupVersionFiles();

		_plugin = null;

		super.stop(context);
	}

	private void _cleanupVersionFiles() {
		IPath liferayTomcatPluginLocation = getDefault().getStateLocation();

		IPath versionPropertiesPath = liferayTomcatPluginLocation.append("version.properties");

		File versionProps = versionPropertiesPath.toFile();

		if (FileUtil.exists(versionProps)) {
			if (!versionProps.delete()) {
				versionProps.deleteOnExit();
			}
		}

		IPath versionTxtPath = liferayTomcatPluginLocation.append("version.txt");

		File versionTxt = versionTxtPath.toFile();

		if (FileUtil.exists(versionTxt)) {
			if (!versionTxt.delete()) {
				versionTxt.deleteOnExit();
			}
		}

		IPath serverInfosPropertiesPath = liferayTomcatPluginLocation.append("serverInfos.properties");

		File serverInfosProperties = serverInfosPropertiesPath.toFile();

		if (FileUtil.exists(serverInfosProperties)) {
			if (!serverInfosProperties.delete()) {
				serverInfosProperties.deleteOnExit();
			}
		}

		IPath serverInfoTxtPath = liferayTomcatPluginLocation.append("serverInfo.txt");

		File serverInfosTxt = serverInfoTxtPath.toFile();

		if (FileUtil.exists(serverInfosTxt)) {
			if (!serverInfosTxt.delete()) {
				serverInfosTxt.deleteOnExit();
			}
		}
	}

	private static LiferayTomcatPlugin _plugin;

}