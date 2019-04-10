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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.JavaUtil;
import com.liferay.ide.server.util.LiferayPortalValueLoader;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.osgi.framework.Version;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Simon Jiang
 * @author Charles Wu
 */
public abstract class AbstractPortalBundle implements PortalBundle {

	public AbstractPortalBundle(IPath path) {
		if (path == null) {
			throw new IllegalArgumentException("path cannot be null");
		}

		bundlePath = path;

		liferayHome = bundlePath.append("..");

		autoDeployPath = liferayHome.append("deploy");

		modulesPath = liferayHome.append("osgi");
	}

	public AbstractPortalBundle(Map<String, String> appServerProperties) {
		if (appServerProperties == null) {
			throw new IllegalArgumentException("bundle parameters cannot be null");
		}

		String appServerPath = appServerProperties.get("app.server.dir");
		String appServerDeployPath = appServerProperties.get("app.server.deploy.dir");
		String appServerParentPath = appServerProperties.get("app.server.parent.dir");

		bundlePath = new Path(appServerPath);

		liferayHome = new Path(appServerParentPath);

		autoDeployPath = new Path(appServerDeployPath);

		modulesPath = null;
	}

	@Override
	public IPath getAppServerDir() {
		return bundlePath;
	}

	@Override
	public IPath getAutoDeployPath() {
		return autoDeployPath;
	}

	@Override
	public IPath[] getBundleDependencyJars() {
		List<IPath> libs = new ArrayList<>();

		File bundleLibPath = new File(getAppServerLibDir().toOSString());

		if (FileUtil.exists(bundleLibPath)) {
			libs = FileListing.getFileListing(bundleLibPath, "jar");
		}

		return libs.toArray(new IPath[libs.size()]);
	}

	@Override
	public String[] getHookSupportedProperties() {
		IPath portalDir = getAppServerPortalDir();
		IPath[] extraLibs = getBundleDependencyJars();

		return new LiferayPortalValueLoader(
			portalDir, extraLibs
		).loadHookPropertiesFromClass();
	}

	@Override
	public int getJmxRemotePort() {
		return getDefaultJMXRemotePort();
	}

	@Override
	public IPath getLiferayHome() {
		return liferayHome;
	}

	@Override
	public IPath getModulesPath() {
		return modulesPath;
	}

	@Override
	public IPath getOSGiBundlesDir() {
		IPath retval = null;

		if (liferayHome != null) {
			retval = liferayHome.append("osgi");
		}

		return retval;
	}

	@Override
	public Properties getPortletCategories() {
		return ServerUtil.getPortletCategories(getAppServerPortalDir());
	}

	@Override
	public Properties getPortletEntryCategories() {
		return ServerUtil.getEntryCategories(getAppServerPortalDir(), getVersion());
	}

	@Override
	public String getServerReleaseInfo() {
		return _getConfigInfoFromManifest(_CONFIG_TYPE_SERVER, getAppServerPortalDir());
	}

	@Override
	public String getVersion() {
		return _getPortalVersion(getAppServerPortalDir(), getBundleDependencyJars());
	}

	protected abstract IPath getAppServerLibDir();

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
					String nodeValue = protocolNode.getNodeValue();

					if (nodeValue.equals(attriValue)) {
						Node portNode = attributes.getNamedItem(targetName);

						return portNode.getNodeValue();
					}
				}
			}
		}
		catch (Exception e) {
			LiferayServerCore.logError(e);
		}

		return null;
	}

	protected IPath autoDeployPath;
	protected IPath bundlePath;
	protected IPath liferayHome;
	protected IPath modulesPath;

	private String _getConfigInfoFromCache(String configType, IPath portalDir) {
		IPath configInfoPath = _getConfigInfoPath(configType);

		File configInfoFile = configInfoPath.toFile();

		String portalDirKey = CoreUtil.createStringDigest(portalDir.toPortableString());

		Properties properties = new Properties();

		if (FileUtil.exists(configInfoFile)) {
			try (InputStream fileInput = Files.newInputStream(configInfoFile.toPath())) {
				properties.load(fileInput);

				String configInfo = (String)properties.get(portalDirKey);

				if (!CoreUtil.isNullOrEmpty(configInfo)) {
					return configInfo;
				}
			}
			catch (IOException ioe) {
				LiferayServerCore.logError(ioe);
			}
		}

		return null;
	}

	private String _getConfigInfoFromManifest(String configType, IPath portalDir) {
		IPath implJarPath = portalDir.append("/WEB-INF/lib/portal-impl.jar");

		File implJar = implJarPath.toFile();

		String version = null;
		String serverInfo = null;

		if (FileUtil.exists(implJar)) {
			version = JavaUtil.getJarProperty(implJar, "Liferay-Portal-Version");
			serverInfo = JavaUtil.getJarProperty(implJar, "Liferay-Portal-Release-Info");

			if (CoreUtil.compareVersions(Version.parseVersion(version), _MANIFEST_VERSION_REQUIRED) < 0) {
				version = null;
				serverInfo = null;
			}
		}

		if (configType.equals(_CONFIG_TYPE_VERSION)) {
			return version;
		}

		if (configType.equals(_CONFIG_TYPE_SERVER)) {
			serverInfo = serverInfo.substring(0, serverInfo.indexOf("("));

			return serverInfo.trim();
		}

		return null;
	}

	private IPath _getConfigInfoPath(String configType) {
		IPath configInfoPath = null;

		LiferayServerCore serverCore = LiferayServerCore.getDefault();

		IPath stateLocation = serverCore.getStateLocation();

		if (configType.equals(_CONFIG_TYPE_VERSION)) {
			configInfoPath = stateLocation.append("version.properties");
		}
		else if (configType.equals(_CONFIG_TYPE_SERVER)) {
			configInfoPath = stateLocation.append("serverInfos.properties");
		}

		if (!_clearedConfigInfoCacheOnce) {
			File configInfoFile = configInfoPath.toFile();

			configInfoFile.delete();

			_clearedConfigInfoCacheOnce = true;
		}

		return configInfoPath;
	}

	private String _getPortalVersion(IPath portalDir, IPath[] extraLib) {
		if (FileUtil.notExists(portalDir)) {
			return Version.emptyVersion.toString();
		}

		String version = _getConfigInfoFromCache(_CONFIG_TYPE_VERSION, portalDir);

		if (version == null) {
			version = _getConfigInfoFromManifest(_CONFIG_TYPE_VERSION, portalDir);

			if (version == null) {
				LiferayPortalValueLoader loader = new LiferayPortalValueLoader(portalDir, extraLib);

				Version loadedVersion = loader.loadVersionFromClass();

				if (loadedVersion != null) {
					version = loadedVersion.toString();
				}
			}

			if (version != null) {
				_saveConfigInfoIntoCache(_CONFIG_TYPE_VERSION, version, portalDir);
			}
		}

		return version;
	}

	private void _saveConfigInfoIntoCache(String configType, String configInfo, IPath portalDir) {
		IPath versionsInfoPath = null;

		LiferayServerCore serverCore = LiferayServerCore.getDefault();

		IPath stateLocation = serverCore.getStateLocation();

		if (configType.equals(_CONFIG_TYPE_VERSION)) {
			versionsInfoPath = stateLocation.append("version.properties");
		}
		else if (configType.equals(_CONFIG_TYPE_SERVER)) {
			versionsInfoPath = stateLocation.append("serverInfos.properties");
		}

		if (versionsInfoPath != null) {
			File versionInfoFile = versionsInfoPath.toFile();

			if (configInfo != null) {
				String portalDirKey = CoreUtil.createStringDigest(portalDir.toPortableString());

				Properties properties = new Properties();

				try (InputStream fileInput = Files.newInputStream(versionInfoFile.toPath())) {
					properties.load(fileInput);
				}
				catch (Exception e) {
				}

				try (OutputStream fileOutput = Files.newOutputStream(versionInfoFile.toPath())) {
					properties.put(portalDirKey, configInfo);
					properties.store(fileOutput, StringPool.EMPTY);
				}
				catch (Exception e) {
					LiferayServerCore.logError(e);
				}
			}
		}
	}

	private static final String _CONFIG_TYPE_SERVER = "server";

	private static final String _CONFIG_TYPE_VERSION = "version";

	private static final Version _MANIFEST_VERSION_REQUIRED = ILiferayConstants.V700;

	private static boolean _clearedConfigInfoCacheOnce = false;

}