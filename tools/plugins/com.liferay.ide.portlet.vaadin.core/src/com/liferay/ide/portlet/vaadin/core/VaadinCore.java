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

package com.liferay.ide.portlet.vaadin.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Greg Amerson
 */
public class VaadinCore extends Plugin {

	// The plugin ID

	public static final String PLUGIN_ID = "com.liferay.ide.portlet.vaadin.core";

	// The shared instance

	public static IStatus createErrorStatus(Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e);
	}

	public static IStatus createErrorStatus(String message) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message);
	}

	public static IStatus createWarningStatus(String message) {
		return new Status(IStatus.WARNING, PLUGIN_ID, message);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static VaadinCore getDefault() {
		return _plugin;
	}

	public static void logError(Exception e) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(e));
	}

	/**
	 * The constructor
	 */
	public VaadinCore() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private static VaadinCore _plugin;

}