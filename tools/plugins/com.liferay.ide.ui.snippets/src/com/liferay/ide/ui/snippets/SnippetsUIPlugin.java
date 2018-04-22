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

package com.liferay.ide.ui.snippets;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 * @author Gregory Amerson
 */
public class SnippetsUIPlugin extends AbstractUIPlugin {

	// The plugin ID

	public static final String PLUGIN_ID = "com.liferay.ide.ui.snippets";

	// The shared instance

	public static IStatus createErrorStatus(String string) {
		return new Status(IStatus.ERROR, PLUGIN_ID, string);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SnippetsUIPlugin getDefault() {
		return _plugin;
	}

	public static void logError(Exception e) {
		SnippetsUIPlugin plugin = getDefault();

		plugin.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

	/**
	 * The constructor
	 */
	public SnippetsUIPlugin() {
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see AbstractUIPlugin#start(org.osgi.framework. BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		_plugin = this;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see AbstractUIPlugin#stop(org.osgi.framework. BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private static SnippetsUIPlugin _plugin;

}