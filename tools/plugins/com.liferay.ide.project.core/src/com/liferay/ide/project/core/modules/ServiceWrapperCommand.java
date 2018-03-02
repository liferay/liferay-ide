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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.TargetPlatformUtil;
import com.liferay.ide.server.core.portal.PortalRuntime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.apache.commons.lang.StringUtils;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Lovett Li
 */
public class ServiceWrapperCommand {

	public ServiceWrapperCommand(IServer server) {
		_server = server;
	}

	public ServiceWrapperCommand(IServer server, String serviceWrapperName) {
		_server = server;
		_serviceWrapperName = serviceWrapperName;
	}

	public ServiceContainer execute() throws Exception {
		if (_server == null) {
			return _getServiceWrapperFromTargetPlatform();
		}

		Map<String, String[]> dynamicServiceWrappers = _getDynamicServiceWrapper();
		ServiceContainer result;

		if (_serviceWrapperName == null) {
			result = new ServiceContainer(Arrays.asList(dynamicServiceWrappers.keySet().toArray(new String[0])));
		}
		else {
			String[] wrapperBundle = dynamicServiceWrappers.get(_serviceWrapperName);

			result = new ServiceContainer(wrapperBundle[0], wrapperBundle[1], wrapperBundle[2]);
		}

		return result;
	}

	private Map<String, String[]> _getDynamicServiceWrapper() throws IOException {
		PortalRuntime portalRuntime = (PortalRuntime)_server.getRuntime().loadAdapter(PortalRuntime.class, null);

		IPath bundleLibPath = portalRuntime.getAppServerLibGlobalDir();
		IPath bundleServerPath = portalRuntime.getAppServerDir();

		Map<String, String[]> map = new LinkedHashMap<>();

		List<File> libFiles;

		File portalkernelJar = null;

		try {
			libFiles = FileListing.getFileListing(new File(bundleLibPath.toOSString()));

			for (File lib : libFiles) {
				if (FileUtil.exists(lib) && lib.getName().endsWith("portal-kernel.jar")) {
					portalkernelJar = lib;

					break;
				}
			}

			libFiles = FileListing.getFileListing(new File(bundleServerPath.append("../osgi").toOSString()));

			libFiles.add(portalkernelJar);

			if (ListUtil.isNotEmpty(libFiles)) {
				for (File lib : libFiles) {
					if (lib.getName().endsWith(".lpkg")) {
						try (JarFile jar = new JarFile(lib)) {
							Enumeration<JarEntry> enu = jar.entries();

							while (enu.hasMoreElements()) {
								JarInputStream jarInputStream = null;

								try {
									JarEntry entry = enu.nextElement();

									String name = entry.getName();

									if (name.contains(".api-")) {
										JarEntry jarentry = jar.getJarEntry(name);

										InputStream inputStream = jar.getInputStream(jarentry);

										jarInputStream = new JarInputStream(inputStream);

										JarEntry nextJarEntry;

										while ((nextJarEntry = jarInputStream.getNextJarEntry()) != null) {
											String entryName = nextJarEntry.getName();

											_getServiceWrapperList(map, entryName, jarInputStream);
										}
									}
								}
								catch (Exception e) {
								}
								finally {
									if (jarInputStream != null) {
										jarInputStream.close();
									}
								}
							}
						}
					}
					else if (lib.getName().endsWith("api.jar") || lib.getName().equals("portal-kernel.jar")) {
						JarInputStream jarinput = null;

						try (JarFile jar = new JarFile(lib)) {
							jarinput = new JarInputStream(Files.newInputStream(lib.toPath()));

							Enumeration<JarEntry> enu = jar.entries();

							while (enu.hasMoreElements()) {
								JarEntry entry = enu.nextElement();

								String name = entry.getName();

								_getServiceWrapperList(map, name, jarinput);
							}
						}
						catch (IOException ioe) {
						}
						finally {
							if (jarinput != null) {
								jarinput.close();
							}
						}
					}
				}
			}
		}
		catch (FileNotFoundException fnfe) {
		}

		return map;
	}

	private ServiceContainer _getServiceWrapperFromTargetPlatform() throws Exception {
		ServiceContainer result;

		if (_serviceWrapperName == null) {
			result = TargetPlatformUtil.getServiceWrapperList();
		}
		else {
			result = TargetPlatformUtil.getServiceWrapperBundle(_serviceWrapperName);
		}

		return result;
	}

	private void _getServiceWrapperList(Map<String, String[]> wrapperMap, String name, JarInputStream jarInputStream) {
		if (name.endsWith("ServiceWrapper.class") && !(name.contains("$"))) {
			name = name.replaceAll("\\\\", ".").replaceAll("/", ".");

			name = name.substring(0, name.lastIndexOf("."));

			Attributes mainAttributes = jarInputStream.getManifest().getMainAttributes();

			String bundleName = mainAttributes.getValue("Bundle-SymbolicName");
			String version = mainAttributes.getValue("Bundle-Version");

			String group = "";

			if (bundleName.equals("com.liferay.portal.kernel")) {
				group = "com.liferay.portal";
			}
			else {
				int ordinalIndexOf = StringUtils.ordinalIndexOf(bundleName, ".", 2);

				if (ordinalIndexOf != -1) {
					group = bundleName.substring(0, ordinalIndexOf);
				}
			}

			wrapperMap.put(name, new String[] {group, bundleName, version});
		}
	}

	private final IServer _server;
	private String _serviceWrapperName;

}