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

package com.liferay.ide.idea.core;

import com.liferay.ide.idea.util.*;

import java.io.File;
import java.io.FileNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public abstract class AbstractPortalBundle implements PortalBundle {

	public AbstractPortalBundle(Path path) {
		if (path == null) {
			throw new IllegalArgumentException("path cannot be null");
		}

		bundlePath = path;

		liferayHome = Paths.get(bundlePath.toString(), "..");

		autoDeployPath = Paths.get(liferayHome.toString(), "deploy");

		modulesPath = Paths.get(liferayHome.toString(), "osgi");
	}

	@Override
	public Path getAppServerDir() {
		return bundlePath;
	}

	@Override
	public Path getAutoDeployPath() {
		return autoDeployPath;
	}

	@Override
	public Path[] getBundleDependencyJars() {
		final List<Path> libs = new ArrayList<>();
		Path bundleLibPath = getAppServerLibDir();
		List<File> libFiles;

		try {
			libFiles = FileListing.getFileListing(new File(bundleLibPath.toString()));

			for (File lib : libFiles) {
				if (lib.exists() && lib.getName().endsWith(".jar")) {
					libs.add(lib.toPath());
				}
			}
		}
		catch (FileNotFoundException fnfe) {
		}

		return libs.toArray(new Path[libs.size()]);
	}

	@Override
	public String[] getHookSupportedProperties() {
		Path portalDir = getAppServerPortalDir();
		Path[] extraLibs = getBundleDependencyJars();

		return new LiferayPortalValueLoader(portalDir, extraLibs).loadHookPropertiesFromClass();
	}

	@Override
	public int getJmxRemotePort() {
		return getDefaultJMXRemotePort();
	}

	@Override
	public Path getLiferayHome() {
		return liferayHome;
	}

	@Override
	public Path getModulesPath() {
		return modulesPath;
	}

	@Override
	public Path getOSGiBundlesDir() {
		if (liferayHome == null) {
			return null;
		}

		return Paths.get(liferayHome.toString(), "osgi");
	}

	@Override
	public Properties getPortletCategories() {
		return null;
	}

	@Override
	public Properties getPortletEntryCategories() {
		return null;
	}

	@Override
	public String getVersion() {
		return "";
	}

	protected abstract Path getAppServerLibDir();

	protected abstract int getDefaultJMXRemotePort();

	protected String getHttpPortValue(
		File xmlFile, String tagName, String attriName, String attriValue, String targetName) {

		DocumentBuilder db = null;

		DocumentBuilderFactory dbf = null;

		try {
			dbf = DocumentBuilderFactory.newInstance();

			db = dbf.newDocumentBuilder();

			Document document = db.parse(xmlFile);

			NodeList connectorNodes = document.getElementsByTagName(tagName);

			for (int i = 0; i < connectorNodes.getLength(); i++) {
				Node node = connectorNodes.item(i);

				NamedNodeMap attributes = node.getAttributes();

				Node protocolNode = attributes.getNamedItem(attriName);

				if (protocolNode != null) {
					if (protocolNode.getNodeValue().equals(attriValue)) {
						Node portNode = attributes.getNamedItem(targetName);

						return portNode.getNodeValue();
					}
				}
			}
		}
		catch (Exception e) {
		}

		return null;
	}

	protected Path autoDeployPath;
	protected Path bundlePath;
	protected Path liferayHome;
	protected Path modulesPath;

}