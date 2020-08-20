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
import java.io.IOException;

import java.net.URL;

import java.nio.file.Files;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class UpgradePlanCorePlugin extends Plugin {

	public static final String CODE_UPGRADE_ZIP_MD5 = "code-upgrade-zip-md5";

	public static final String DATABASE_UPGRADE_ZIP_MD5 = "database-upgrade-zip-md5";

	public static final String ID = "com.liferay.ide.upgrade.plan.core";

	public static final String OFFLINE_OUTLINE_KEY = "offline-outline";

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

	private static String _computeMD5(File file) {
		if (Objects.nonNull(file)) {
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");

				md5.update(Files.readAllBytes(file.toPath()));

				return DatatypeConverter.printHexBinary(md5.digest());
			}
			catch (IOException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private void _initOfflineOutline() throws Exception {
		IPreferencesService preferencesService = Platform.getPreferencesService();

		IPath pluginStateLocation = _instance.getStateLocation();

		IPath offlineOutlinePath = pluginStateLocation.append(OFFLINE_UNZIP_FOLDER);

		Bundle bundle = Platform.getBundle(UpgradePlanCorePlugin.ID);

		Enumeration<URL> entryUrls = bundle.findEntries("resources/", "*.zip", true);

		if (ListUtil.isEmpty(entryUrls)) {
			return;
		}

		while (entryUrls.hasMoreElements()) {
			URL fileURL = FileLocator.toFileURL(entryUrls.nextElement());

			File outlineFile = new File(fileURL.getFile());

			String contentZipMD5 = "";

			switch (outlineFile.getName()) {
				case "code-upgrade.zip":
					contentZipMD5 = CODE_UPGRADE_ZIP_MD5;

					break;

				case "database-upgrade.zip":
					contentZipMD5 = DATABASE_UPGRADE_ZIP_MD5;

					break;
			}

			String outlineFilename = outlineFile.getName();

			String outlineFilenameWithoutEx = outlineFilename.split("\\.")[0];

			String storedMD5 = preferencesService.getString(UpgradePlanCorePlugin.ID, contentZipMD5, "", null);

			String updateMD5 = _computeMD5(outlineFile);

			IPath offlineDocDirPath = offlineOutlinePath.append(outlineFilenameWithoutEx);

			if (!updateMD5.equals(storedMD5) || FileUtil.notExists(offlineDocDirPath)) {
				FileUtil.deleteDir(offlineDocDirPath.toFile(), true);

				ZipUtil.unzip(outlineFile, offlineDocDirPath.toFile());

				_prefstore.put(contentZipMD5, updateMD5);

				_prefstore.flush();
			}

			offlineOutlineLists.add(new UpgradePlanOutline(outlineFilenameWithoutEx, offlineDocDirPath.toOSString()));
		}
	}

	private static UpgradePlanCorePlugin _instance;

	private IEclipsePreferences _prefstore = InstanceScope.INSTANCE.getNode(ID);

}