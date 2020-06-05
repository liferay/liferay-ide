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

package com.liferay.ide.core.util;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.liferay.ide.core.LiferayCore;
import com.liferay.portal.tools.bundle.support.commands.DownloadCommand;

import java.io.File;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Ethan Sun
 */
public class WorkspaceProductInfoUtil {

	public static void downloadProductInfo() {
		Job job = new Job("") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					DownloadCommand downloadCommand = new DownloadCommand();

					downloadCommand.setCacheDir(_workspaceCacheDir);
					downloadCommand.setPassword(null);
					downloadCommand.setToken(false);
					downloadCommand.setUrl(new URL(_PRODUCT_INFO_URL));
					downloadCommand.setUserName(null);
					downloadCommand.setQuiet(true);

					downloadCommand.execute();
				}
				catch (Exception exception) {
					LiferayCore.logError("Failed to dwonload product info", exception);
				}

				return Status.OK_STATUS;
			}

		};

		job.setSystem(true);

		job.setUser(false);

		job.schedule();
	}

	public static Set<String> getProductCategory() {
		Set<String> productCategory = new HashSet<>();

		try {
			Map<String, ProductInfo> productInfoMap = _getProductInfos();

			productInfoMap.forEach(
				(productKey, productInfo) -> {
					String category = productKey.split("-")[0];

					productCategory.add(category);
				});
		}
		catch (CoreException ce) {
			ce.printStackTrace();
		}

		return productCategory;
	}

	public static List<String> getProductVersionList(String key) {
		Set<String> productKeysSet = null;

		try {
			productKeysSet = _getProductInfos().keySet();
		}
		catch (CoreException ce) {
			LiferayCore.logError("Failed to load product Info.", ce);
		}

		return productKeysSet.stream(
		).filter(
			productKey -> productKey.startsWith(key)
		).collect(
			Collectors.toList()
		);
	}

	public class ProductInfo {

		public String getAppServerTomcatVersion() {
			return _appServerTomcatVersion;
		}

		public String getBundleUrl() {
			return _bundleUrl;
		}

		public String getLiferayDockerImage() {
			return _liferayDockerImage;
		}

		public String getLiferayProductVersion() {
			return _liferayProductVersion;
		}

		public String getReleaseDate() {
			return _releaseDate;
		}

		public String getTargetPlatformVersion() {
			return _targetPlatformVersion;
		}

		public boolean isInitialVersion() {
			return _initialVersion;
		}

		@SerializedName("appServerTomcatVersion")
		private String _appServerTomcatVersion;

		@SerializedName("bundleUrl")
		private String _bundleUrl;

		@SerializedName("targetPlatformVersion")
		private boolean _initialVersion = true;

		@SerializedName("liferayDockerImage")
		private String _liferayDockerImage;

		@SerializedName("liferayProductVersion")
		private String _liferayProductVersion;

		@SerializedName("releaseDate")
		private String _releaseDate;

		@SerializedName("targetPlatformVersion")
		private String _targetPlatformVersion;

	}

	private static Map<String, ProductInfo> _getProductInfos() throws CoreException {
		Path downloadPath = _workspaceCacheFile.toPath();

		try (JsonReader jsonReader = new JsonReader(Files.newBufferedReader(downloadPath))) {
			Gson gson = new Gson();

			TypeToken<Map<String, ProductInfo>> typeToken = new TypeToken<Map<String, ProductInfo>>() {
			};

			Map<String, ProductInfo> productInfoMap = gson.fromJson(jsonReader, typeToken.getType());

			if (productInfoMap == null) {
				return Collections.emptyMap();
			}

			Set<Entry<String, ProductInfo>> productInfoEntrySet = productInfoMap.entrySet();

			Iterator<Entry<String, ProductInfo>> productInfoIterator = productInfoEntrySet.iterator();

			while (productInfoIterator.hasNext()) {
				Entry<String, ProductInfo> productEntry = productInfoIterator.next();

				ProductInfo productInfo = productEntry.getValue();

				if (!productInfo.isInitialVersion()) {
					productInfoIterator.remove();
				}
			}

			return productInfoMap;
		}
		catch (Exception ce) {
			throw new CoreException(LiferayCore.createErrorStatus("Cannot Find Product Info", ce));
		}
	}

	private static final String _DEFAULT_WORKSPACE_CACHE_DIR_NAME = ".liferay/workspace";

	private static final String _DEFAULT_WORKSPACE_CACHE_FILE = ".liferay/workspace/.product_info.json";

	private static final String _PRODUCT_INFO_URL = "https://releases.liferay.com/tools/workspace/.product_info.json";

	private static File _workspaceCacheDir = new File(
		System.getProperty("user.home"), _DEFAULT_WORKSPACE_CACHE_DIR_NAME);
	private static File _workspaceCacheFile = new File(System.getProperty("user.home"), _DEFAULT_WORKSPACE_CACHE_FILE);

}