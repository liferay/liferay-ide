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

package com.liferay.ide.server.tomcat.ui;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class LiferayTomcatUIPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.liferay.ide.server.tomcat.ui";

	public static IStatus createErrorStatus(Exception ex) {
		return new Status(IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex);
	}

	public static LiferayTomcatUIPlugin getDefault() {
		return _plugin;
	}

	public static void logError(Exception ex) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(ex));
	}

	public LiferayTomcatUIPlugin() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private static LiferayTomcatUIPlugin _plugin;

}