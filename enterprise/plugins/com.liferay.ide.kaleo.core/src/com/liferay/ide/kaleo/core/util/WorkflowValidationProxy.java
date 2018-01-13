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

package com.liferay.ide.kaleo.core.util;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.scripting.core.GroovyScriptProxy;
import com.liferay.ide.server.core.ILiferayRuntime;

import java.io.File;
import java.io.FilenameFilter;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IRuntime;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class WorkflowValidationProxy extends GroovyScriptProxy {

	public WorkflowValidationProxy(IRuntime runtime) {
		_runtime = runtime;
	}

	protected File getGroovyFile() throws Exception {
		Object loadAdapter = _runtime.loadAdapter(ILiferayRuntime.class, null);

		ILiferayRuntime liferayRuntime = (ILiferayRuntime)loadAdapter;

		KaleoCore kaleoCore = KaleoCore.getDefault();

		if (liferayRuntime != null) {
			Version version = new Version(liferayRuntime.getPortalVersion());

			Bundle bundle = kaleoCore.getBundle();

			URL bundleURL = FileLocator.toFileURL(bundle.getEntry(_getGroovyWorkflowValidationScript(version)));

			return new File(bundleURL.getFile());
		}

		IStatus error = KaleoCore.createErrorStatus("Unable to locate groovy script");

		ILog log = kaleoCore.getLog();

		log.log(error);

		throw new CoreException(error);
	}

	@SuppressWarnings("deprecation")
	protected URL[] getProxyClasspath() throws CoreException {
		List<URL> scriptUrlList = new ArrayList<>();

		IRuntime serverRuntime = _runtime;

		if (serverRuntime == null) {
			throw new CoreException(KaleoCore.createErrorStatus("Could not get server runtime."));
		}

		ILiferayRuntime liferayRuntime = (ILiferayRuntime)serverRuntime.loadAdapter(ILiferayRuntime.class, null);

		IPath appServerPortalDir = liferayRuntime.getAppServerPortalDir();

		IPath appServerPortalLibDir = appServerPortalDir.append("WEB-INF/lib");

		File libFolder = appServerPortalLibDir.toFile();

		FilenameFilter fileNameFilter = new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (name.endsWith(".jar") &&
					(name.contains("dom4j") || name.contains("xercesImpl") || name.contains("portal-impl") ||
					 name.contains("util-java") || name.contains("commons-lang"))) {

					return true;
				}

				return false;
			}

		};

		String[] libs = libFolder.list(fileNameFilter);

		for (String lib : libs) {
			File libJar = new File(libFolder, lib);

			if (FileUtil.exists(libJar)) {
				try {
					scriptUrlList.add(libJar.toURL());
				}
				catch (MalformedURLException murle) {
				}
			}
		}

		IPath runtimePath = _runtime.getLocation();

		IPath kaleoWebServiceJarDir = runtimePath.append(
			"webapps/kaleo-designer-portlet/WEB-INF/lib/kaleo-web-service.jar");

		IPath portalServiceJarDir = runtimePath.append("lib/ext/portal-service.jar");

		File[] jars = {kaleoWebServiceJarDir.toFile(), portalServiceJarDir.toFile()};

		for (File jar : jars) {
			if (FileUtil.exists(jar)) {
				try {
					scriptUrlList.add(jar.toURL());
				}
				catch (MalformedURLException murle) {
				}
			}
		}

		IPath classesDir = runtimePath.append("webapps/kaleo-web/WEB-INF/classes/");

		File parserDir = classesDir.toFile();

		if (FileUtil.exists(parserDir)) {
			try {
				scriptUrlList.add(parserDir.toURL());
			}
			catch (Exception e) {
			}
		}

		return scriptUrlList.toArray(new URL[0]);
	}

	private String _getGroovyWorkflowValidationScript(Version runtimeVersion) {
		int result = runtimeVersion.compareTo(new Version(6, 1, 30));

		if (result > 0) {
			return "/scripts/portal/WorkflowValidation620.groovy";
		}
		else if (result == 0) {
			return "/scripts/portal/WorkflowValidation6130.groovy";
		}
		else {
			return "/scripts/portal/WorkflowValidation6120.groovy";
		}
	}

	private IRuntime _runtime;

}