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

package com.liferay.ide.project.core.util;

import com.beust.jcommander.internal.Sets;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

	public static final String DEFAULT_WORKSPACE_CACHE_FILE = ".liferay/workspace/.product_info.json";

	public static Job job = new Job("") {

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

	public static final File workspaceCacheFile = new File(
		System.getProperty("user.home"), DEFAULT_WORKSPACE_CACHE_FILE);

	public static void downloadProductInfo() {
		job.setSystem(true);

		job.setUser(false);

		job.schedule();
	}

	public static Set<String> getProductCategory() {
		Set<String> productCategorySet = new HashSet<>();

		if (workspaceCacheFile.exists()) {
			productCategorySet.clear();

			try {
				Map<String, ProductInfo> productInfoMap = _getProductInfos();

				productInfoMap.forEach(
					(productCategory, productInfo) -> {
						String category = productCategory.split("-")[0];

						productCategorySet.add(category);
					});
			}
			catch (CoreException ce) {
				ce.printStackTrace();
			}
		}

		return productCategorySet;
	}

	public static List<String> getProductVersionList(String category, boolean showAll) {
		List<String> productVersionList = new ArrayList<>();

		if (workspaceCacheFile.exists()) {
			final Set<String> productCategorySet = Sets.newHashSet();

			try {
				Map<String, ProductInfo> productInfos = _getProductInfos();

				if (!showAll) {
					productInfos.forEach(
						(key, product) -> {
							if (product.isInitialVersion()) {
								productCategorySet.add(key);
							}
						});
				}
				else {
					productCategorySet.addAll(_getProductInfos().keySet());
				}
			}
			catch (CoreException ce) {
				LiferayCore.logError("Failed to load product Info.", ce);
			}

			if (productCategorySet != null) {
				productVersionList = productCategorySet.stream(
				).filter(
					productCategory -> productCategory.startsWith(category)
				).collect(
					Collectors.toList()
				);
			}
		}

		return productVersionList;
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
			return getLiferayProductVersion().contains("GA");
		}

		@SerializedName("appServerTomcatVersion")
		private String _appServerTomcatVersion;

		@SerializedName("bundleUrl")
		private String _bundleUrl;

		//		@SerializedName("initialVersion")
		//		private boolean _initialVersion = true;

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
		Path downloadPath = workspaceCacheFile.toPath();

		try (JsonReader jsonReader = new JsonReader(Files.newBufferedReader(downloadPath))) {
			Gson gson = new Gson();

			TypeToken<Map<String, ProductInfo>> typeToken = new TypeToken<Map<String, ProductInfo>>() {
			};

			return gson.fromJson(jsonReader, typeToken.getType());
		}
		catch (Exception ce) {
			throw new CoreException(LiferayCore.createErrorStatus("Cannot Find Product Info", ce));
		}
	}

	private static final String _DEFAULT_WORKSPACE_CACHE_DIR_NAME = ".liferay/workspace";

	private static final String _PRODUCT_INFO_URL = "https://releases.liferay.com/tools/workspace/.product_info.json";

	private static final File _workspaceCacheDir = new File(
		System.getProperty("user.home"), _DEFAULT_WORKSPACE_CACHE_DIR_NAME);

}