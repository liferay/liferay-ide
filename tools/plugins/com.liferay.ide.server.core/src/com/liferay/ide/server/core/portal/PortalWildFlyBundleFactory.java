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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.util.JavaUtil;
import com.liferay.ide.server.util.LayeredModulePathFactory;

import java.io.File;
import java.io.FileFilter;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 * @author Charles Wu
 */
public class PortalWildFlyBundleFactory extends PortalJBossBundleFactory {

	@Override
	public PortalBundle create(IPath location) {
		return new PortalWildFlyBundle(location);
	}

	@Override
	public PortalBundle create(Map<String, String> appServerProperties) {
		return new PortalWildFlyBundle(appServerProperties);
	}

	@Override
	protected boolean detectBundleDir(IPath path) {
		if (FileUtil.notExists(path)) {
			return false;
		}

		IPath modulesPath = path.append("modules");
		IPath standalonePath = path.append("standalone");
		IPath binPath = path.append("bin");

		if (FileUtil.exists(modulesPath) && FileUtil.exists(standalonePath) && FileUtil.exists(binPath)) {
			String vers = getManifestPropFromJBossModulesFolder(
				new File[] {new File(path.toPortableString(), "modules")}, "org.jboss.as.product",
				"wildfly-full/dir/META-INF", _WF_RELEASE_MANIFEST_KEY);

			if ((vers != null) && (vers.startsWith("10.") || vers.startsWith("11."))) {
				return true;
			}
			else {
				return super.detectBundleDir(path);
			}
		}

		return false;
	}

	protected String getManifestPropFromJBossModulesFolder(
		File[] moduleRoots, String moduleId, String slot, String property) {

		File[] layeredRoots = LayeredModulePathFactory.resolveLayeredModulePath(moduleRoots);

		for (File root : layeredRoots) {
			IPath[] manifests = _getFilesForModule(root, moduleId, slot, _manifestFilter());

			if (ListUtil.isNotEmpty(manifests)) {
				String value = JavaUtil.getManifestProperty(manifests[0].toFile(), property);

				if (value != null) {
					return value;
				}

				return null;
			}
		}

		return null;
	}

	private static IPath[] _getFiles(File modulesFolder, IPath moduleRelativePath, FileFilter filter) {
		File[] layeredPaths = LayeredModulePathFactory.resolveLayeredModulePath(modulesFolder);

		for (File layeredPathFile : layeredPaths) {
			IPath lay = new Path(layeredPathFile.getAbsolutePath());

			IPath relativeLayPath = lay.append(moduleRelativePath);

			File layeredPath = new File(relativeLayPath.toOSString());

			if (FileUtil.exists(layeredPath)) {
				return _getFilesFrom(layeredPath, filter);
			}
		}

		return new IPath[0];
	}

	private static IPath[] _getFilesForModule(File modulesFolder, String moduleName, String slot, FileFilter filter) {
		String slashed = moduleName.replaceAll("\\.", "/");

		slot = slot == null ? "main" : slot;

		return _getFiles(modulesFolder, new Path(slashed).append(slot), filter);
	}

	private static IPath[] _getFilesFrom(File layeredPath, FileFilter filter) {
		ArrayList<IPath> list = new ArrayList<>();
		File[] children = layeredPath.listFiles();

		for (File child : children) {
			if (filter.accept(child)) {
				list.add(new Path(child.getAbsolutePath()));
			}
		}

		return list.toArray(new IPath[list.size()]);
	}

	private static FileFilter _manifestFilter() {
		return new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				String pathName = pathname.getName();

				String pathNameLowerCase = pathName.toLowerCase();

				if (pathname.isFile() && pathNameLowerCase.equals("manifest.mf")) {
					return true;
				}

				return false;
			}

		};
	}

	private static final String _WF_RELEASE_MANIFEST_KEY = "JBoss-Product-Release-Version";

}