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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.LiferayCore;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 * @author Simon Jiang
 */
public class LiferayGradleCore extends Plugin {

	public static final String FAMILY_BUILDSHIP_CORE_JOBS = "org.eclipse.buildship.core.jobs";

	public static final String LIFERAY_WATCH = "liferay-watch";

	public static final String LIFERAY_WORKSPACE_WATCH_JOB_SUFFIX = "workspace";

	public static final String PLUGIN_ID = "com.liferay.ide.gradle.core";

	public static final File customModelCache = LiferayCore.GLOBAL_SETTINGS_PATH.toFile();

	public static IStatus createErrorStatus(Exception ex) {
		return new Status(IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex);
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static IStatus createWarningStatus(String msg) {
		return new Status(IStatus.WARNING, PLUGIN_ID, msg);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LiferayGradleCore getDefault() {
		return _plugin;
	}

	public static IPath getDefaultStateLocation() {
		return _plugin.getStateLocation();
	}

	public static <T> T getToolingModel(Class<T> modelClass, IProject gradleProject) {
		T retval = null;

		try {
			retval = GradleTooling.getModel(modelClass, customModelCache, gradleProject);
		}
		catch (Exception e) {
			logError("Error getting tooling model", e);
		}

		return retval;
	}

	public static void logError(Exception ex) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(ex));
	}

	public static void logError(String msg) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(msg));
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(msg, e));
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		super.stop(context);
	}

	private static LiferayGradleCore _plugin;

}