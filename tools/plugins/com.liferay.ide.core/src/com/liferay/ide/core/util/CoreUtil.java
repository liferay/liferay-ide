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

package com.liferay.ide.core.util;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.adapter.NoopLiferayProject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.security.MessageDigest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import org.osgi.framework.Version;

import org.w3c.dom.Node;

/**
 * Core Utility methods
 *
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Simon Jiang
 * @author Terry Jia
 */
public class CoreUtil {

	public static void addNaturesToProject(IProject proj, String[] natureIds, IProgressMonitor monitor)
		throws CoreException {

		IProjectDescription description = proj.getDescription();

		String[] prevNatures = description.getNatureIds();

		String[] newNatures = new String[prevNatures.length + natureIds.length];

		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

		for (int i = prevNatures.length; i < newNatures.length; i++) {
			newNatures[i] = natureIds[i - prevNatures.length];
		}

		description.setNatureIds(newNatures);

		proj.setDescription(description, monitor);
	}

	/**
	 * Compares v1 <-> v2, if v1 is greater than v2, then result will be > 0 if v2
	 * is greater than v1 the result will be < 0
	 *
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static int compareVersions(Version v1, Version v2) {
		if (v2 == v1) {

			// quicktest

			return 0;
		}

		int result = v1.getMajor() - v2.getMajor();

		if (result != 0) {
			return result;
		}

		result = v1.getMinor() - v2.getMinor();

		if (result != 0) {
			return result;
		}

		result = v1.getMicro() - v2.getMicro();

		if (result != 0) {
			return result;
		}

		String s1 = v1.getQualifier();

		return s1.compareTo(v2.getQualifier());
	}

	public static boolean containsNullElement(Object[] array) {
		if (ListUtil.isEmpty(array)) {
			return true;
		}

		for (Object object : array) {
			if (object == null) {
				return true;
			}
		}

		return false;
	}

	public static void createEmptyFile(IFile newFile) throws CoreException {
		if (newFile.getParent() instanceof IFolder) {
			prepareFolder((IFolder)newFile.getParent());
		}

		try (InputStream inputStream = new ByteArrayInputStream(new byte[0])) {
			newFile.create(inputStream, true, null);
		}
		catch (Exception e) {
			throw new CoreException(LiferayCore.createErrorStatus(e));
		}
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, LiferayCore.PLUGIN_ID, msg);
	}

	public static final String createStringDigest(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			byte[] input = str.getBytes("UTF-8");

			byte[] digest = md.digest(input);

			StringBuilder buf = new StringBuilder();

			for (byte d : digest) {
				String hex = Integer.toHexString(0xFF & d);

				if (hex.length() == 1) {
					buf.append('0');
				}

				buf.append(hex);
			}

			return buf.toString();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void deleteResource(IResource resource) throws CoreException {
		if (FileUtil.notExists(resource)) {
			return;
		}

		resource.delete(true, null);
	}

	public static boolean empty(String val) {
		return isNullOrEmpty(val);
	}

	public static IResource findResourceForLocationURI(File file) {
		IWorkspaceRoot root = getWorkspaceRoot();

		IFile[] files = root.findFilesForLocationURI(file.toURI());

		return _filterIResource(files);
	}

	public static IProject[] getAllProjects() {
		return getWorkspaceRoot().getProjects();
	}

	public static IClasspathEntry[] getClasspathEntries(IProject project) {
		if (project == null) {
			return new IClasspathEntry[0];
		}

		IJavaProject javaProject = JavaCore.create(project);

		try {
			return javaProject.getRawClasspath();
		}
		catch (JavaModelException jme) {
		}

		return new IClasspathEntry[0];
	}

	public static IProject[] getClasspathProjects(IProject project) {
		List<IProject> retval = new ArrayList<>();

		try {
			IJavaProject javaProject = JavaCore.create(project);

			IClasspathEntry[] classpathEntries = getClasspathEntries(project);

			for (IClasspathEntry classpathEntry : classpathEntries) {
				if (classpathEntry.getEntryKind() != IClasspathEntry.CPE_CONTAINER) {
					continue;
				}

				IClasspathContainer container = JavaCore.getClasspathContainer(classpathEntry.getPath(), javaProject);

				IClasspathEntry[] containerClasspathEntries = container.getClasspathEntries();

				for (IClasspathEntry containerClasspathEntry : containerClasspathEntries) {
					if (containerClasspathEntry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
						IResource member = getWorkspaceRoot().findMember(containerClasspathEntry.getPath());

						if ((member != null) && (member.getType() == IResource.PROJECT)) {
							retval.add((IProject)member);
						}
					}
				}
			}
		}
		catch (JavaModelException jme) {
		}

		return retval.toArray(new IProject[0]);
	}

	public static IFolder getDefaultDocrootFolder(IProject project) {
		IWebProject webproject = LiferayCore.create(IWebProject.class, project);

		if (webproject != null) {
			return webproject.getDefaultDocrootFolder();
		}

		return null;
	}

	public static IExtensionPoint getExtensionPoint(String namespace, String extensionPointName) {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

		return extensionRegistry.getExtensionPoint(namespace, extensionPointName);
	}

	public static IFile getIFileFromWorkspaceRoot(IPath path) {
		return getWorkspaceRoot().getFile(path);
	}

	public static Set<IProject> getJavaProjects(Set<IProject> projects) {
		Stream<IProject> stream = projects.stream();

		return stream.map(
			JavaCore::create
		).filter(
			Objects::nonNull
		).filter(
			IJavaProject::isOpen
		).map(
			IJavaProject::getProject
		).collect(
			Collectors.toSet()
		);
	}

	public static IProject getLiferayProject(IResource resource) {
		IProject project = null;

		if (FileUtil.exists(resource)) {
			project = resource.getProject();
		}

		if (FileUtil.exists(project)) {
			if (isLiferayProject(project)) {
				return project;
			}

			IProject[] projects = getAllProjects();

			for (IProject proj : projects) {
				IPath location = proj.getLocation();

				if ((location != null) && (project != null) && isLiferayProject(proj)) {
					IPath projectLocation = project.getLocation();

					if (projectLocation.isPrefixOf(location)) {
						return proj;
					}
				}
			}
		}

		return null;
	}

	public static IProject getLiferayProject(String projectName) {
		return getLiferayProject(getProject(projectName));
	}

	public static Object getNewObject(Object[] oldObjects, Object[] newObjects) {
		if ((oldObjects != null) && (newObjects != null) && (oldObjects.length < newObjects.length)) {
			for (Object newObject : newObjects) {
				boolean found = false;

				for (Object oldObject : oldObjects) {
					if (oldObject == newObject) {
						found = true;

						break;
					}
				}

				if (!found) {
					return newObject;
				}
			}
		}

		if ((oldObjects == null) && (newObjects != null) && (newObjects.length == 1)) {
			return newObjects[0];
		}

		return null;
	}

	/**
	 * try to get leaf child project that contains this file, cause a file also be
	 * contained in the parent project
	 */
	public static IProject getProject(File file) {
		IWorkspaceRoot ws = getWorkspaceRoot();

		IResource[] containers = ws.findContainersForLocationURI(file.toURI());

		IResource resource = _filterIResource(containers);

		if (resource == null) {
			return null;
		}

		return resource.getProject();
	}

	public static IProject getProject(IPath path) {
		File file = path.toFile();

		return getProject(file);
	}

	public static IProject getProject(String projectName) {
		return getWorkspaceRoot().getProject(projectName);
	}

	public static final List<IFolder> getSourceFolders(IJavaProject project) {
		if (FileUtil.notExists(project)) {
			return Collections.emptyList();
		}

		IClasspathEntry[] entries;

		try {
			entries = project.readRawClasspath();
		}
		catch (Exception e) {
			return Collections.emptyList();
		}

		List<IFolder> folders = new ArrayList<>();

		for (IClasspathEntry entry : entries) {
			if (entry.getEntryKind() != IClasspathEntry.CPE_SOURCE) {
				continue;
			}

			IPath path = entry.getPath();

			if (path.segmentCount() == 0) {
				continue;
			}

			IContainer container = null;

			if (path.segmentCount() == 1) {
				container = getProject(path.segment(0));
			}
			else {
				container = getWorkspaceRoot().getFolder(entry.getPath());
			}

			if (!folders.contains(container) && (container instanceof IFolder)) {
				folders.add((IFolder)container);
			}
		}

		return folders;
	}

	/**
	 * Gets the stack trace from a Throwable as a readable String.
	 *
	 * @return the stack trace as generated by the exception's
	 *  <code>printStackTrace(PrintWriter)</code> method.
	 */
	public static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();

		PrintWriter pw = new PrintWriter(sw, true);

		throwable.printStackTrace(pw);

		StringBuffer sb = sw.getBuffer();

		return sb.toString();
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static IWorkspaceRoot getWorkspaceRoot() {
		return getWorkspace().getRoot();
	}

	public static File getWorkspaceRootFile() {
		return getWorkspaceRootLocation().toFile();
	}

	public static IPath getWorkspaceRootLocation() {
		return getWorkspaceRoot().getLocation();
	}

	public static Object invoke(String methodName, Object object, Class<?>[] argTypes, Object[] args)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			   SecurityException {

		Class<?> clazz = object.getClass();

		Method method = clazz.getDeclaredMethod(methodName, argTypes);

		method.setAccessible(true);

		return method.invoke(object, args);
	}

	public static boolean isEqual(Object object1, Object object2) {
		if ((object1 != null) && (object2 != null) && object1.equals(object2)) {
			return true;
		}

		return false;
	}

	public static boolean isLiferayProject(IProject project) {
		if (FileUtil.notExists(project)) {
			return false;
		}

		ILiferayProject lrproject = LiferayCore.create(ILiferayProject.class, project);

		if ((lrproject != null) && !(lrproject instanceof NoopLiferayProject)) {
			return true;
		}

		return false;
	}

	public static boolean isLinux() {
		return Platform.OS_LINUX.equals(Platform.getOS());
	}

	public static boolean isMac() {
		return Platform.OS_MACOSX.equals(Platform.getOS());
	}

	public static boolean isNotNullOrEmpty(String val) {
		return !isNullOrEmpty(val);
	}

	public static boolean isNullOrEmpty(String val) {
		if ((val == null) || val.equals(StringPool.EMPTY)) {
			return true;
		}

		String s = val.trim();

		if (s.equals(StringPool.EMPTY)) {
			return true;
		}

		return false;
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		}
		catch (NumberFormatException nfe) {
			return false;
		}

		return true;
	}

	public static boolean isWindows() {
		return Platform.OS_WIN32.equals(Platform.getOS());
	}

	public static void makeFolders(IFolder folder) throws CoreException {
		if (folder == null) {
			return;
		}

		IContainer parent = folder.getParent();

		if (parent instanceof IFolder) {
			makeFolders((IFolder)parent);
		}

		if (!folder.exists()) {
			folder.create(true, true, null);
		}
	}

	public static IProgressMonitor newSubmonitor(IProgressMonitor parent, int ticks) {
		if (parent == null) {
			return null;
		}

		return SubMonitor.convert(parent, ticks);
	}

	public static IProject openProject(String projectName, IPath dir, IProgressMonitor monitor) throws CoreException {
		IWorkspace workspace = getWorkspace();

		IProject project = getProject(projectName);

		IProjectDescription desc = workspace.newProjectDescription(project.getName());

		desc.setLocation(dir);

		project.create(desc, monitor);

		project.open(monitor);

		return project;
	}

	public static Version parseVersion(String versionString) {
		return Version.parseVersion(versionString.replaceAll("q|-lts", ""));
	}

	public static void prepareFolder(IFolder folder) throws CoreException {
		IContainer parent = folder.getParent();

		if (parent instanceof IFolder) {
			prepareFolder((IFolder)parent);
		}

		if (!folder.exists()) {
			folder.create(IResource.FORCE, true, null);
		}
	}

	public static String readPropertyFileValue(File propertiesFile, String key) throws IOException {
		try (FileInputStream fis = new FileInputStream(propertiesFile)) {
			Properties props = new Properties();

			props.load(fis);

			return props.getProperty(key);
		}
	}

	public static String readStreamToString(InputStream contents) throws IOException {
		return readStreamToString(contents, true);
	}

	public static String readStreamToString(InputStream contents, boolean closeStream) throws IOException {
		if (contents == null) {
			return null;
		}

		char[] buffer = new char[0x10000];

		StringBuilder out = new StringBuilder();

		try (Reader in = new InputStreamReader(contents, "UTF-8")) {
			int read;

			do {
				read = in.read(buffer, 0, buffer.length);

				if (read > 0) {
					out.append(buffer, 0, read);
				}
			}
			while (read >= 0);
		}

		if (closeStream) {
			contents.close();
		}

		return out.toString();
	}

	public static Version readVersionFile(File versionInfoFile) {
		String versionContents = FileUtil.readContents(versionInfoFile);

		if (isNullOrEmpty(versionContents)) {
			return Version.emptyVersion;
		}

		Version version = null;

		try {
			version = Version.parseVersion(versionContents.trim());
		}
		catch (NumberFormatException nfe) {
			version = Version.emptyVersion;
		}

		return version;
	}

	public static void removeChildren(Node node) {
		if (node == null) {
			return;
		}

		while (node.hasChildNodes()) {
			node.removeChild(node.getFirstChild());
		}
	}

	public static IStatus validateName(String segment, int typeMask) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		return workspace.validateName(segment, typeMask);
	}

	public static void writeStreamFromString(String contents, OutputStream outputStream) throws IOException {
		if (contents == null) {
			return;
		}

		char[] buffer = new char[0x10000];

		try (InputStream inputStream = new ByteArrayInputStream(contents.getBytes("UTF-8"));
			Reader in = new InputStreamReader(inputStream);
			Writer out = new OutputStreamWriter(outputStream, "UTF-8")) {

			int read;

			do {
				read = in.read(buffer, 0, buffer.length);

				if (read > 0) {
					out.write(buffer, 0, read);
				}
			}
			while (read >= 0);
		}
	}

	private static IResource _filterIResource(IResource[] resources) {
		IResource result = null;

		for (IResource resource : resources) {
			if (result == null) {
				result = resource;
			}
			else {
				IPath filePath = resource.getProjectRelativePath();
				IPath resourcePath = result.getProjectRelativePath();

				if (filePath.segmentCount() < resourcePath.segmentCount()) {
					result = resource;
				}
			}
		}

		if (result == null) {
			return null;
		}

		return result;
	}

}