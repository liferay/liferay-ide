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

package com.liferay.ide.core;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.workspace.ProjectChangeListener;

import java.util.List;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Gregory Amerson
 */
public class LiferayCore extends Plugin {

	public static final IPath GLOBAL_SETTINGS_PATH = new Path(System.getProperty("user.home", "") + "/.liferay-ide");

	public static final IPath GLOBAL_USER_DIR = GLOBAL_SETTINGS_PATH.append("bundles");

	public static final String LIFERAY_JOB_FAMILY = "com.liferay.ide.jobs";

	public static final String PLUGIN_ID = "com.liferay.ide.core";

	public static <T> T create(Class<T> type, Object adaptable) {
		if (type == null) {
			return null;
		}

		T retval = null;

		ILiferayProject lrproject = create(adaptable);

		if ((lrproject != null) && type.isAssignableFrom(lrproject.getClass())) {
			retval = type.cast(lrproject);
		}

		if ((retval == null) && (lrproject != null)) {
			retval = lrproject.adapt(type);
		}

		return retval;
	}

	// The shared instance

	public static ILiferayProject create(Object adaptable) {
		if (adaptable == null) {
			return null;
		}

		ILiferayProjectProvider[] providers = getProviders(adaptable.getClass());

		if (ListUtil.isEmpty(providers)) {
			return null;
		}

		ILiferayProjectProvider currentProvider = null;
		ILiferayProject project = null;

		for (ILiferayProjectProvider provider : providers) {
			if ((currentProvider == null) || (provider.getPriority() > currentProvider.getPriority())) {
				ILiferayProject lrp = provider.provide(adaptable);

				if (lrp != null) {
					currentProvider = provider;

					project = lrp;
				}
			}
		}

		return project;
	}

	// The plugin ID

	public static IStatus createErrorStatus(Exception e) {
		return createErrorStatus(PLUGIN_ID, e);
	}

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg) {
		return new Status(IStatus.ERROR, pluginId, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg, Throwable e) {
		return new Status(IStatus.ERROR, pluginId, msg, e);
	}

	public static IStatus createErrorStatus(String pluginId, Throwable t) {
		return new Status(IStatus.ERROR, pluginId, t.getMessage(), t);
	}

	public static IStatus createInfoStatus(String msg) {
		return createInfoStatus(PLUGIN_ID, msg);
	}

	public static IStatus createInfoStatus(String pluginId, String msg) {
		return new Status(IStatus.INFO, pluginId, msg);
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

	public static LiferayCore getDefault() {
		return _plugin;
	}

	public static IPath getDefaultStateLocation() {
		return getDefault().getStateLocation();
	}

	public static synchronized ILiferayProjectImporter getImporter(String buildType) {
		if (_importerReader == null) {
			_importerReader = new LiferayProjectImporterReader();
		}

		return _importerReader.getImporter(buildType);
	}

	public static synchronized ILiferayProjectImporter[] getImporters() {
		if (_importerReader == null) {
			_importerReader = new LiferayProjectImporterReader();
		}

		return _importerReader.getImporters();
	}

	public static synchronized ILiferayProjectAdapter[] getProjectAdapters() {
		List<ILiferayProjectAdapter> extensions = _adapterReader.getExtensions();

		return extensions.toArray(new ILiferayProjectAdapter[0]);
	}

	public static synchronized ILiferayProjectProvider getProvider(String shortName) {
		for (ILiferayProjectProvider provider : getProviders()) {
			String name = provider.getShortName();

			if (name.equals(shortName)) {
				return provider;
			}
		}

		return null;
	}

	public static synchronized ILiferayProjectProvider[] getProviders() {
		if (_providerReader == null) {
			_providerReader = new LiferayProjectProviderReader();
		}

		return _providerReader.getProviders();
	}

	public static synchronized ILiferayProjectProvider[] getProviders(Class<?> type) {
		if (_providerReader == null) {
			_providerReader = new LiferayProjectProviderReader();
		}

		return _providerReader.getProviders(type);
	}

	public static synchronized ILiferayProjectProvider[] getProviders(String projectType) {
		if (_providerReader == null) {
			_providerReader = new LiferayProjectProviderReader();
		}

		return _providerReader.getProviders(projectType);
	}

	public static IProxyService getProxyService() {
		Bundle bundle = getDefault().getBundle();

		ServiceTracker<Object, Object> proxyTracker = new ServiceTracker<>(
			bundle.getBundleContext(), IProxyService.class.getName(), null);

		proxyTracker.open();

		IProxyService proxyService = (IProxyService)proxyTracker.getService();

		proxyTracker.close();

		return proxyService;
	}

	public static ListenerRegistry listenerRegistry() {
		return getDefault()._listenerRegistryServiceTracker.getService();
	}

	public static void logError(IStatus status) {
		ILog log = getDefault().getLog();

		log.log(status);
	}

	public static void logError(String msg) {
		logError(createErrorStatus(msg));
	}

	public static void logError(String msg, Throwable t) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(PLUGIN_ID, msg, t));
	}

	public static void logError(Throwable t) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t));
	}

	public static void logInfo(String msg) {
		logError(createInfoStatus(msg));
	}

	public static void logWarning(Throwable t) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.WARNING, PLUGIN_ID, t.getMessage(), t));
	}

	public LiferayCore() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;

		_listenerRegistryServiceTracker = _createServiceTracker(context, ListenerRegistry.class);

		_projectChangeListener = ProjectChangeListener.createAndRegister();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		_listenerRegistryServiceTracker.close();

		_projectChangeListener.close();

		super.stop(context);
	}

	private <T> ServiceTracker<T, T> _createServiceTracker(BundleContext context, Class<T> clazz) {
		ServiceTracker<T, T> serviceTracker = new ServiceTracker<>(context, clazz.getName(), null);

		serviceTracker.open();

		return serviceTracker;
	}

	private static final LiferayProjectAdapterReader _adapterReader = new LiferayProjectAdapterReader();
	private static LiferayProjectImporterReader _importerReader;
	private static LiferayCore _plugin;
	private static LiferayProjectProviderReader _providerReader;

	private ServiceTracker<ListenerRegistry, ListenerRegistry> _listenerRegistryServiceTracker;
	private ProjectChangeListener _projectChangeListener;

}