/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.scripting.core;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;

/**
 * @author Greg Amerson
 */
public class GroovyScriptingSupport {

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

	// public Object evaluateScriptText(
	// final String scriptText, final Map<String, Object> variableMap, final URL[] classpath) {
	// final Object[] retval = new Object[1];
	//
	// Job script = new Job("Groovy script") {
	//
	// @Override
	// protected IStatus run(IProgressMonitor monitor) {
	// ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
	//
	// ClassLoader scriptClassLoader = createClassLoader(classpath, threadClassLoader);
	//
	// Thread.currentThread().setContextClassLoader(scriptClassLoader);
	//
	// retval[0] = createGroovyShell(variableMap).evaluate(scriptText);
	//
	// Thread.currentThread().setContextClassLoader(threadClassLoader);
	//
	// return Status.OK_STATUS;
	// }
	//
	// };
	//
	// script.setSystem(true);
	// script.schedule();
	//
	// try {
	// script.join();
	// }
	// catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// return retval[0];
	// }




	protected GroovyShell createGroovyShell(Map<String, Object> variableMap) {
		GroovyShell groovyShell = new GroovyShell(new GroovyClassLoader());

		if (variableMap != null && variableMap.keySet() != null && variableMap.keySet().size() > 0) {
			for (String key : variableMap.keySet()) {
				groovyShell.setVariable(key, variableMap.get(key));
			}
		}

		return groovyShell;
	}

	public URL getGroovyLibURL() {
		try {
			return FileLocator.toFileURL(ScriptingCore.getPluginEntry("/lib/groovy-all-1.7.5.jar"));
		}
		catch (IOException e) {
		}

		return null;
	}

}
