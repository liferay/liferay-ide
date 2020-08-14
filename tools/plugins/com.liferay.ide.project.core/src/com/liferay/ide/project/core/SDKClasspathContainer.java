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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;

/**
 * @author Simon.Jiang
 */
public class SDKClasspathContainer extends PluginClasspathContainer implements IClasspathContainer {

	public static final String ID = "com.liferay.ide.sdk.container";

	public SDKClasspathContainer(
		IPath containerPath, IJavaProject project, IPath portalDir, String javadocURL, IPath sourceURL,
		IPath portalGlobalDir, IPath bundleDir, IPath[] bundleLibDependencyPaths, IPath[] sdkDependencyPaths) {

		super(containerPath, project, portalDir, javadocURL, sourceURL);

		_portalGlobalDir = portalGlobalDir;
		_bundleDir = bundleDir;
		_bundleLibDependencyPaths = bundleLibDependencyPaths;
		_sdkDependencyPaths = sdkDependencyPaths;
	}

	public IPath getBundleDir() {
		return _bundleDir;
	}

	public IPath[] getBundleLibDependencyPath() {
		return _bundleLibDependencyPaths;
	}

	@Override
	public IClasspathEntry[] getClasspathEntries() {
		if (classpathEntries != null) {
			return classpathEntries;
		}

		List<IClasspathEntry> entries = new ArrayList<>();

		for (IPath pluginJarPath : _bundleLibDependencyPaths) {
			IPath sourcePath = null;

			if (portalSourceJars.contains(pluginJarPath.lastSegment())) {
				sourcePath = getSourceLocation();
			}

			entries.add(createClasspathEntry(pluginJarPath, sourcePath, javadocURL));
		}

		if (portalDir != null) {
			for (String pluginJar : getPortalJars()) {
				entries.add(createPortalJarClasspathEntry(pluginJar));
			}

			for (String pluginPackageJar : getPortalDependencyJars()) {
				entries.add(createPortalJarClasspathEntry(pluginPackageJar));
			}
		}

		if (_sdkDependencyPaths != null) {
			for (IPath sdkDependencyJarPath : _sdkDependencyPaths) {
				entries.add(createClasspathEntry(sdkDependencyJarPath, null, null));
			}
		}

		classpathEntries = entries.toArray(new IClasspathEntry[0]);

		return classpathEntries;
	}

	@Override
	public String getDescription() {
		return "Plugins SDK Dependencies";
	}

	public IPath getPortalGlobalDir() {
		return _portalGlobalDir;
	}

	@Override
	protected String[] getPortalJars() {
		return new String[] {
			"commons-logging.jar", "log4j.jar", "util-bridges.jar", "util-java.jar", "util-taglib.jar"
		};
	}

	private final IPath _bundleDir;
	private final IPath[] _bundleLibDependencyPaths;
	private final IPath _portalGlobalDir;
	private final IPath[] _sdkDependencyPaths;

}