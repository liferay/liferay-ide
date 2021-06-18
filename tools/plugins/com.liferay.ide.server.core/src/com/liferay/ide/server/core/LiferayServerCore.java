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

package com.liferay.ide.server.core;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.sdk.core.ISDKListener;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.server.core.portal.AbstractPortalBundleFactory;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalBundleFactory;
import com.liferay.ide.server.core.portal.docker.IDockerServer;
import com.liferay.ide.server.remote.IRemoteServer;
import com.liferay.ide.server.remote.IServerManagerConnection;
import com.liferay.ide.server.remote.ServerManagerConnection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.Method;

import java.net.URL;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.Base;
import org.eclipse.wst.server.core.internal.IMemento;
import org.eclipse.wst.server.core.internal.XMLMemento;
import org.eclipse.wst.server.core.model.RuntimeDelegate;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class LiferayServerCore extends Plugin {

	public static final String BUNDLE_OUTPUT_ERROR_MARKER_TYPE = "com.liferay.ide.server.core.BundleOutputErrorMarker";

	public static final String BUNDLE_OUTPUT_WARNING_MARKER_TYPE =
		"com.liferay.ide.server.core.BundleOutputWarningMarker";

	public static final String PLUGIN_ID = "com.liferay.ide.server.core";

	public static IStatus createErrorStatus(Exception e) {
		return error(e.getMessage(), e);
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg) {
		return new Status(IStatus.ERROR, pluginId, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg, Throwable e) {
		return new Status(IStatus.ERROR, pluginId, msg, e);
	}

	public static IStatus createErrorStatus(String msg, Throwable t) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, t);
	}

	public static IStatus createWarningStatus(String message) {
		return new Status(IStatus.WARNING, PLUGIN_ID, message);
	}

	public static IStatus createWarningStatus(String message, String id) {
		return new Status(IStatus.WARNING, id, message);
	}

	public static IStatus createWarningStatus(String message, String id, Exception e) {
		return new Status(IStatus.WARNING, id, message, e);
	}

	public static IStatus error(String msg) {
		return createErrorStatus(PLUGIN_ID, msg);
	}

	public static IStatus error(String msg, Throwable t) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, t);
	}

	public static LiferayServerCore getDefault() {
		return _plugin;
	}

	public static IDockerServer getDockerServer() {
		IDockerServer retval = null;

		IDockerServer[] dockerServers = getDockerServers();

		if (ListUtil.isNotEmpty(dockerServers)) {
			for (IDockerServer dockerServer : dockerServers) {
				if (dockerServer != null) {
					retval = dockerServer;

					break;
				}
			}
		}

		return retval;
	}

	public static IDockerServer[] getDockerServers() {
		if (_dockerServers == null) {
			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

			IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(IDockerServer.ID);

			try {
				List<IDockerServer> deployers = new ArrayList<>();

				for (IConfigurationElement element : elements) {
					Object o = element.createExecutableExtension("class");

					if (o instanceof IDockerServer) {
						IDockerServer dockerServer = (IDockerServer)o;

						deployers.add(dockerServer);
					}
				}

				_dockerServers = deployers.toArray(new IDockerServer[0]);
			}
			catch (Exception e) {
				logError("Unable to get docker deployer extensions", e);
			}
		}

		return _dockerServers;
	}

	public static URL getPluginEntry(String path) {
		LiferayServerCore serverCore = getDefault();

		Bundle pluginBundle = serverCore.getBundle();

		return pluginBundle.getEntry(path);
	}

	public static IPluginPublisher getPluginPublisher(String facetId, String runtimeTypeId) {
		if (CoreUtil.isNullOrEmpty(facetId) || CoreUtil.isNullOrEmpty(runtimeTypeId)) {
			return null;
		}

		IPluginPublisher retval = null;

		IPluginPublisher[] publishers = getPluginPublishers();

		if (ListUtil.isNotEmpty(publishers)) {
			for (IPluginPublisher publisher : publishers) {
				if ((publisher != null) && facetId.equals(publisher.getFacetId()) &&
					runtimeTypeId.equals(publisher.getRuntimeTypeId())) {

					retval = publisher;

					break;
				}
			}
		}

		return retval;
	}

	public static IPluginPublisher[] getPluginPublishers() {
		if (_pluginPublishers == null) {
			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

			IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(IPluginPublisher.ID);

			try {
				List<IPluginPublisher> deployers = new ArrayList<>();

				for (IConfigurationElement element : elements) {
					Object o = element.createExecutableExtension("class");

					if (o instanceof AbstractPluginPublisher) {
						AbstractPluginPublisher pluginDeployer = (AbstractPluginPublisher)o;

						pluginDeployer.setFacetId(element.getAttribute("facetId"));
						pluginDeployer.setRuntimeTypeId(element.getAttribute("runtimeTypeId"));

						deployers.add(pluginDeployer);
					}
				}

				_pluginPublishers = deployers.toArray(new IPluginPublisher[0]);
			}
			catch (Exception e) {
				logError("Unable to get plugin deployer extensions", e);
			}
		}

		return _pluginPublishers;
	}

	public static PortalBundleFactory[] getPortalBundleFactories() {
		if (_portalBundleFactories == null) {
			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

			IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(
				PortalBundleFactory.EXTENSION_ID);

			try {
				List<PortalBundleFactory> bundleFactories = new ArrayList<>();

				for (IConfigurationElement element : elements) {
					Object o = element.createExecutableExtension("class");

					if (o instanceof PortalBundleFactory) {
						AbstractPortalBundleFactory portalBundleFactory = (AbstractPortalBundleFactory)o;

						portalBundleFactory.setBundleFactoryType(element.getAttribute("type"));

						bundleFactories.add(portalBundleFactory);
					}
				}

				_portalBundleFactories = bundleFactories.toArray(new PortalBundleFactory[0]);
			}
			catch (Exception e) {
				logError("Unable to get PortalBundleFactory extensions", e);
			}
		}

		return _portalBundleFactories;
	}

	public static PortalBundleFactory getPortalBundleFactories(String type) {
		PortalBundleFactory[] factories = getPortalBundleFactories();

		if (factories != null) {
			for (PortalBundleFactory portalBundleFactory : factories) {
				String portalBundleFactoryType = portalBundleFactory.getType();

				if (portalBundleFactoryType.equals(type)) {
					return portalBundleFactory;
				}
			}
		}

		return null;
	}

	public static PortalLaunchParticipant[] getPortalLaunchParticipants() {
		PortalLaunchParticipant[] retval = null;

		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

		IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(
			"com.liferay.ide.server.core.portalLaunchParticipants");

		try {
			List<PortalLaunchParticipant> participants = new ArrayList<>();

			for (IConfigurationElement element : elements) {
				Object o = element.createExecutableExtension("class");

				if (o instanceof PortalLaunchParticipant) {
					PortalLaunchParticipant participant = (PortalLaunchParticipant)o;

					participants.add(participant);
				}
			}

			retval = participants.toArray(new PortalLaunchParticipant[0]);
		}
		catch (Exception e) {
			logError("Unable to get portal launch participants", e);
		}

		return retval;
	}

	public static URL getPortalSupportLibURL() {
		try {
			return FileLocator.toFileURL(getPluginEntry("/portal-support/portal-support.jar"));
		}
		catch (IOException ioe) {
		}

		return null;
	}

	public static String getPreference(String key) {
		IEclipsePreferences pref = InstanceScope.INSTANCE.getNode(PLUGIN_ID);

		return pref.get(key, "");
	}

	public static IServerManagerConnection getRemoteConnection(IRemoteServer server) {
		IServerManagerConnection retval = null;

		if (_connections == null) {
			_connections = new HashMap<>();
		}

		if (server != null) {
			IServerManagerConnection service = _connections.get(server.getId());

			if (service == null) {
				service = new ServerManagerConnection();

				updateConnectionSettings(server, service);

				_connections.put(server.getId(), service);
			}
			else {
				updateConnectionSettings(server, service);
			}

			retval = service;
		}

		return retval;
	}

	public static IRuntimeDelegateValidator[] getRuntimeDelegateValidators() {
		if (_runtimeDelegateValidators == null) {
			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

			IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(
				IRuntimeDelegateValidator.ID);

			try {
				List<IRuntimeDelegateValidator> validators = new ArrayList<>();

				for (IConfigurationElement element : elements) {
					Object o = element.createExecutableExtension("class");
					String runtimeTypeId = element.getAttribute("runtimeTypeId");

					if (o instanceof AbstractRuntimeDelegateValidator) {
						AbstractRuntimeDelegateValidator validator = (AbstractRuntimeDelegateValidator)o;

						validator.setRuntimeTypeId(runtimeTypeId);

						validators.add(validator);
					}
				}

				_runtimeDelegateValidators = validators.toArray(new IRuntimeDelegateValidator[0]);
			}
			catch (Exception e) {
				logError("Unable to get IRuntimeDelegateValidator extensions", e);
			}
		}

		return _runtimeDelegateValidators;
	}

	public static ILiferayRuntimeStub getRuntimeStub(String stubTypeId) {
		ILiferayRuntimeStub retval = null;

		ILiferayRuntimeStub[] stubs = getRuntimeStubs();

		if (ListUtil.isNotEmpty(stubs)) {
			for (ILiferayRuntimeStub stub : stubs) {
				String runtimeStubTypeId = stub.getRuntimeStubTypeId();

				if (runtimeStubTypeId.equals(stubTypeId)) {
					retval = stub;

					break;
				}
			}
		}

		return retval;
	}

	public static ILiferayRuntimeStub[] getRuntimeStubs() {
		if (_runtimeStubs == null) {
			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

			IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(
				ILiferayRuntimeStub.EXTENSION_ID);

			if (ListUtil.isNotEmpty(elements)) {
				List<ILiferayRuntimeStub> stubs = new ArrayList<>();

				for (IConfigurationElement element : elements) {
					String runtimeTypeId = element.getAttribute(ILiferayRuntimeStub.RUNTIME_TYPE_ID);
					String name = element.getAttribute(ILiferayRuntimeStub.NAME);
					boolean isDefault = Boolean.parseBoolean(element.getAttribute(ILiferayRuntimeStub.DEFAULT));

					try {
						LiferayRuntimeStub stub = new LiferayRuntimeStub();

						stub.setRuntimeTypeId(runtimeTypeId);
						stub.setName(name);
						stub.setDefault(isDefault);

						stubs.add(stub);
					}
					catch (Exception e) {
						logError("Could not create liferay runtime stub.", e);
					}
				}

				_runtimeStubs = stubs.toArray(new ILiferayRuntimeStub[0]);
			}
		}

		return _runtimeStubs;
	}

	public static IPath getTempLocation(String prefix, String fileName) {
		LiferayServerCore serverCore = getDefault();

		IPath stateLocation = serverCore.getStateLocation();

		IPath tempLocation = stateLocation.append("tmp");

		return tempLocation.append(
			prefix + "/" + System.currentTimeMillis() +
				(CoreUtil.isNullOrEmpty(fileName) ? StringPool.EMPTY : "/" + fileName));
	}

	public static IStatus info(String msg) {
		return new Status(IStatus.INFO, PLUGIN_ID, msg);
	}

	public static boolean isPortalBundlePath(IPath bundlePath) {
		if (FileUtil.notExists(bundlePath)) {
			return false;
		}

		PortalBundleFactory[] factories = getPortalBundleFactories();

		if (factories != null) {
			for (PortalBundleFactory portalBundleFactory : factories) {
				IPath path = portalBundleFactory.canCreateFromPath(bundlePath);

				if (path != null) {
					return true;
				}
			}
		}

		return false;
	}

	public static void logError(Exception e) {
		LiferayServerCore serverCore = getDefault();

		ILog log = serverCore.getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

	public static void logError(IStatus status) {
		LiferayServerCore serverCore = getDefault();

		ILog log = serverCore.getLog();

		log.log(status);
	}

	public static void logError(String msg) {
		logError(error(msg));
	}

	public static void logError(String msg, Throwable e) {
		LiferayServerCore serverCore = getDefault();

		ILog log = serverCore.getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	public static void logError(Throwable t) {
		LiferayServerCore serverCore = getDefault();

		ILog log = serverCore.getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t));
	}

	public static void logInfo(IStatus status) {
		LiferayServerCore serverCore = getDefault();

		ILog log = serverCore.getLog();

		log.log(status);
	}

	public static void logInfo(String msg) {
		logInfo(info(msg));
	}

	public static PortalBundle newPortalBundle(IPath bundlePath) {
		PortalBundleFactory[] factories = getPortalBundleFactories();

		if (factories != null) {
			for (PortalBundleFactory portalBundleFactory : factories) {
				IPath path = portalBundleFactory.canCreateFromPath(bundlePath);

				if (path != null) {
					return portalBundleFactory.create(path);
				}
			}
		}

		return null;
	}

	public static void setPreference(String key, String value) {
		try {
			IEclipsePreferences pref = InstanceScope.INSTANCE.getNode(PLUGIN_ID);

			pref.put(key, value);
			pref.flush();
		}
		catch (BackingStoreException bse) {
			logError("Unable to save preference", bse);
		}
	}

	public static void updateConnectionSettings(IRemoteServer server) {
		updateConnectionSettings(server, getRemoteConnection(server));
	}

	public static void updateConnectionSettings(IRemoteServer server, IServerManagerConnection remoteConnection) {
		remoteConnection.setHost(server.getHost());
		remoteConnection.setHttpPort(server.getHTTPPort());
		remoteConnection.setManagerContextPath(server.getServerManagerContextPath());
		remoteConnection.setUsername(server.getUsername());
		remoteConnection.setPassword(server.getPassword());
	}

	public static IStatus validateRuntimeDelegate(RuntimeDelegate runtimeDelegate) {
		IRuntime runtime = runtimeDelegate.getRuntime();

		if (runtime.isStub()) {
			return Status.OK_STATUS;
		}

		IRuntimeType runtimeType = runtime.getRuntimeType();

		String runtimeTypeId = runtimeType.getId();

		IRuntimeDelegateValidator[] validators = getRuntimeDelegateValidators();

		if (ListUtil.isNotEmpty(validators)) {
			for (IRuntimeDelegateValidator validator : validators) {
				if (runtimeTypeId.equals(validator.getRuntimeTypeId())) {
					IStatus status = validator.validateRuntimeDelegate(runtimeDelegate);

					if (!status.isOK()) {
						return status;
					}
				}
			}
		}

		return Status.OK_STATUS;
	}

	public LiferayServerCore() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;

		_runtimeLifecycleListener = new IRuntimeLifecycleListener() {

			@Override
			public void runtimeAdded(IRuntime runtime) {
				_saveGlobalRuntimeSettings(runtime);
			}

			@Override
			public void runtimeChanged(IRuntime runtime) {
				_saveGlobalRuntimeSettings(runtime);
			}

			@Override
			public void runtimeRemoved(IRuntime runtime) {
				_saveGlobalRuntimeSettings(runtime);
			}

		};

		_serverLifecycleListener = new IServerLifecycleListener() {

			@Override
			public void serverAdded(IServer server) {
				_saveGlobalServerSettings(server);
			}

			@Override
			public void serverChanged(IServer server) {
				_saveGlobalServerSettings(server);
			}

			@Override
			public void serverRemoved(IServer server) {
				_saveGlobalServerSettings(server);

				if (_connections.get(server.getId()) != null) {
					_connections.put(server.getId(), null);
				}
			}

		};

		ServerCore.addRuntimeLifecycleListener(_runtimeLifecycleListener);
		ServerCore.addServerLifecycleListener(_serverLifecycleListener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);

		SDKManager sdkManagerInstance = SDKManager.getInstance();

		sdkManagerInstance.removeSDKListener(_sdkListener);

		ServerCore.removeRuntimeLifecycleListener(_runtimeLifecycleListener);
		ServerCore.removeServerLifecycleListener(_serverLifecycleListener);

		IJobManager jobManager = Job.getJobManager();

		Job[] jobs = jobManager.find(null);

		for (Job job : jobs) {
			if (job.getProperty(ILiferayServer.LIFERAY_SERVER_JOB) != null) {
				job.cancel();
			}
		}
	}

	private boolean _addRuntimeToMemento(IRuntime runtime, IMemento memento) {
		if (runtime instanceof Base) {
			Base base = (Base)runtime;

			try {
				Method save = Base.class.getDeclaredMethod("save", IMemento.class);

				if (save != null) {
					save.setAccessible(true);
					save.invoke(base, memento);
				}

				return true;
			}
			catch (Exception e) {
				logError("Unable to add runtime to memento", e);
			}
		}

		return false;
	}

	private boolean _addServerToMemento(IServer server, IMemento memento) {
		if (server instanceof Base) {
			Base base = (Base)server;

			try {
				Method save = Base.class.getDeclaredMethod("save", IMemento.class);

				if (save != null) {
					save.setAccessible(true);
					save.invoke(base, memento);
				}

				return true;
			}
			catch (Exception e) {
				logError("Unable to add server to memento", e);
			}
		}

		return false;
	}

	private void _copyMemento(IMemento from, IMemento to) {
		for (String name : from.getNames()) {
			to.putString(name, from.getString(name));
		}
	}

	private synchronized void _saveGlobalRuntimeSettings(IRuntime runtime) {
		IRuntimeType runtimeType = runtime.getRuntimeType();

		String runtimeTypeId = runtimeType.getId();

		if ((runtimeType != null) && runtimeTypeId.startsWith("com.liferay")) {
			try {
				IPath globalSettingPath = LiferayCore.GLOBAL_SETTINGS_PATH;

				File globalSettingFile = globalSettingPath.toFile();

				globalSettingFile.mkdirs();

				IPath runtimesGlobalPath = globalSettingPath.append("runtimes.xml");

				File runtimesGlobalFile = runtimesGlobalPath.toFile();

				Set<IMemento> existing = new HashSet<>();

				if (FileUtil.exists(runtimesGlobalFile)) {
					try {
						try (InputStream newInputStream = Files.newInputStream(runtimesGlobalFile.toPath())) {
							IMemento existingMemento = XMLMemento.loadMemento(newInputStream);

							if (existingMemento != null) {
								IMemento[] children = existingMemento.getChildren("runtime");

								if (ListUtil.isNotEmpty(children)) {
									for (IMemento child : children) {
										IPath loc = Path.fromPortableString(child.getString("location"));

										if (FileUtil.exists(loc)) {
											boolean duplicate = false;

											if (ServerCore.findRuntime(child.getString("id")) != null) {
												duplicate = true;
											}

											if (!duplicate) {
												existing.add(child);
											}
										}
									}
								}
							}
						}
					}
					catch (Exception e) {
					}
				}

				Map<String, IMemento> mementos = new HashMap<>();

				XMLMemento runtimeMementos = XMLMemento.createWriteRoot("runtimes");

				for (IMemento exist : existing) {
					IMemento copy = runtimeMementos.createChild("runtime");

					_copyMemento(exist, copy);

					mementos.put(copy.getString("id"), copy);
				}

				for (IRuntime r : ServerCore.getRuntimes()) {
					if ((mementos.get(r.getId()) == null) && (r.getRuntimeType() != null)) {
						IMemento rMemento = runtimeMementos.createChild("runtime");

						if (_addRuntimeToMemento(r, rMemento)) {
							mementos.put(r.getId(), rMemento);
						}
					}
				}

				try (OutputStream fos = Files.newOutputStream(runtimesGlobalFile.toPath())) {
					runtimeMementos.save(fos);
				}
			}
			catch (Exception e) {
				logError("Unable to save global runtime settings", e);
			}
		}
	}

	private synchronized void _saveGlobalServerSettings(IServer server) {
		IServerType serverType = server.getServerType();

		if (serverType != null) {
			String serverTypeId = serverType.getId();

			if (serverTypeId.startsWith("com.liferay")) {
				try {
					IPath globalSettingPath = LiferayCore.GLOBAL_SETTINGS_PATH;

					File globalSettingFile = globalSettingPath.toFile();

					globalSettingFile.mkdirs();

					IPath globalServersPath = globalSettingPath.append("servers.xml");

					File globalServersFile = globalServersPath.toFile();

					Set<IMemento> existing = new HashSet<>();

					if (FileUtil.exists(globalServersFile)) {
						try {
							try (InputStream newInputStream = Files.newInputStream(globalServersFile.toPath())) {
								IMemento existingMemento = XMLMemento.loadMemento(newInputStream);

								if (existingMemento != null) {
									IMemento[] children = existingMemento.getChildren("server");

									if (ListUtil.isNotEmpty(children)) {
										for (IMemento child : children) {
											boolean duplicate = false;

											if (ServerCore.findServer(child.getString("id")) != null) {
												duplicate = true;
											}

											if (!duplicate) {
												existing.add(child);
											}
										}
									}
								}
							}
						}
						catch (Exception e) {
						}
					}

					Map<String, IMemento> mementos = new HashMap<>();

					XMLMemento serverMementos = XMLMemento.createWriteRoot("servers");

					for (IMemento exist : existing) {
						IMemento copy = serverMementos.createChild("server");

						_copyMemento(exist, copy);
						mementos.put(copy.getString("id"), copy);
					}

					for (IServer s : ServerCore.getServers()) {
						if ((mementos.get(s.getId()) == null) && (s.getServerType() != null)) {
							IMemento sMemento = serverMementos.createChild("server");

							if (_addServerToMemento(s, sMemento)) {
								mementos.put(s.getId(), sMemento);
							}
						}
					}

					if (!mementos.isEmpty()) {
						try (OutputStream fos = Files.newOutputStream(globalServersFile.toPath())) {
							serverMementos.save(fos);
						}
					}
				}
				catch (Exception e) {
					logError("Unable to save global server settings", e);
				}
			}
		}
	}

	private static Map<String, IServerManagerConnection> _connections = null;
	private static IDockerServer[] _dockerServers = null;
	private static LiferayServerCore _plugin;
	private static IPluginPublisher[] _pluginPublishers = null;
	private static PortalBundleFactory[] _portalBundleFactories;
	private static IRuntimeDelegateValidator[] _runtimeDelegateValidators;
	private static ILiferayRuntimeStub[] _runtimeStubs;

	private IRuntimeLifecycleListener _runtimeLifecycleListener;
	private ISDKListener _sdkListener;
	private IServerLifecycleListener _serverLifecycleListener;

}