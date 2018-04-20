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

package com.liferay.ide.server.util;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.io.FilenameFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;

import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class LiferayPortalValueLoader {

	public LiferayPortalValueLoader(IPath appServerPortalDir, IPath[] extraLibs) {
		_portalDir = appServerPortalDir;
		_userLibs = extraLibs;
	}

	public LiferayPortalValueLoader(IPath[] extraLibs) {
		_userLibs = extraLibs;
	}

	public String[] loadHookPropertiesFromClass() {
		String loadClassName = "com.liferay.portal.deploy.hot.HookHotDeployListener";
		String fieldName = "SUPPORTED_PROPERTIES";

		return (String[])_getFieldValuesFromClass(loadClassName, fieldName);
	}

	public String loadServerInfoFromClass() {
		String loadClassName = "com.liferay.portal.kernel.util.ReleaseInfo";
		String methodName = "getServerInfo";

		return (String)_getMethodValueFromClass(loadClassName, methodName);
	}

	public Version loadVersionFromClass() {
		String loadClassName = "com.liferay.portal.kernel.util.ReleaseInfo";
		String methodName = "getVersion";

		Version retval = null;

		try {
			String versionString = (String)_getMethodValueFromClass(loadClassName, methodName);

			retval = Version.parseVersion(versionString);
		}
		catch (Exception e) {
			retval = Version.emptyVersion;
			LiferayServerCore.logError("Error unable to find " + loadClassName, e);
		}

		return retval;
	}

	private void _addLibs(File libDir, List<URL> libUrlList) throws MalformedURLException {
		if (FileUtil.exists(libDir)) {
			File[] libs = libDir.listFiles(
				new FilenameFilter() {

					@Override
					public boolean accept(File dir, String fileName) {
						String fileNameLowerCase = fileName.toLowerCase();

						return fileNameLowerCase.endsWith(".jar");
					}

				});

			if (ListUtil.isNotEmpty(libs)) {
				for (File portaLib : libs) {
					URI portalLibUri = portaLib.toURI();

					libUrlList.add(portalLibUri.toURL());
				}
			}
		}
	}

	private Object[] _getFieldValuesFromClass(String loadClassName, String fieldName) {
		Object[] retval = null;

		try {
			Class<?> classRef = _loadClass(loadClassName);

			Field propertiesField = classRef.getDeclaredField(fieldName);

			retval = (Object[])propertiesField.get(propertiesField);
		}
		catch (Exception e) {
			retval = new Object[0];
			LiferayServerCore.logError("Error unable to find " + loadClassName, e);
		}

		return retval;
	}

	private Object _getMethodValueFromClass(String loadClassName, String methodName) {
		Object retval = null;

		try {
			Class<?> classRef = _loadClass(loadClassName);

			Method method = classRef.getMethod(methodName);

			retval = method.invoke(null);
		}
		catch (Exception e) {
			LiferayServerCore.logError("Error unable to find " + loadClassName, e);
		}

		return retval;
	}

	@SuppressWarnings("resource")
	private Class<?> _loadClass(String className) throws Exception {
		List<URL> libUrlList = new ArrayList<>();

		if (_portalDir != null) {
			IPath libPath = _portalDir.append("WEB-INF/lib");

			File libDir = libPath.toFile();

			_addLibs(libDir, libUrlList);
		}

		if (ListUtil.isNotEmpty(_userLibs)) {
			for (IPath url : _userLibs) {
				URI uri = new File(url.toOSString()).toURI();

				libUrlList.add(uri.toURL());
			}
		}

		URL[] urls = libUrlList.toArray(new URL[libUrlList.size()]);

		return new URLClassLoader(urls).loadClass(className);
	}

	private IPath _portalDir;
	private IPath[] _userLibs;

}