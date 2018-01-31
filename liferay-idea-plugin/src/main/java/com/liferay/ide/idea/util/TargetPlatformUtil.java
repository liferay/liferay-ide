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

package com.liferay.ide.idea.util;

import com.liferay.ide.idea.ui.LiferayIdeaUI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author Lovett Li
 */
public class TargetPlatformUtil {

	public static ServiceContainer getServicesList() throws Exception {
		if (_service == null) {
			File tpIndexFile = _checkCurrentTargetPlatform("service");

			_service = _getServicesNameList(tpIndexFile);
		}

		return _service;
	}

	public static ServiceContainer getServiceWrapperList() throws Exception {
		if (_serviceWrapper == null) {
			File tpIndexFile = _checkCurrentTargetPlatform("servicewrapper");

			_serviceWrapper = _getServicesNameList(tpIndexFile);
		}

		return _serviceWrapper;
	}

	private static File _checkCurrentTargetPlatform(String type) throws IOException {
		return _useSpecificTargetPlatform("ce-7.0-ga3", type);
	}

	private static ServiceContainer _getServicesNameList(File tpFile) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String[]> map = mapper.readValue(tpFile, Map.class);

		Set<String> keySet = map.keySet();

		String[] services = keySet.toArray(new String[0]);

		return new ServiceContainer(Arrays.asList(services));
	}

	private static File _useSpecificTargetPlatform(String currentVersion, String type) throws IOException {
		Path path = Paths.get(
			LiferayIdeaUI.USER_TEMP_DIR.toString(), "target-platform", currentVersion,
			currentVersion + "-" + type + ".json");

		File file = path.toFile();

		if (!file.exists()) {
			ClassLoader bladeClassLoader = BladeCLI.class.getClassLoader();

			try (InputStream in = bladeClassLoader.getResourceAsStream(
					"/target-platform/" + currentVersion + "/" + currentVersion + "-" + type + ".json")) {

				FileUtil.writeFile(file, in);
			}
			catch (IOException ioe) {
			}
		}

		return file;
	}

	private static ServiceContainer _service = null;
	private static ServiceContainer _serviceWrapper = null;

}