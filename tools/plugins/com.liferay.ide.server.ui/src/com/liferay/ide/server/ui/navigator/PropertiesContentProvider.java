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

package com.liferay.ide.server.ui.navigator;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.remote.IRemoteServer;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public class PropertiesContentProvider extends AbstractNavigatorContentProvider {

	public PropertiesContentProvider() {
	}

	public void dispose() {
		_propertiesFilesMap.clear();
	}

	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getPipelinedChildren(Object parent, Set currentChildren) {
		if (_shouldAddChildren(parent)) {
			IServer server = (IServer)parent;

			PropertiesFile[] propertiesFiles = _propertiesFilesMap.get(server.getId());

			if (ListUtil.isEmpty(propertiesFiles)) {
				ILiferayRuntime runtime = ServerUtil.getLiferayRuntime(server);

				if (runtime != null) {
					File[] files = _getExtPropertiesFiles(runtime);

					List<PropertiesFile> newFiles = new ArrayList<>();

					for (File file : files) {
						newFiles.add(new PropertiesFile(file));
					}

					propertiesFiles = newFiles.toArray(new PropertiesFile[0]);

					_propertiesFilesMap.put(server.getId(), propertiesFiles);
				}
			}

			if (ListUtil.isNotEmpty(propertiesFiles)) {
				for (PropertiesFile propertiesFile : propertiesFiles) {
					currentChildren.add(propertiesFile);
				}
			}
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		boolean retVal = false;

		if (element instanceof IServer) {
			IServer server = (IServer)element;

			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(server);

			if (liferayRuntime != null) {
				File[] files = _getExtPropertiesFiles(liferayRuntime);

				return ListUtil.isNotEmpty(files);
			}
		}

		return retVal;
	}

	@Override
	public boolean hasPipelinedChildren(Object element, boolean currentHasChildren) {
		return hasChildren(element);
	}

	private File[] _getExtPropertiesFiles(ILiferayRuntime liferayRuntime) {
		File[] retVal = new File[0];

		IPath liferayHome = liferayRuntime.getLiferayHome();

		if (liferayHome != null) {
			File liferayHomeDir = liferayHome.toFile();

			File[] files = liferayHomeDir.listFiles(
				(dir, name) -> dir.equals(liferayHomeDir) && name.endsWith("-ext.properties"));

			if (files != null) {
				retVal = files;
			}
		}

		return retVal;
	}

	private boolean _shouldAddChildren(Object parent) {
		if (parent instanceof IServer) {
			IServer server = (IServer)parent;

			ILiferayServer liferayServer = (ILiferayServer)server.loadAdapter(ILiferayServer.class, null);

			if (!(liferayServer instanceof IRemoteServer)) {
				return true;
			}
		}

		return false;
	}

	private Map<String, PropertiesFile[]> _propertiesFilesMap = new HashMap<>();

}