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

package com.liferay.ide.scripting.core;

import java.net.URL;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class ScriptingCore extends Plugin {

	public static final String PLUGIN_ID = "com.liferay.ide.scripting.core";

	public static IStatus createErrorStatus(String msg, Throwable e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static ScriptingCore getDefault() {
		return _plugin;
	}

	public static GroovyScriptingSupport getGroovyScriptingSupport() {
		if (_groovyScriptingSupport == null) {
			_groovyScriptingSupport = new GroovyScriptingSupport();
		}

		return _groovyScriptingSupport;
	}

	public static URL getPluginEntry(String path) {
		Bundle bundle = getDefault().getBundle();

		return bundle.getEntry(path);
	}

	public static void logError(String msg, Throwable e) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(msg, e));
	}

	public ScriptingCore() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		super.stop(context);
	}

	private static GroovyScriptingSupport _groovyScriptingSupport;
	private static ScriptingCore _plugin;

}