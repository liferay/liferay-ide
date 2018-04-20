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

package com.liferay.ide.alloy.core.webresources_src;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesFileSystemProvider;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
public class PortalResourcesProvider implements IWebResourcesFileSystemProvider {

	@Override
	public File[] getResources(IWebResourcesContext context) {
		File[] retval = null;
		IFile htmlFile = context.getHtmlFile();

		ILiferayProject project = LiferayCore.create(htmlFile.getProject());

		if ((htmlFile != null) && (project != null)) {
			ILiferayPortal portal = project.adapt(ILiferayPortal.class);

			if ((portal != null) && ProjectUtil.isPortletProject(htmlFile.getProject())) {
				IPath portalDir = portal.getAppServerPortalDir();

				if (portalDir != null) {
					IPath cssPath = portalDir.append("html/themes/_unstyled/css");

					if (FileUtil.exists(cssPath)) {
						synchronized (_fileCache) {
							Collection<File> cachedFiles = _fileCache.get(cssPath);

							if (cachedFiles != null) {
								retval = cachedFiles.toArray(new File[0]);
							}
							else {
								String types = new String[] {"css", "scss"};

								Collection<File> files = FileUtils.listFiles(cssPath.toFile(), types, true);

								Collection<File> cached = new HashSet<>();

								for (File file : files) {
									String fileName = file.getName();

									if (fileName.endsWith("scss")) {
										File cachedFile = new File(
											file.getParent(), ".sass-cache/" + fileName.replaceAll("scss$", "css"));

										if (FileUtil.exists(cachedFile)) {
											cached.add(file);
										}
									}
								}

								files.removeAll(cached);

								if (files != null) {
									retval = files.toArray(new File[0]);
								}

								_fileCache.put(cssPath, files);
							}
						}
					}
				}
			}
			else if ((portal != null) && ProjectUtil.isLayoutTplProject(htmlFile.getProject())) {

				// return the static css resource for layout template names based on the version

				String version = portal.getVersion();

				try {
					if ((version != null) && (version.startsWith("6.0") || version.startsWith("6.1"))) {
						retval = _createLayoutHelperFiles("resources/layouttpl-6.1.css");
					}
					else if (version != null) {
						retval = _createLayoutHelperFiles("resources/layouttpl-6.2.css");
					}
				}
				catch (IOException ioe) {
					AlloyCore.logError("Unable to load layout template helper css files", ioe);
				}
			}
		}

		return retval;
	}

	private File[] _createLayoutHelperFiles(String path) throws IOException {
		AlloyCore plugin = AlloyCore.getDefault();

		Bundle bundle = plugin.getBundle();

		URL url = FileLocator.toFileURL(bundle.getEntry(path));

		return new File[] {new File(url.getFile())};
	}

	private static final Map<IPath, Collection<File>> _fileCache = new HashMap<>();

}