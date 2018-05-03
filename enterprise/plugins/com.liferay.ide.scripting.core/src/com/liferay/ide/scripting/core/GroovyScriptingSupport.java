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

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.Map;

import org.eclipse.core.runtime.FileLocator;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("resource")
public class GroovyScriptingSupport {

	public URL getGroovyLibURL() {
		try {
			return FileLocator.toFileURL(ScriptingCore.getPluginEntry("/lib/groovy-all-1.7.5.jar"));
		}
		catch (IOException ioe) {
		}

		return null;
	}

	public Object newInstanceFromFile(File scriptFile) {
		GroovyClassLoader gcl = new GroovyClassLoader();

		try {
			return gcl.parseClass(scriptFile).newInstance();
		}
		catch (Exception e) {
			ScriptingCore.logError("Could not create script file.", e);
		}

		return null;
	}

	protected GroovyShell createGroovyShell(Map<String, Object> variableMap) {
		GroovyShell groovyShell = new GroovyShell(new GroovyClassLoader());

		if ((variableMap != null) && (variableMap.keySet() != null) && (variableMap.keySet().size() > 0)) {
			for (Map.Entry<String, Object> map : variableMap.entrySet()) {
				groovyShell.setVariable(map.getKey(), map.getValue());
			}
		}

		return groovyShell;
	}

}