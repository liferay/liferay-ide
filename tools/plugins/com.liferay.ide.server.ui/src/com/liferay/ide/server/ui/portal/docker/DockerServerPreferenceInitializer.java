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

package com.liferay.ide.server.ui.portal.docker;



import java.io.File;

import java.util.Properties;

import org.eclipse.core.internal.preferences.EclipsePreferences;
import org.eclipse.core.internal.preferences.IPreferencesConstants;
import org.eclipse.core.internal.runtime.DataArea;
import org.eclipse.core.internal.runtime.MetaDataKeeper;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.Property;

import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Ethan Sun
 */
@SuppressWarnings("restriction")
public class DockerServerPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
//		IEclipsePreferences node = InstanceScope.INSTANCE.getNode(LiferayGradleUI.PLUGIN_ID);
//
//		DataArea dataArea = MetaDataKeeper.getMetaArea();
//
//		IPath baseLocation = dataArea.getStateLocation(IPreferencesConstants.RUNTIME_NAME);
//
//		IPath prefLocation = _computeLocation(baseLocation, LiferayGradleUI.PLUGIN_ID);
//
//		File prefFile = prefLocation.toFile();
//
//		if (!prefFile.exists()) {
//			Properties property = new Properties();
//
//			property.setProperty(LiferayGradleDockerServer.PROP_REPO_NAME, LiferayGradleDockerServer.PROP_REPO_PORTAL);
//
//			property.setProperty(
//				LiferayGradleDockerServer.PROP_REGISTRY_URL, LiferayGradleDockerServer.PROP_REGISTRY_URL_PORTAL);
//
//			property.setProperty(LiferayGradleDockerServer.PROP_STATE, "true");
//
//			JSONObject dafaultValue1 = Property.toJSONObject(property);
//
//			property = new Properties();
//
//			property.setProperty(LiferayGradleDockerServer.PROP_REPO_NAME, LiferayGradleDockerServer.PROP_REPO_DXP);
//
//			property.setProperty(
//				LiferayGradleDockerServer.PROP_REGISTRY_URL, LiferayGradleDockerServer.PROP_REGISTRY_URL_DXP);
//
//			property.setProperty(LiferayGradleDockerServer.PROP_STATE, "false");
//
//			JSONObject dafaultValue2 = Property.toJSONObject(property);
//
//			JSONArray jsonArray = new JSONArray();
//
//			jsonArray.put(dafaultValue1);
//
//			jsonArray.put(dafaultValue2);
//
//			try {
//				node.put(
//					LiferayGradleDockerServer.DOCKER_DAEMON_CONNECTION,
//					LiferayGradleDockerServer.getDefaultDockerUrl());
//
//				node.put(LiferayGradleDockerServer.DOCKER_REGISTRY_INFO, jsonArray.toString());
//
//				node.flush();
//			}
//			catch (BackingStoreException e) {
//				e.printStackTrace();
//			}
//		}
	}

	private IPath _computeLocation(IPath root, String qualifier) {
		if (root == null) {
			return null;
		}

		root = root.append(EclipsePreferences.DEFAULT_PREFERENCES_DIRNAME);

		root = root.append(qualifier);

		return root.addFileExtension(EclipsePreferences.PREFS_FILE_EXTENSION);
	}

}