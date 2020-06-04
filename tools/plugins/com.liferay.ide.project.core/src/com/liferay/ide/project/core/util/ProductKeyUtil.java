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

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.portal.tools.bundle.support.commands.DownloadCommand;

import java.io.File;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Ethan Sun
 */
public class ProductKeyUtil {

	public static void downloadProductKey() {
		Job job = Job.create(
			"Request ProductKeys",
			(ICoreRunnable)monitor -> {
				try {
					DownloadCommand downloadCommand = new DownloadCommand();

					downloadCommand.setCacheDir(_workspaceCacheDir);
					downloadCommand.setPassword(null);
					downloadCommand.setToken(false);
					downloadCommand.setUrl(new URL(_PRODUCT_INFO_URL));
					downloadCommand.setUserName(null);

					downloadCommand.execute();
				}
				catch (Exception exception) {
					exception.printStackTrace();
				}
			});

		job.setSystem(true);

		job.setUser(true);

		job.schedule();
	}

	public static Set<String> getKeyCategory() {
		Set<String> productCategory = new HashSet<>();

		try {
			Set<String> productKeysSet = getProductKeys().keySet();

			productKeysSet.forEach(
				entry -> {
					String category = entry.split("-")[0];

					productCategory.add(category);
				});
		}
		catch (CoreException ce) {
			ce.printStackTrace();
		}

		return productCategory;
	}

	public static Map<String, ProductInfo> getProductKeys() throws CoreException {
		Path downloadPath = _workspaceCacheFile.toPath();

		try (JsonReader jsonReader = new JsonReader(Files.newBufferedReader(downloadPath))) {
			Gson gson = new Gson();

			TypeToken<Map<String, ProductInfo>> typeToken = new TypeToken<Map<String, ProductInfo>>() {
			};

			return gson.fromJson(jsonReader, typeToken.getType());
		}
		catch (Exception ce) {
			throw new CoreException(ProjectCore.createErrorStatus("Cannot Find ProductKeys Info", ce));
		}
	}

	public static List<String> getProductVersionList(String key) {
		Set<String> productKeysSet = null;

		try {
			productKeysSet = getProductKeys().keySet();
		}
		catch (CoreException ce) {
			ce.printStackTrace();
		}

		Map<String, List<String>> productInfoGroupByMap = productKeysSet.stream(
		).collect(
			Collectors.groupingBy(entry -> entry.split("-")[0])
		);

		if ("commerce".equals(key)) {
			return productInfoGroupByMap.get(
				key
			).stream(
			).sorted(
				Comparator.reverseOrder()
			).collect(
				Collectors.toList()
			);
		}
		else {
			List<String> productVersionList = new ArrayList<>();

			List<String> multiGAVersion = productInfoGroupByMap.get(
				key
			).stream(
			).filter(
				micro -> micro.split("-")[2].contains("ga")
			).collect(
				Collectors.toList()
			);

			productVersionList.addAll(multiGAVersion);

			Map<String, List<String>> maxSPVersionList = productInfoGroupByMap.get(
				key
			).stream(
			).filter(
				micro -> micro.split("-")[2].contains("sp")
			).collect(
				Collectors.groupingBy(entry -> entry.split("-")[1])
			);

			maxSPVersionList.forEach(
				(minor, list) -> {
					Optional<String> maxMinorVersion = list.stream(
					).max(
						(v1, v2) -> Integer.valueOf(
							v1.split("-")[2].substring(2)
						).compareTo(
							Integer.valueOf(v2.split("-")[2].substring(2))
						)
					);

					if (maxMinorVersion.isPresent()) {
						productVersionList.add(maxMinorVersion.get());
					}
				});

			return productVersionList;
		}
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

		@SerializedName("appServerTomcatVersion")
		private String _appServerTomcatVersion;

		@SerializedName("bundleUrl")
		private String _bundleUrl;

		@SerializedName("liferayDockerImage")
		private String _liferayDockerImage;

		@SerializedName("liferayProductVersion")
		private String _liferayProductVersion;

		@SerializedName("releaseDate")
		private String _releaseDate;

		@SerializedName("targetPlatformVersion")
		private String _targetPlatformVersion;

	}

	private static final String _DEFAULT_WORKSPACE_CACHE_DIR_NAME = ".liferay/workspace";

	private static final String _DEFAULT_WORKSPACE_CACHE_FILE = ".liferay/workspace/.product_info.json";

	private static final String _PRODUCT_INFO_URL = "https://releases.liferay.com/tools/workspace/.product_info.json";

	private static File _workspaceCacheDir = new File(
		System.getProperty("user.home"), _DEFAULT_WORKSPACE_CACHE_DIR_NAME);
	private static File _workspaceCacheFile = new File(System.getProperty("user.home"), _DEFAULT_WORKSPACE_CACHE_FILE);

}