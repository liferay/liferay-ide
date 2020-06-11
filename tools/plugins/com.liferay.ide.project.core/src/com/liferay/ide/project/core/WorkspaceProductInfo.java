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

package com.liferay.ide.project.core;

import com.beust.jcommander.internal.Sets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.ProductInfo;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.portal.tools.bundle.support.commands.DownloadCommand;

import java.io.File;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Ethan Sun
 * @author Simon Jiang
 */
public class WorkspaceProductInfo {

	public static WorkspaceProductInfo getInstance() {
		if (_instance == null) {
			_instance = new WorkspaceProductInfo();
		}

		return _instance;
	}

	public Set<String> getProductCategory() {
		Set<String> productCategorySet = new HashSet<>();

		if (_workspaceCacheFile.exists()) {
			try {
				Map<String, ProductInfo> productInfoMap = _getProductInfos();

				if (Objects.isNull(productInfoMap)) {
					return Collections.emptySet();
				}

				productInfoMap.forEach(
					(productKey, productInfo) -> {
						if (CoreUtil.isNotNullOrEmpty(productInfo.getTargetPlatformVersion())) {
							String category = productKey.split("-")[0];

							productCategorySet.add(category);
						}
					});
			}
			catch (CoreException ce) {
				ProjectCore.logError("Failed to get liferay workspace product information.", ce);
			}
		}

		return productCategorySet;
	}

	public List<String> getProductVersionList(String category, boolean showAll) {
		List<String> productVersionList = new ArrayList<>();

		if (_workspaceCacheFile.exists()) {
			final Set<String> productCategorySet = Sets.newHashSet();

			try {
				Map<String, ProductInfo> productInfos = _getProductInfos();

				if (Objects.isNull(productInfos)) {
					return Collections.emptyList();
				}

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

				if (productCategorySet != null) {
					productVersionList = productCategorySet.stream(
					).filter(
						productCategory -> productCategory.startsWith(category)
					).collect(
						Collectors.toList()
					);
				}
			}
			catch (CoreException ce) {
				LiferayCore.logError("Failed to load product Info.", ce);
			}
		}

		return productVersionList;
	}

	public ProductInfo getWorkspaceProductInfo(String productKey) {
		try {
			Map<String, ProductInfo> productInfos = _getProductInfos();

			if (Objects.isNull(productInfos)) {
				return null;
			}

			return productInfos.get(productKey);
		}
		catch (Exception e) {
		}

		return null;
	}

	public void startWorkspaceProductDownload(Runnable callback) {
		if (FileUtil.notExists(_workspaceCacheFile)) {
			Job job = new Job("Liferay workspace product download job") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						DownloadCommand downloadCommand = new DownloadCommand() {

							public void onCompleted() {
								System.out.println("GGGGGGGGGGGGGGGGGGG");
								callback.run();
							}

						};

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
	}

	private Map<String, ProductInfo> _getProductInfos() throws CoreException {
		Path downloadPath = _workspaceCacheFile.toPath();

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

	private static final String _DEFAULT_WORKSPACE_CACHE_FILE = ".liferay/workspace/.product_info.json";

	private static final String _PRODUCT_INFO_URL = "https://releases.liferay.com/tools/workspace/.product_info.json";

	private static WorkspaceProductInfo _instance;

	private static final File _workspaceCacheDir = new File(
		System.getProperty("user.home"), _DEFAULT_WORKSPACE_CACHE_DIR_NAME);

	private final File _workspaceCacheFile = new File(System.getProperty("user.home"), _DEFAULT_WORKSPACE_CACHE_FILE);

}