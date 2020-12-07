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

package com.liferay.ide.upgrade.planner.ui;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

/**
 * @author Terry Jia
 */
public class UpgradePlannerPlugin extends AbstractUIPlugin implements IStartup {

	public static final String ACCEPTED_EULA = "ACCEPTED_EULA";

	public static final String FIRST_LAUNCH_COMPLETE = "FIRST_LAUNCH_COMPLETE";

	public static final String PLUGIN_ID = "com.liferay.ide.upgrade.planner.ui";

	public static final String PRODUCT_ID = PLUGIN_ID + ".product";

	public static IStatus createErrorStatus(Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e);
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static IStatus createInfoStatus(String msg) {
		return new Status(IStatus.INFO, PLUGIN_ID, msg);
	}

	public static UpgradePlannerPlugin getDefault() {
		return _plugin;
	}

	public static boolean isFirstLaunch() {
		IScopeContext[] scopes = {ConfigurationScope.INSTANCE, InstanceScope.INSTANCE};

		IPreferencesService preferencesService = Platform.getPreferencesService();

		return !preferencesService.getBoolean(PLUGIN_ID, FIRST_LAUNCH_COMPLETE, false, scopes);
	}

	public static boolean isProductRunning() {
		boolean productRunning = false;

		IProduct product = Platform.getProduct();

		if (product != null) {
			productRunning = PRODUCT_ID.equals(product.getId());
		}

		return productRunning;
	}

	public static void logError(Exception e) {
		logError(e.getMessage(), e);
	}

	public static void logError(IStatus status) {
		ILog log = getDefault().getLog();

		log.log(status);
	}

	public static void logError(String msg, Exception ex) {
		logError(createErrorStatus(msg, ex));
	}

	public static void logInfo(String msg) {
		ILog log = getDefault().getLog();

		log.log(createInfoStatus(msg));
	}

	/**
	 * The constructor
	 */
	public UpgradePlannerPlugin() {
	}

	public void earlyStartup() {
		if (!isProductRunning()) {
			//TODO
		}
	}

	public IEclipsePreferences getPreferences() {
		return InstanceScope.INSTANCE.getNode(PLUGIN_ID);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private static UpgradePlannerPlugin _plugin;

}