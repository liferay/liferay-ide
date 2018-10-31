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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ComponentUtil;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public abstract class PluginClasspathContainer implements IClasspathContainer {

	public static String getDecorationManagerKey(IProject project, String container) {
		return project.getName() + SEPARATOR + container;
	}

	public static ClasspathDecorationsManager getDecorationsManager() {
		return cpDecorations;
	}

	public PluginClasspathContainer(
		IPath containerPath, IJavaProject project, IPath portalDir, String javadocURL, IPath sourceURL) {

		path = containerPath;

		javaProject = project;

		this.portalDir = portalDir;

		this.javadocURL = javadocURL;

		sourceLocation = sourceURL;
	}

	public IClasspathEntry[] getClasspathEntries() {
		if (classpathEntries != null) {
			return classpathEntries;
		}

		List<IClasspathEntry> entries = new ArrayList<>();

		if (portalDir != null) {
			for (String pluginJar : getPortalJars()) {
				entries.add(createPortalJarClasspathEntry(pluginJar));
			}

			for (String pluginPackageJar : getPortalDependencyJars()) {
				entries.add(createPortalJarClasspathEntry(pluginPackageJar));
			}
		}

		classpathEntries = entries.toArray(new IClasspathEntry[entries.size()]);

		return classpathEntries;
	}

	public abstract String getDescription();

	public String getJavadocURL() {
		return javadocURL;
	}

	public int getKind() {
		return K_APPLICATION;
	}

	public IPath getPath() {
		return path;
	}

	public IPath getPortalDir() {
		return portalDir;
	}

	public IPath getSourceLocation() {
		return sourceLocation;
	}

	protected IClasspathEntry createClasspathEntry(IPath entryPath, IPath sourcePath) {
		return createClasspathEntry(entryPath, sourcePath, null);
	}

	protected IClasspathEntry createClasspathEntry(IPath entryPath, IPath sourceLocation, String javadocURL) {
		IPath sourceRootPath = null;
		IPath sourcePath = null;
		IAccessRule[] rules = {};
		IClasspathAttribute[] attrs = new IClasspathAttribute[0];

		ClasspathDecorations dec = cpDecorations.getDecorations(
			getDecorationManagerKey(javaProject.getProject(), getPath().toString()), entryPath.toString());

		if (dec != null) {
			sourcePath = dec.getSourceAttachmentPath();
			sourceRootPath = dec.getSourceAttachmentRootPath();
			attrs = dec.getExtraAttributes();
		}

		if (javadocURL != null) {
			if (ListUtil.isEmpty(attrs)) {
				attrs = new IClasspathAttribute[] {newJavadocAttr(javadocURL)};
			}
			else {
				List<IClasspathAttribute> newAttrs = new ArrayList<>();

				for (IClasspathAttribute attr : attrs) {
					if (IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME.equals(attr.getName())) {
						newAttrs.add(newJavadocAttr(javadocURL));
					}
					else {
						newAttrs.add(attr);
					}
				}

				attrs = newAttrs.toArray(new IClasspathAttribute[0]);
			}
		}

		if ((sourcePath == null) && (sourceLocation != null)) {
			sourcePath = sourceLocation;
		}

		return JavaCore.newLibraryEntry(entryPath, sourcePath, sourceRootPath, rules, attrs, false);
	}

	protected IClasspathEntry createContextClasspathEntry(String context) {
		IClasspathEntry entry = null;

		IFile serviceJar = ComponentUtil.findServiceJarForContext(context);

		if (serviceJar.exists()) {
			IWebProject webproject = LiferayCore.create(IWebProject.class, serviceJar.getProject());

			// IDE-110 IDE-648

			if ((webproject != null) && (webproject.getDefaultDocrootFolder() != null)) {
				IFolder defaultDocroot = webproject.getDefaultDocrootFolder();

				IFolder serviceFolder = defaultDocroot.getFolder(new Path("WEB-INF/service"));

				if (serviceFolder.exists()) {
					entry = createClasspathEntry(serviceJar.getLocation(), serviceFolder.getLocation());
				}
			}

			if (entry == null) {
				entry = createClasspathEntry(serviceJar.getLocation(), null);
			}
		}

		// TODO IDE-657 IDE-110

		if (entry == null) {
			IProject project = this.javaProject.getProject();

			SDK sdk = SDKUtil.getSDK(project);

			IPath sdkLocation = sdk.getLocation();

			String type = StringPool.EMPTY;

			if (ProjectUtil.isPortletProject(project)) {
				type = "portlets";
			}
			else if (ProjectUtil.isHookProject(project)) {
				type = "hooks";
			}
			else if (ProjectUtil.isExtProject(project)) {
				type = "ext";
			}

			IPath p = sdkLocation.append(type);

			IPath contextPath = p.append(context);

			String libFolder = ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/lib";

			contextPath = contextPath.append(libFolder);

			IPath serviceJarPath = contextPath.append(context + "-service.jar");

			if (FileUtil.exists(serviceJarPath)) {
				serviceJarPath = serviceJarPath.removeLastSegments(2);

				IPath servicePath = serviceJarPath.append("service");

				entry = createClasspathEntry(serviceJarPath, FileUtil.exists(servicePath) ? servicePath : null);
			}
		}

		return entry;
	}

	protected IClasspathEntry createPortalJarClasspathEntry(String portalJar) {
		IPath entryPath = portalDir.append("/WEB-INF/lib/" + portalJar);

		IPath sourcePath = null;

		if (portalSourceJars.contains(portalJar)) {
			sourcePath = getSourceLocation();
		}

		return createClasspathEntry(entryPath, sourcePath, javadocURL);
	}

	protected IClasspathEntry findSuggestedEntry(IPath jarPath, IClasspathEntry[] suggestedEntries) {

		// compare jarPath to an existing entry

		if ((jarPath != null) && !CoreUtil.isNullOrEmpty(jarPath.toString()) && ListUtil.isNotEmpty(suggestedEntries)) {
			int matchLength = jarPath.segmentCount();

			for (IClasspathEntry suggestedEntry : suggestedEntries) {
				IPath suggestedPath = suggestedEntry.getPath();

				IPath path = suggestedPath.removeFirstSegments(suggestedPath.segmentCount() - matchLength);

				IPath p = path.setDevice(null);

				IPath pathToMatch = p.makeAbsolute();

				if (jarPath.equals(pathToMatch)) {
					return suggestedEntry;
				}
			}
		}

		return null;
	}

	protected IFile getPluginPackageFile() {
		IFile retval = null;

		if (pluginPackageFilePath == null) {
			retval = lookupPluginPackageFile();

			if (FileUtil.exists(retval)) {
				pluginPackageFilePath = retval.getFullPath();

				return retval;
			}
		}
		else {
			IWorkspaceRoot root = CoreUtil.getWorkspaceRoot();

			retval = root.getFile(pluginPackageFilePath);

			if (!retval.exists()) {
				pluginPackageFilePath = null;

				retval = lookupPluginPackageFile();
			}
		}

		return retval;
	}

	protected String[] getPortalDependencyJars() {
		String[] jars = new String[0];

		IFile pluginPackageFile = getPluginPackageFile();

		try {
			String deps = getPropertyValue("portal-dependency-jars", pluginPackageFile);

			String[] split = deps.split(StringPool.COMMA);

			if (ListUtil.isNotEmpty(split) && !CoreUtil.isNullOrEmpty(split[0])) {
				for (int i = 0; i < split.length; i++) {
					split[i] = split[i].trim();
				}

				return split;
			}
		}
		catch (Exception e) {
		}

		return jars;
	}

	protected abstract String[] getPortalJars();

	protected String getPropertyValue(String key, IFile propertiesFile) {
		String retval = null;

		try (InputStream inputStream = getPluginPackageFile().getContents()) {
			Properties props = new Properties();

			props.load(inputStream);

			retval = props.getProperty(key, StringPool.EMPTY);
		}
		catch (Exception e) {
		}

		return retval;
	}

	protected String[] getRequiredDeploymentContexts() {
		String[] jars = new String[0];

		IFile pluginPackageFile = getPluginPackageFile();

		try {
			String context = getPropertyValue("required-deployment-contexts", pluginPackageFile);

			String[] split = context.split(StringPool.COMMA);

			if (ListUtil.isNotEmpty(split) && !CoreUtil.isNullOrEmpty(split[0])) {
				return split;
			}
		}
		catch (Exception e) {
		}

		return jars;
	}

	protected IFile lookupPluginPackageFile() {
		IVirtualComponent component = ComponentCore.createComponent(javaProject.getProject());

		if (component == null) {
			return null;
		}

		IVirtualFolder folder = component.getRootFolder();

		IContainer resource = folder.getUnderlyingFolder();

		if (!(resource instanceof IFolder)) {
			return null;
		}

		IFolder webroot = (IFolder)resource;

		IFile pluginPackageFile = webroot.getFile(
			"WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE);

		/**
		 * IDE-226 the file may be missing because we are in an ext plugin which has a different layout
		 * check for ext-web in the path to the docroot
		 */
		String webrootFullPath = FileUtil.getFullPathPortableString(webroot);

		if (!pluginPackageFile.exists() && webrootFullPath.endsWith("WEB-INF/ext-web/docroot")) {

			// look for packages file in first docroot

			IPath fullPath = webroot.getFullPath();

			IPath path = fullPath.removeFirstSegments(1);

			IPath parentDocroot = path.removeLastSegments(3);

			IProject project = javaProject.getProject();

			IFolder parentWebroot = project.getFolder(parentDocroot);

			if (parentWebroot.exists()) {
				pluginPackageFile = parentWebroot.getFile(
					"WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE);
			}
		}

		return pluginPackageFile;
	}

	protected IClasspathAttribute newJavadocAttr(String javadocURL) {
		return JavaCore.newClasspathAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, javadocURL);
	}

	protected static final String SEPARATOR = "!";

	protected static ClasspathDecorationsManager cpDecorations;
	protected static final Collection<String> portalSourceJars = Arrays.asList(
		"util-bridges.jar", "util-java.jar", "util-taglib.jar", "portal-impl.jar");

	static {
		cpDecorations = new ClasspathDecorationsManager(LiferayServerCore.PLUGIN_ID);
	}

	protected IClasspathEntry[] classpathEntries;
	protected String javadocURL;
	protected IJavaProject javaProject;
	protected IPath path;
	protected IPath pluginPackageFilePath;
	protected IPath portalDir;
	protected IPath sourceLocation;

}