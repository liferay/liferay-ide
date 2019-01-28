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

package com.liferay.ide.upgrade.plan.core.internal;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class UpgradePlanCorePlugin extends Plugin {

	public static final String ID = "com.liferay.ide.upgrade.plan.core";

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, ID, msg);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, ID, msg, e);
	}

	public static Plugin getDefault() {
		return _instance;
	}

	public static UpgradePlanCorePlugin getInstance() {
		return _instance;
	}

	public static void logError(String msg) {
		ILog log = _instance.getLog();

		log.log(createErrorStatus(msg));
	}

	public static void logError(String msg, Exception e) {
		ILog log = _instance.getLog();

		log.log(createErrorStatus(msg, e));
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_instance = null;

		super.stop(context);
	}

	private static UpgradePlanCorePlugin _instance;

}