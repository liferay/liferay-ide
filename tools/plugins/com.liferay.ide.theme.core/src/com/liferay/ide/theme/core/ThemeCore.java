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

package com.liferay.ide.theme.core;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class ThemeCore extends Plugin {

	// The plugin ID

	public static final String PLUGIN_ID = "com.liferay.ide.theme.core";

	// The shared instance

	public static IStatus createErrorStatus(Exception ex) {
		return new Status(IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex);
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static IStatus createWarningStatus(String msg) {
		return new Status(IStatus.WARNING, PLUGIN_ID, msg);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ThemeCore getDefault() {
		return _plugin;
	}

	public static void logError(Exception ex) {
		ThemeCore plugin = getDefault();

		plugin.getLog().log(createErrorStatus(ex));
	}

	public static void logError(String msg) {
		ThemeCore plugin = getDefault();

		plugin.getLog().log(createErrorStatus(msg));
	}

	public static void logError(String msg, Exception e) {
		ThemeCore plugin = getDefault();

		plugin.getLog().log(createErrorStatus(msg, e));
	}

	/**
	 * The constructor
	 */
	public ThemeCore() {

		// themeDiffResourceListener = new ThemeDiffResourceListener();

	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);

		if (_themeDiffResourceListener != null) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			workspace.removeResourceChangeListener(_themeDiffResourceListener);
		}
	}

	private static ThemeCore _plugin;
	private static ThemeDiffResourceListener _themeDiffResourceListener;

}