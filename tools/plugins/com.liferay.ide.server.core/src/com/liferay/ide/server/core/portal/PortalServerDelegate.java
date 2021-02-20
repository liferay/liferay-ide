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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.file.Files;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.ServerDelegate;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class PortalServerDelegate extends ServerDelegate implements PortalServerWorkingCopy {

	public PortalServerDelegate() {
	}

	@Override
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		IStatus retval = Status.OK_STATUS;

		if (ListUtil.isNotEmpty(add)) {
			for (IModule module : add) {
				IModuleType moduleType = module.getModuleType();

				if (!_supportTypeList.contains(moduleType.getId())) {
					retval = LiferayServerCore.error("Unable to add module with type " + moduleType.getName());

					break;
				}
			}
		}

		return retval;
	}

	public int getAutoPublishTime() {
		return getAttribute(Server.PROP_AUTO_PUBLISH_TIME, 0);
	}

	@Override
	public IModule[] getChildModules(IModule[] module) {
		IModule[] retval = null;

		if (module != null) {
			IModuleType moduleType = module[0].getModuleType();

			if ((module.length == 1) && (moduleType != null) && _supportTypeList.contains(moduleType.getId())) {
				retval = new IModule[0];
			}
		}

		return retval;
	}

	@Override
	public boolean getCustomLaunchSettings() {
		return getAttribute(PROPERTY_CUSTOM_LAUNCH_SETTINGS, PortalServerConstants.DEFAULT_CUSTOM_LAUNCH_SETTING);
	}

	@Override
	public boolean getDeveloperMode() {
		return getAttribute(PROPERTY_DEVELOPER_MODE, PortalServerConstants.DEFAULT_DEVELOPER_MODE);
	}

	public String getExternalProperties() {
		return getAttribute(PROPERTY_EXTERNAL_PROPERTIES, StringPool.EMPTY);
	}

	public String getGogoShellPort() {
		String gogoShellPort = getAttribute(PROPERTY_GOGOSHELL_PORT, StringPool.EMPTY);

		if (StringUtils.isEmpty(gogoShellPort)) {
			gogoShellPort = _getGogoShellPort(getServer());
		}

		return gogoShellPort;
	}

	public String getHost() {
		return getServer().getHost();
	}

	@Override
	public String getHttpPort() {
		return getAttribute(ATTR_HTTP_PORT, DEFAULT_HTTP_PORT);
	}

	public String getId() {
		return getServer().getId();
	}

	public String[] getMemoryArgs() {
		String[] retval = new String[0];

		String args = getAttribute(PROPERTY_MEMORY_ARGS, PortalServerConstants.DEFAULT_MEMORY_ARGS);

		if (!CoreUtil.isNullOrEmpty(args)) {
			retval = args.split(" ");
		}

		return retval;
	}

	public String getPassword() {
		return getAttribute(ATTR_PASSWORD, DEFAULT_PASSWORD);
	}

	@Override
	public URL getPluginContextURL(String context) {
		try {
			return new URL(getPortalHomeUrl(), StringPool.FORWARD_SLASH + context);
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public URL getPortalHomeUrl() {
		try {
			return new URL("http://localhost:" + getHttpPort());
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public IModule[] getRootModules(IModule module) throws CoreException {
		IStatus status = canModifyModules(new IModule[] {module}, null);

		if ((status == null) || !status.isOK()) {
			throw new CoreException(status);
		}

		return new IModule[] {module};
	}

	public String getUsername() {
		return getAttribute(ATTR_USERNAME, DEFAULT_USERNAME);
	}

	@Override
	public URL getWebServicesListURL() {
		try {
			return new URL(getPortalHomeUrl(), "/tunnel-web/axis");
		}
		catch (MalformedURLException murle) {
			LiferayServerCore.logError("Unable to get web services list URL", murle);
		}

		return null;
	}

	@Override
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public void setCustomLaunchSettings(boolean customLaunchSettings) {
		setAttribute(PROPERTY_CUSTOM_LAUNCH_SETTINGS, customLaunchSettings);
	}

	@Override
	public void setDefaults(IProgressMonitor monitor) {
		setAttribute(Server.PROP_AUTO_PUBLISH_TIME, getAutoPublishTime());
		ServerUtil.setServerDefaultName(getServerWorkingCopy());
	}

	@Override
	public void setDeveloperMode(boolean developerMode) {
		setAttribute(PROPERTY_DEVELOPER_MODE, developerMode);
	}

	public void setExternalProperties(String externalProperties) {
		setAttribute(PROPERTY_EXTERNAL_PROPERTIES, externalProperties);
	}

	@Override
	public void setGogoShellPort(String gogoShellPort) {
		setAttribute(PROPERTY_GOGOSHELL_PORT, gogoShellPort);
	}

	public void setHttpPort(String httpPort) {
		setAttribute(ATTR_HTTP_PORT, httpPort);

		IRuntime rt = getServer().getRuntime();

		PortalRuntime runtime = (PortalRuntime)rt.loadAdapter(PortalRuntime.class, new NullProgressMonitor());

		PortalBundle portalRuntime = runtime.getPortalBundle();

		portalRuntime.setHttpPort(httpPort);
	}

	public void setMemoryArgs(String memoryArgs) {
		setAttribute(PROPERTY_MEMORY_ARGS, memoryArgs);
	}

	public void setPassword(String password) {
		setAttribute(ATTR_PASSWORD, password);
	}

	public void setUsername(String username) {
		setAttribute(ATTR_USERNAME, username);
	}

	private String _getGogoShellPort(IServer server) {
		IRuntime runtime = server.getRuntime();

		String gogoShellPortValue = PortalServerConstants.DEFAULT_GOGOSHELL_PORT;

		PortalRuntime liferayRuntime = (PortalRuntime)runtime.loadAdapter(PortalRuntime.class, null);

		if (Objects.nonNull(liferayRuntime)) {
			File[] extPropertiesFiles = _getPortalExtraPropertiesFiles(liferayRuntime, "portal-ext.properties");
			File[] developerPropertiesFiles = _getPortalExtraPropertiesFiles(
				liferayRuntime, "portal-developer.properties");
			File[] setupWizardPropertiesFiles = _getPortalExtraPropertiesFiles(
				liferayRuntime, "portal-setup-wizard.properties");

			Properties poralExtraProperties = new Properties();

			_loadProperties(poralExtraProperties, extPropertiesFiles);
			_loadProperties(poralExtraProperties, developerPropertiesFiles);
			_loadProperties(poralExtraProperties, setupWizardPropertiesFiles);

			String gogoShellConnectString = poralExtraProperties.getProperty(
				"module.framework.properties.osgi.console");

			if (Objects.nonNull(gogoShellConnectString)) {
				String[] gogoShellConnectStrings = gogoShellConnectString.split(":");

				if (Objects.nonNull(gogoShellConnectStrings) && (gogoShellConnectStrings.length > 1)) {
					gogoShellPortValue = gogoShellConnectStrings[1];
				}
			}
		}

		return gogoShellPortValue;
	}

	private File[] _getPortalExtraPropertiesFiles(ILiferayRuntime liferayRuntime, String propertyFileName) {
		File[] retVal = new File[0];

		IPath liferayHome = liferayRuntime.getLiferayHome();

		if (liferayHome != null) {
			File liferayHomeDir = liferayHome.toFile();

			File[] files = liferayHomeDir.listFiles(
				(dir, name) -> dir.equals(liferayHomeDir) && Objects.equals(name, propertyFileName));

			if (files != null) {
				retVal = files;
			}
		}

		return retVal;
	}

	private void _loadProperties(Properties poralExtraPropertiesFiles, File[] propertyFiles) {
		if (ListUtil.isNotEmpty(propertyFiles)) {
			try (InputStream stream = Files.newInputStream(propertyFiles[0].toPath())) {
				poralExtraPropertiesFiles.load(stream);
			}
			catch (IOException ioe) {
			}
		}
	}

	private static final List<String> _supportTypeList = Arrays.asList("liferay.bundle", "jst.web", "jst.utility");

}