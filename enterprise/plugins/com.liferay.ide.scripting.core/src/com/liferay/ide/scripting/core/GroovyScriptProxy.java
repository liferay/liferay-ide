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

import java.io.File;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;

/**
 * @author Gregory Amerson
 */
public abstract class GroovyScriptProxy implements InvocationHandler {

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object retval = null;
		Throwable error = null;

		configureClassloader();

		try {
			Object serviceObject = getServiceObject();

			Class<?> serviceClass = serviceObject.getClass();

			Method serviceMethod = serviceClass.getMethod(method.getName(), method.getParameterTypes());

			retval = serviceMethod.invoke(serviceObject, args);
		}
		catch (Throwable t) {
			ScriptingCore.logError("Error in script method " + method.getName(), t);
			error = t;
		}
		finally {
			unconfigureClassloader();
		}

		if (error != null) {
			throw new RuntimeException("Error in workflow validation proxy.", error.getCause());
		}

		return retval;
	}

	protected void configureClassloader() throws CoreException {
		if (proxyClassLoader == null) {
			proxyClassLoader = createClassLoader();
		}

		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

		if (currentClassLoader.equals(proxyClassLoader)) {
			return;
		}
		else {
			previousClassLoader = currentClassLoader;
			Thread.currentThread().setContextClassLoader(proxyClassLoader);
		}
	}

	protected URLClassLoader createClassLoader() throws CoreException {
		List<URL> urls = new ArrayList<>();

		urls.add(ScriptingCore.getGroovyScriptingSupport().getGroovyLibURL());

		for (URL url : getProxyClasspath()) {
			urls.add(url);
		}

		return new URLClassLoader(urls.toArray(new URL[0]), Platform.class.getClassLoader());
	}

	protected abstract File getGroovyFile() throws Exception;

	protected abstract URL[] getProxyClasspath() throws CoreException;

	protected Object getServiceObject() throws Exception {
		if (serviceObject == null) {
			serviceObject = ScriptingCore.getGroovyScriptingSupport().newInstanceFromFile(getGroovyFile());
		}

		return serviceObject;
	}

	protected void unconfigureClassloader() {
		if (previousClassLoader != null) {
			Thread.currentThread().setContextClassLoader(previousClassLoader);
		}
		else {
			Class<?> proxyClass = getClass();

			Thread.currentThread().setContextClassLoader(proxyClass.getClassLoader());
		}
	}

	protected ClassLoader previousClassLoader;
	protected URLClassLoader proxyClassLoader;
	protected Object serviceObject;

}