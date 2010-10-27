/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.server.core;

import com.liferay.ide.eclipse.core.ILiferayConstants;
import com.liferay.ide.eclipse.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.jdt.internal.classpath.ClasspathDecorations;
import org.eclipse.jst.common.jdt.internal.classpath.ClasspathDecorationsManager;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public abstract class PluginClasspathContainer implements IClasspathContainer {

	protected static ClasspathDecorationsManager cpDecorations;

	protected static final String SEPARATOR = "!";

	static {
		cpDecorations = new ClasspathDecorationsManager(PortalServerCorePlugin.PLUGIN_ID);
	}

	public static String getDecorationManagerKey(IProject project, String container) {
		return project.getName() + SEPARATOR + container;
	}

	static ClasspathDecorationsManager getDecorationsManager() {
		return cpDecorations;
	}

	protected IClasspathEntry[] classpathEntries;

	protected IPath path;

	protected IPath portalRoot;

	protected IJavaProject project;

	public PluginClasspathContainer(IPath containerPath, IJavaProject project, IPath portalRoot) {
		this.path = containerPath;
		this.project = project;
		this.portalRoot = portalRoot;
	}

	public IClasspathEntry[] getClasspathEntries() {
		if (this.classpathEntries == null) {
			List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

			if (this.portalRoot == null) {
				return entries.toArray(new IClasspathEntry[0]);
			}

			for (String pluginJar : getPluginJars()) {
				entries.add(createClasspathEntry(pluginJar));
			}

			for (String pluginPackageJar : getPluginPackageJars()) {
				entries.add(createClasspathEntry(pluginPackageJar));
			}

			this.classpathEntries = entries.toArray(new IClasspathEntry[entries.size()]);
		}

		return this.classpathEntries;
	}

	public abstract String getDescription();

	public int getKind() {
		return K_APPLICATION;
	}

	public IPath getPath() {
		return this.path;
	}

	public IPath getPortalRoot() {
		return portalRoot;
	}

	protected IClasspathEntry createClasspathEntry(String pluginJar) {
		IPath sourcePath = null;
		IPath sourceRootPath = null;
		IAccessRule[] rules = new IAccessRule[] {};
		IClasspathAttribute[] attrs = new IClasspathAttribute[] {};
		IPath entryPath = this.portalRoot.append("/WEB-INF/lib/" + pluginJar);

		final ClasspathDecorations dec =
			cpDecorations.getDecorations(
				getDecorationManagerKey(project.getProject(), getPath().toString()), entryPath.toString());

		if (dec != null) {
			sourcePath = dec.getSourceAttachmentPath();
			sourceRootPath = dec.getSourceAttachmentRootPath();
			attrs = dec.getExtraAttributes();
		}

		IClasspathEntry newEntry = null;

		newEntry = JavaCore.newLibraryEntry(entryPath, sourcePath, sourceRootPath, rules, attrs, false);

		return newEntry;
	}

	protected IClasspathEntry findSuggestedEntry(IPath jarPath, IClasspathEntry[] suggestedEntries) {
		// compare jarPath to an existing entry
		if (jarPath != null && (!CoreUtil.isNullOrEmpty(jarPath.toString())) &&
			(!CoreUtil.isNullOrEmpty(suggestedEntries))) {
			int matchLength = jarPath.segmentCount();

			for (IClasspathEntry suggestedEntry : suggestedEntries) {
				IPath suggestedPath = suggestedEntry.getPath();
				IPath pathToMatch =
					suggestedPath.removeFirstSegments(suggestedPath.segmentCount() - matchLength).setDevice(null).makeAbsolute();
				if (jarPath.equals(pathToMatch)) {
					return suggestedEntry;
				}
			}
		}

		return null;
	}

	protected String[] getJarsfromPackagePropertiesFile(IFile pluginPackageFile) {
		Properties props = new Properties();

		try {
			props.load(pluginPackageFile.getContents());

			String deps = props.getProperty("portal-dependency-jars", "");

			String[] split = deps.split(",");

			if (split.length > 0 && !(CoreUtil.isNullOrEmpty(split[0]))) {
				return split;
			}
		}
		catch (Exception e) {
		}

		return new String[0];
	}

	protected abstract String[] getPluginJars();

	protected String[] getPluginPackageJars() {
		String[] jars = new String[0];

		IVirtualComponent comp = ComponentCore.createComponent(this.project.getProject());

		if (comp != null) {
			IContainer resource = comp.getRootFolder().getUnderlyingFolder();

			if (resource instanceof IFolder) {
				IFolder webroot = (IFolder) resource;

				IFile pluginPackageFile =
					webroot.getFile("WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE);

				if (!pluginPackageFile.exists()) {
					// IDE-226 the file may be missing because we are in an ext plugin which has a different layout
					// check for ext-web in the path to the docroot
					try {
						if (webroot.getFullPath().toPortableString().endsWith("WEB-INF/ext-web/docroot")) {
							// look for packages file in first docroot
							IPath parentDocroot = webroot.getFullPath().removeFirstSegments(1).removeLastSegments(3);
							IFolder parentWebroot = this.project.getProject().getFolder(parentDocroot);
							if (parentWebroot.exists()) {
								pluginPackageFile =
									parentWebroot.getFile("WEB-INF/" +
										ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE);
							}
						}
					}
					catch (Exception ex) {
						PortalServerCorePlugin.logError(ex);
					}
				}

				if (pluginPackageFile.exists()) {
					jars = getJarsfromPackagePropertiesFile(pluginPackageFile);
				}			}
	
		}

		return jars;
	}

}
