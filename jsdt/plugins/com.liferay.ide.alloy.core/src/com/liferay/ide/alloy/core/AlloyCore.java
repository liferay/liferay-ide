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

package com.liferay.ide.alloy.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 */
public class AlloyCore extends Plugin {

	// The shared instance

	public static final String PLUGIN_ID = "com.liferay.ide.alloy.core";

	// The plugin ID

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
	public static AlloyCore getDefault() {
		return _plugin;
	}

	public static void logError(Exception ex) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(ex));
	}

	public static void logError(String msg) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(msg));
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(msg, e));
	}

	/**
	 * The constructor
	 */
	public AlloyCore() {
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		_plugin = this;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private static AlloyCore _plugin;

}