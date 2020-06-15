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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.ProductInfo;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;

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

	public void getInitPromotedWorkspaceProduct(boolean showAll, Runnable callback) {
		//
		//		try {
		//			Job job = new Job("Liferay workspace product download job") {

		//
		//				@Override
		//				protected IStatus run(IProgressMonitor monitor) {
		//					try {
		//
		//						callback.run();
		//					} catch (BladeCLIException e) {
		//						e.printStackTrace();
		//					}
		//					return Status.OK_STATUS;
		//				}
		//
		//			};
		//			job.addJobChangeListener(new JobChangeAdapter() {
		//
		//				@Override
		//				public void done(IJobChangeEvent event) {
		//
		//				}
		//			});
		//			job.setSystem(true);
		//			job.setUser(false);
		//			job.schedule();
		//		}
		//		catch (Exception exception) {
		//			LiferayCore.logError("Failed to dwonload product info", exception);
		//		}
	}

	public Set<String> getProductCategory() {
		Set<String> productCategorySet = new HashSet<>();

		if (Objects.isNull(_projectTempletes)) {
			return Collections.emptySet();
		}

		List<String> projectTempletes = Arrays.asList(_projectTempletes);

		projectTempletes.forEach(
			productTemplete -> {
				String productCategory = productTemplete.split("-")[0];

				productCategorySet.add(productCategory);
			});

		return productCategorySet;
	}

	public List<String> getProductVersionList(String productCategory, String[] productVersions) {
		if (Objects.isNull(productCategory) || Objects.isNull(productVersions)) {
			return Collections.emptyList();
		}

		return Stream.of(
			productVersions
		).filter(
			category -> category.startsWith(productCategory)
		).collect(
			Collectors.toList()
		);
	}

	public ProductInfo getWorkspaceProductInfo(String productCategory) {
		if (_workspaceCacheFile.exists()) {
			Map<String, ProductInfo> productInfos;

			try {
				if (Objects.isNull(_projectTempletes)) {
					return null;
				}

				productInfos = _getProductInfos();

				return productInfos.get(productCategory);
			}
			catch (CoreException ce) {
				ce.printStackTrace();
			}
		}

		return null;
	}

	//	public List<String> getWorkspaceProduct(String productCategory, Boolean showAll, Runnable callback) {
	//
	//		if (callback != null) {
	//			getInitPromotedWorkspaceProduct(showAll, callback);
	//		} else {
	//			return getProductVersionList(productCategory, showAll);
	//		}
	//
	//		return null;
	//
	//	}

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

	private static final String _DEFAULT_WORKSPACE_CACHE_FILE = ".liferay/workspace/.product_info.json";

	private static WorkspaceProductInfo _instance;

	private String[] _projectTempletes;
	private final File _workspaceCacheFile = new File(System.getProperty("user.home"), _DEFAULT_WORKSPACE_CACHE_FILE);

}