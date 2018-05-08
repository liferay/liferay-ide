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

package com.liferay.ide.maven.ui;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class LiferayMavenUI extends Plugin {

	// The plug-in ID

	public static final String PLUGIN_ID = "com.liferay.ide.maven.ui";

	// The shared instance

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LiferayMavenUI getDefault() {
		return _plugin;
	}

	public static void logError(String msg, Throwable t) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, msg, t));
	}

	public static void logError(Throwable t) {
		logError(t.getMessage(), t);
	}

	/**
	 * The constructor
	 */
	public LiferayMavenUI() {
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 *      BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private static LiferayMavenUI _plugin;

}