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

package com.liferay.ide.upgrade.plan.core.util;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.internal.UpgradePlanCorePlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import org.eclipse.core.runtime.IPath;

/**
 * @author Lovett Li
 * @author Terry Jia
 */
public class UpgradeAssistantSettingsUtil {

	public static <T> T getObjectFromStore(Class<T> clazz) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.readValue(FileUtil.getFile(_storageLocation.append(clazz.getSimpleName() + ".json")), clazz);
		}
		catch (FileNotFoundException fnfe) {
		}

		return null;
	}

	public static <T> void setObjectToStore(Class<T> clazz, T object) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		File storageFile = FileUtil.getFile(_storageLocation.append(clazz.getSimpleName() + ".json"));

		mapper.writeValue(storageFile, object);
	}

	private static final IPath _storageLocation = UpgradePlanCorePlugin.getDefault().getStateLocation();

}