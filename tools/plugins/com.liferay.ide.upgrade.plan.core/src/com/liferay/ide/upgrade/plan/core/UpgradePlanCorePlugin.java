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

package com.liferay.ide.upgrade.plan.core;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.util.ZipUtil;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class UpgradePlanCorePlugin extends Plugin {

	public static final String ID = "com.liferay.ide.upgrade.plan.core";

	public static final String OFFLINE_UNZIP_FOLDER = "offline-outline";

	public static List<IUpgradePlanOutline> offlineOutlineLists = new ArrayList<>();

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, ID, msg);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, ID, msg, e);
	}

	public static IUpgradePlanOutline getFilterOutlines(String name) {
		for (IUpgradePlanOutline outline : offlineOutlineLists) {
			if (StringUtil.equals(name, outline.getName())) {
				return outline;
			}
		}

		return null;
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

		_initOfflineOutline();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_instance = null;

		super.stop(context);
	}

	private void _initOfflineOutline() throws Exception {
		IPath pluginStateLocation = _instance.getStateLocation();

		IPath offlineOutlinePath = pluginStateLocation.append(OFFLINE_UNZIP_FOLDER);

		if (FileUtil.notExists(offlineOutlinePath)) {
			Bundle bundle = Platform.getBundle(UpgradePlanCorePlugin.ID);

			Enumeration<URL> entryUrls = bundle.findEntries("resources/", "*.zip", true);

			if (ListUtil.isEmpty(entryUrls)) {
				return;
			}

			while (entryUrls.hasMoreElements()) {
				URL fileURL = FileLocator.toFileURL(entryUrls.nextElement());

				File outlineFile = new File(fileURL.getFile());

				ZipUtil.unzip(outlineFile, offlineOutlinePath.toFile());

				String outlineFilename = outlineFile.getName();

				String outlineFilenameWithoutEx = outlineFilename.split("\\.")[0];

				IPath outlinePath = offlineOutlinePath.append(outlineFilenameWithoutEx);

				offlineOutlineLists.add(new UpgradePlanOutline(outlineFilenameWithoutEx, outlinePath.toOSString()));
			}
		}
	}

	private static UpgradePlanCorePlugin _instance;

}