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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;

import java.io.File;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Terry Jia
 */
public class PortalLanguagePropertiesCacheUtil {

	public static Properties getPortalLanguageProperties(ILiferayProject project) {
		if (project == null) {
			return new Properties();
		}

		Properties retval = null;

		try {
			ILiferayPortal portal = project.adapt(ILiferayPortal.class);

			if (portal == null) {
				IRuntime[] runtimes = ServerCore.getRuntimes();

				if (ListUtil.isNotEmpty(runtimes)) {
					for (IRuntime runtime : runtimes) {
						PortalBundle portalBundle = LiferayServerCore.newPortalBundle(runtime.getLocation());

						if (portalBundle != null) {
							portal = portalBundle;

							// find the first liferay 7 portal

							break;
						}
					}
				}

				if (portal == null) {
					return retval;
				}
			}

			IPath appServerPortalDir = portal.getAppServerPortalDir();

			retval = _languagePortalMap.get(appServerPortalDir);

			if (retval == null) {
				if (FileUtil.exists(appServerPortalDir)) {
					String[] implJarFolders = {"lib", "shielded-container-lib"};

					File implJar = Stream.of(
						implJarFolders
					).map(
						libFolder -> appServerPortalDir.append("/WEB-INF/" + libFolder + "/portal-impl.jar")
					).map(
						jarPath -> jarPath.toFile()
					).filter(
						file -> Objects.nonNull(file)
					).filter(
						file -> FileUtil.exists(file)
					).findFirst(
					).orElse(
						null
					);

					if (Objects.nonNull(implJar)) {
						try (JarFile jar = new JarFile(implJar);
							InputStream inputStream = jar.getInputStream(jar.getEntry("content/Language.properties"))) {

							retval = new Properties();

							retval.load(inputStream);
						}
					}
				}

				_languagePortalMap.put(appServerPortalDir, retval);
			}
		}
		catch (Exception e) {
			LiferayXMLSearchUI.logError("Unable to find portal language properties", e);
		}

		return retval;
	}

	private static final Map<IPath, Properties> _languagePortalMap = new HashMap<>();

}