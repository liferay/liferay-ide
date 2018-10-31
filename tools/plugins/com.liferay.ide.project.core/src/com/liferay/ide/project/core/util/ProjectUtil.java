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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.PluginClasspathContainerInitializer;
import com.liferay.ide.project.core.PluginsSDKBundleProject;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.SDKClasspathContainer;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.core.facet.PluginFacetProjectCreationDataModelProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.jst.j2ee.classpathdep.IClasspathDependencyConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetInstallDataModelProperties;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FacetDataModelMap;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFrameworkException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class ProjectUtil {

	public static final String METADATA_FOLDER = ".metadata";

	public static void collectProjectsFromDirectory(List<IProject> result, File location) {
		File[] children = location.listFiles();

		if (children == null) {
			return;
		}

		for (File child : children) {
			if (child.isFile() && IProjectDescription.DESCRIPTION_FILE_NAME.equals(child.getName())) {
				IWorkspace workspace = CoreUtil.getWorkspace();
				IProjectDescription projectDescription;

				try {
					projectDescription = workspace.loadProjectDescription(new Path(child.getAbsolutePath()));

					IProject project = CoreUtil.getProject(projectDescription.getName());

					if (FileUtil.exists(project)) {
						result.add(project);
					}
				}
				catch (CoreException ce) {
					ProjectCore.logError("loadProjectDescription error", ce);
				}
			}
			else {
				collectProjectsFromDirectory(result, child);
			}
		}
	}

	public static boolean collectSDKProjectsFromDirectory(
		Collection<File> eclipseProjectFiles, Collection<File> liferayProjectDirs, File directory,
		Set<String> directoriesVisited, boolean recurse, IProgressMonitor monitor) {

		if (monitor.isCanceled()) {
			return false;
		}

		monitor.subTask(NLS.bind(Msgs.checking, directory.getPath()));

		File[] contents = directory.listFiles();

		if (contents == null) {
			return false;
		}

		// Initialize recursion guard for recursive symbolic links

		if (directoriesVisited == null) {
			directoriesVisited = new HashSet<>();

			try {
				directoriesVisited.add(directory.getCanonicalPath());
			}
			catch (IOException ioe) {
				ProjectCore.logError(ioe.getLocalizedMessage(), ioe);
			}
		}

		// first look for project description files

		String dotProject = IProjectDescription.DESCRIPTION_FILE_NAME;

		for (File file : contents) {
			if (isLiferaySDKProjectDir(file)) {

				// recurse to see if it has project file

				int currentSize = eclipseProjectFiles.size();

				collectSDKProjectsFromDirectory(
					eclipseProjectFiles, liferayProjectDirs, file, directoriesVisited, false, monitor);

				int newSize = eclipseProjectFiles.size();

				if (newSize == currentSize) {
					liferayProjectDirs.add(file);
				}
			}
			else if (file.isFile() && dotProject.equals(file.getName())) {
				if (!eclipseProjectFiles.contains(file) && isLiferaySDKProjectDir(file.getParentFile())) {
					eclipseProjectFiles.add(file);

					// don't search sub-directories since we can't have nested projects

					return true;
				}
			}
		}

		// no project description found, so recurse into sub-directories

		for (File file : contents) {
			if (file.isDirectory()) {
				if (!METADATA_FOLDER.equals(file.getName())) {
					try {
						String canonicalPath = file.getCanonicalPath();

						if (!directoriesVisited.add(canonicalPath)) {

							// already been here --> do not recurse

							continue;
						}
					}
					catch (IOException ioe) {
						ProjectCore.logError(ioe.getLocalizedMessage(), ioe);
					}

					// dont recurse directories that we have already determined are Liferay projects

					if (!liferayProjectDirs.contains(file) && recurse) {
						collectSDKProjectsFromDirectory(
							eclipseProjectFiles, liferayProjectDirs, file, directoriesVisited, recurse, monitor);
					}
				}
			}
		}

		return true;
	}

	public static String convertToDisplayName(String name) {
		if (CoreUtil.isNullOrEmpty(name)) {
			return StringPool.EMPTY;
		}

		String displayName = removePluginSuffix(name);

		displayName = displayName.replaceAll("-", " ");

		displayName = displayName.replaceAll("_", " ");

		displayName = WordUtils.capitalize(displayName);

		return displayName;
	}

	public static void createDefaultWebXml(File webxmlFile, String expectedContainingProjectName) {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<web-app id=\"WebApp_ID\" ");
		sb.append("version=\"2.5\" ");
		sb.append("xmlns=\"http://java.sun.com/xml/ns/javaee\" ");
		sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		sb.append("xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee ");
		sb.append("http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\">\n");
		sb.append("</web-app>");

		try {
			FileUtil.writeFile(webxmlFile, sb.toString(), expectedContainingProjectName);
		}
		catch (Exception e) {
			ProjectCore.logError("Unable to create default web xml", e);
		}
	}

	public static IFile createEmptyProjectFile(String fileName, IFolder folder) throws CoreException {
		IFile emptyFile = folder.getFile(fileName);

		if (FileUtil.exists(emptyFile)) {
			return emptyFile;
		}

		try (InputStream inputStream = new ByteArrayInputStream(StringPool.EMPTY.getBytes())) {
			emptyFile.create(inputStream, true, null);
		}
		catch (IOException ioe) {
			throw new CoreException(ProjectCore.createErrorStatus(ioe));
		}

		return emptyFile;
	}

	public static IProject createExistingProject(ProjectRecord record, IPath sdkLocation, IProgressMonitor monitor)
		throws CoreException {

		String projectName = record.getProjectName();

		IWorkspace workspace = CoreUtil.getWorkspace();

		IProject project = CoreUtil.getProject(projectName);

		if (record.description == null) {

			// error case

			record.description = workspace.newProjectDescription(projectName);
			IPath locationPath = new Path(record.projectSystemFile.getAbsolutePath());

			// If it is under the root use the default location

			IPath platformLocation = Platform.getLocation();

			if (platformLocation.isPrefixOf(locationPath)) {
				record.description.setLocation(null);
			}
			else {
				record.description.setLocation(locationPath);
			}
		}
		else {
			record.description.setName(projectName);
		}

		project.create(record.description, CoreUtil.newSubmonitor(monitor, 30));

		project.open(IResource.FORCE, CoreUtil.newSubmonitor(monitor, 70));

		// need to check to see if we an ext project with source folders with incorrect
		// parent attributes

		if (StringUtil.endsWith(project.getName(), ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX)) {
			_fixExtProjectClasspathEntries(project);
		}

		IFacetedProject fProject = ProjectFacetsManager.create(project, true, monitor);

		FacetedProjectWorkingCopy fpwc = new FacetedProjectWorkingCopy(fProject);

		String pluginType = guessPluginType(fpwc);

		SDKPluginFacetUtil.configureProjectAsSDKProject(fpwc, pluginType, sdkLocation.toPortableString(), record);

		fpwc.commitChanges(monitor);

		IJavaProject javaProject = JavaCore.create(fProject.getProject());

		workspace.run(
			new IWorkspaceRunnable() {

				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					List<IClasspathEntry> rawClasspaths = new ArrayList<>();

					IPath containerPath = null;

					for (IClasspathEntry entry : javaProject.getRawClasspath()) {
						IPath path = entry.getPath();

						String segment = path.segment(0);

						if ((entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) &&
							segment.equals(SDKClasspathContainer.ID)) {

							containerPath = entry.getPath();

							break;
						}

						if (!_isLiferayRuntimePluginClassPath(entry)) {
							rawClasspaths.add(entry);
						}
					}

					if (containerPath != null) {
						ClasspathContainerInitializer initializer = JavaCore.getClasspathContainerInitializer(
							SDKClasspathContainer.ID);

						initializer.initialize(containerPath, javaProject);
					}
					else {
						javaProject.setRawClasspath(
							rawClasspaths.toArray(new IClasspathEntry[rawClasspaths.size()]),
							new NullProgressMonitor());

						javaProject.setRawClasspath(
							rawClasspaths.toArray(new IClasspathEntry[rawClasspaths.size()]),
							new NullProgressMonitor());

						IAccessRule[] accessRules = {};

						IClasspathAttribute[] attributes = {
							JavaCore.newClasspathAttribute(
								IClasspathDependencyConstants.CLASSPATH_COMPONENT_NON_DEPENDENCY, StringPool.EMPTY)
						};

						IPath cpePath = new Path(SDKClasspathContainer.ID);

						IClasspathEntry newEntry = JavaCore.newContainerEntry(cpePath, accessRules, attributes, false);

						IClasspathEntry[] entries = javaProject.getRawClasspath();

						for (IClasspathEntry entry : entries) {
							if (cpePath.equals(entry.getPath())) {
								return;
							}
						}

						IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];

						System.arraycopy(entries, 0, newEntries, 0, entries.length);

						newEntries[entries.length] = newEntry;

						javaProject.setRawClasspath(newEntries, monitor);
					}

					monitor.done();

					SDK sdk = SDKUtil.createSDKFromLocation(sdkLocation);

					SDKUtil.openAsProject(sdk);
				}

			},
			monitor);

		return project;
	}

	public static IProject createExistingProject(
			ProjectRecord record, IRuntime runtime, String sdkLocation, IProgressMonitor monitor)
		throws CoreException {

		String projectName = record.getProjectName();

		IWorkspace workspace = CoreUtil.getWorkspace();

		IProject project = CoreUtil.getProject(projectName);

		if (record.description == null) {

			// error case

			record.description = workspace.newProjectDescription(projectName);

			IPath locationPath = new Path(record.projectSystemFile.getAbsolutePath());

			// If it is under the root use the default location

			IPath platformLocation = Platform.getLocation();

			if (platformLocation.isPrefixOf(locationPath)) {
				record.description.setLocation(null);
			}
			else {
				record.description.setLocation(locationPath);
			}
		}
		else {
			record.description.setName(projectName);
		}

		monitor.beginTask(Msgs.importingProject, 100);

		project.create(record.description, CoreUtil.newSubmonitor(monitor, 30));

		project.open(IResource.FORCE, CoreUtil.newSubmonitor(monitor, 70));

		// need to check to see if we an ext project with source folders with incorrect parent attributes

		if (StringUtil.endsWith(project.getName(), ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX)) {
			_fixExtProjectClasspathEntries(project);
		}

		IFacetedProject fProject = ProjectFacetsManager.create(project, true, monitor);

		FacetedProjectWorkingCopy fpwc = new FacetedProjectWorkingCopy(fProject);

		String pluginType = guessPluginType(fpwc);

		SDKPluginFacetUtil.configureProjectAsRuntimeProject(fpwc, runtime, pluginType, sdkLocation, record);

		fpwc.commitChanges(monitor);

		IJavaProject javaProject = JavaCore.create(fProject.getProject());

		workspace.run(
			new IWorkspaceRunnable() {

				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					for (IClasspathEntry entry : javaProject.getRawClasspath()) {
						IPath path = entry.getPath();

						String segment = path.segment(0);

						if ((entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) &&
							segment.equals(PluginClasspathContainerInitializer.ID)) {

							ClasspathContainerInitializer initializer = JavaCore.getClasspathContainerInitializer(
								PluginClasspathContainerInitializer.ID);

							initializer.initialize(entry.getPath(), javaProject);

							break;
						}
					}

					monitor.done();
				}

			},
			monitor);

		return project;
	}

	public static IProject createNewSDKProject(
			ProjectRecord projectRecord, IPath sdkLocation, IProgressMonitor monitor, NewLiferayPluginProjectOp op)
		throws CoreException {

		IDataModel newProjectDataModel = DataModelFactory.createDataModel(
			new PluginFacetProjectCreationDataModelProvider());

		// we are importing so set flag to not create anything

		newProjectDataModel.setBooleanProperty(IPluginProjectDataModelProperties.CREATE_PROJECT_OPERATION, false);

		IDataModel nestedModel = newProjectDataModel.getNestedModel(
			IPluginProjectDataModelProperties.NESTED_PROJECT_DM);

		// using sdk location

		nestedModel.setBooleanProperty(IPluginProjectDataModelProperties.USE_DEFAULT_LOCATION, true);

		newProjectDataModel.setBooleanProperty(IPluginProjectDataModelProperties.LIFERAY_USE_SDK_LOCATION, false);

		newProjectDataModel.setBooleanProperty(IPluginProjectDataModelProperties.LIFERAY_USE_WORKSPACE_LOCATION, true);

		setGenerateDD(newProjectDataModel, false);

		IPath projectLocation = projectRecord.getProjectLocation();

		IPath webXmlPath = projectLocation.append(ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/web.xml");

		File webXmlFile = webXmlPath.toFile();

		if (StringUtil.endsWith(projectRecord.getProjectName(), ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX)) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_PORTLET, true);

			if (FileUtil.notExists(webXmlFile)) {
				createDefaultWebXml(webXmlFile, projectRecord.getProjectName());
			}
		}
		else if (StringUtil.endsWith(projectRecord.getProjectName(), ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX)) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_HOOK, true);

			if (FileUtil.notExists(webXmlFile)) {
				createDefaultWebXml(webXmlFile, projectRecord.getProjectName());
			}
		}
		else if (StringUtil.endsWith(projectRecord.getProjectName(), ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX)) {
			Path path = new Path(ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-web/docroot/WEB-INF/web.xml");

			webXmlPath = webXmlPath.removeLastSegments(3);

			webXmlPath = webXmlPath.append(path);

			webXmlFile = webXmlPath.toFile();

			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_EXT, true);

			if (FileUtil.notExists(webXmlFile)) {
				createDefaultWebXml(webXmlFile, projectRecord.getProjectName());
			}
		}
		else if (StringUtil.endsWith(projectRecord.getProjectName(), ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX)) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_LAYOUTTPL, true);
		}
		else if (StringUtil.endsWith(projectRecord.getProjectName(), ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX)) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_THEME, true);
		}
		else if (StringUtil.endsWith(projectRecord.getProjectName(), ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX)) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_WEB, true);
		}

		IFacetedProjectWorkingCopy fpwc = (IFacetedProjectWorkingCopy)newProjectDataModel.getProperty(
			IFacetProjectCreationDataModelProperties.FACETED_PROJECT_WORKING_COPY);

		fpwc.setProjectName(projectRecord.getProjectName());

		String projectDirName = projectLocation.lastSegment();

		// for now always set a project location (so it can be used by facet install methods) may be unset later

		fpwc.setProjectLocation(projectRecord.getProjectLocation());

		String pluginType = null;

		if (op != null) {
			PluginType type = SapphireUtil.getContent(op.getPluginType());

			pluginType = type.name();

			if (pluginType.equals(PluginType.theme.name())) {
				newProjectDataModel.setProperty(
					IPluginProjectDataModelProperties.THEME_PARENT, SapphireUtil.getContent(op.getThemeParent()));

				newProjectDataModel.setProperty(
					IPluginProjectDataModelProperties.THEME_TEMPLATE_FRAMEWORK,
					SapphireUtil.getContent(op.getThemeFramework()));
			}
		}
		else {
			pluginType = guessPluginType(fpwc);
		}

		SDKPluginFacetUtil.configureProjectAsSDKProject(
			fpwc, pluginType, sdkLocation.toPortableString(), projectRecord);

		if ((op != null) && pluginType.equals(PluginType.portlet.name())) {
			IPortletFramework portletFramework = SapphireUtil.getContent(op.getPortletFramework());

			portletFramework.configureNewProject(newProjectDataModel, fpwc);
		}

		// if project is located in natural workspace location then don't need to set a project location

		IPath rootLocation = CoreUtil.getWorkspaceRootLocation();

		if (projectLocation.equals(rootLocation.append(projectDirName))) {
			fpwc.setProjectLocation(null);
		}

		try {
			fpwc.commitChanges(monitor);
		}
		catch (FacetedProjectFrameworkException fpfe) {
			ProjectCore.logError(fpfe);
		}

		IWorkspace workspace = CoreUtil.getWorkspace();

		workspace.run(
			new IWorkspaceRunnable() {

				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					SDK sdk = SDKUtil.createSDKFromLocation(sdkLocation);

					SDKUtil.openAsProject(sdk);
				}

			},
			monitor);

		return fpwc.getProject();
	}

	public static IProject createNewSDKProject(
			ProjectRecord projectRecord, IRuntime runtime, String sdkLocation, NewLiferayPluginProjectOp op,
			IProgressMonitor monitor)
		throws CoreException {

		IDataModel newProjectDataModel = DataModelFactory.createDataModel(
			new PluginFacetProjectCreationDataModelProvider());

		// we are importing so set flag to not create anything

		newProjectDataModel.setBooleanProperty(IPluginProjectDataModelProperties.CREATE_PROJECT_OPERATION, false);

		IDataModel nestedModel = newProjectDataModel.getNestedModel(
			IPluginProjectDataModelProperties.NESTED_PROJECT_DM);

		if (op != null) {
			if (SapphireUtil.getContent(op.getUseDefaultLocation())) {

				// using Eclipse workspace location

				nestedModel.setBooleanProperty(IPluginProjectDataModelProperties.USE_DEFAULT_LOCATION, true);

				newProjectDataModel.setBooleanProperty(
					IPluginProjectDataModelProperties.LIFERAY_USE_SDK_LOCATION, false);

				newProjectDataModel.setBooleanProperty(
					IPluginProjectDataModelProperties.LIFERAY_USE_WORKSPACE_LOCATION, true);
			}
			else {
				nestedModel.setBooleanProperty(IPluginProjectDataModelProperties.USE_DEFAULT_LOCATION, false);

				org.eclipse.sapphire.modeling.Path path = SapphireUtil.getContent(op.getLocation());

				nestedModel.setStringProperty(
					IPluginProjectDataModelProperties.USER_DEFINED_LOCATION, path.toOSString());

				if (!SapphireUtil.getContent(op.getUseDefaultLocation())) {
					newProjectDataModel.setBooleanProperty(
						IPluginProjectDataModelProperties.LIFERAY_USE_CUSTOM_LOCATION, true);
				}
			}
		}

		setGenerateDD(newProjectDataModel, false);

		IPath location = projectRecord.getProjectLocation();

		IPath webXmlPath = location.append(ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/web.xml");

		String projectName = projectRecord.getProjectName();

		File webXmlFile = webXmlPath.toFile();

		if (projectName.endsWith(ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX)) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_PORTLET, true);

			if (!webXmlFile.exists()) {
				createDefaultWebXml(webXmlFile, projectRecord.getProjectName());
			}
		}
		else if (projectName.endsWith(ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX)) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_HOOK, true);

			if (!webXmlFile.exists()) {
				createDefaultWebXml(webXmlFile, projectName);
			}
		}
		else if (projectName.endsWith(ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX)) {
			Path webPath = new Path(ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-web/docroot/WEB-INF/web.xml");

			webXmlPath = webXmlPath.removeLastSegments(3);

			webXmlPath = webXmlPath.append(webPath);

			webXmlFile = webXmlPath.toFile();

			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_EXT, true);

			if (!webXmlFile.exists()) {
				createDefaultWebXml(webXmlPath.toFile(), projectName);
			}
		}
		else if (projectName.endsWith(ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX)) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_LAYOUTTPL, true);
		}
		else if (projectName.endsWith(ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX)) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_THEME, true);
		}
		else if (projectName.endsWith(ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX)) {
			newProjectDataModel.setProperty(IPluginProjectDataModelProperties.PLUGIN_TYPE_WEB, true);
		}

		IFacetedProjectWorkingCopy fpwc = (IFacetedProjectWorkingCopy)newProjectDataModel.getProperty(
			IFacetProjectCreationDataModelProperties.FACETED_PROJECT_WORKING_COPY);

		fpwc.setProjectName(projectName);

		IPath projectLocation = projectRecord.getProjectLocation();

		String projectDirName = projectLocation.lastSegment();

		// for now always set a project location (so it can be used by facet install methods) may be unset later

		fpwc.setProjectLocation(projectRecord.getProjectLocation());

		String pluginType = null;

		if (op != null) {
			PluginType type = SapphireUtil.getContent(op.getPluginType());

			pluginType = type.name();

			if (pluginType.equals(PluginType.theme.name())) {
				newProjectDataModel.setProperty(
					IPluginProjectDataModelProperties.THEME_PARENT, SapphireUtil.getContent(op.getThemeParent()));

				newProjectDataModel.setProperty(
					IPluginProjectDataModelProperties.THEME_TEMPLATE_FRAMEWORK,
					SapphireUtil.getContent(op.getThemeFramework()));
			}
		}
		else {
			pluginType = guessPluginType(fpwc);
		}

		SDKPluginFacetUtil.configureProjectAsRuntimeProject(fpwc, runtime, pluginType, sdkLocation, projectRecord);

		String portlet = PluginType.portlet.name();

		if ((op != null) && portlet.equals(pluginType)) {
			IPortletFramework portletFramework = SapphireUtil.getContent(op.getPortletFramework());

			portletFramework.configureNewProject(newProjectDataModel, fpwc);
		}

		// if project is located in natural workspace location then don't need to set a project location

		IPath workspaceLocation = CoreUtil.getWorkspaceRootLocation();

		if (projectLocation.equals(workspaceLocation.append(projectDirName))) {
			fpwc.setProjectLocation(null);
		}

		fpwc.commitChanges(monitor);

		return fpwc.getProject();
	}

	public static void fixExtProjectSrcFolderLinks(IProject extProject) throws JavaModelException {
		if (extProject == null) {
			return;
		}

		IJavaProject javaProject = JavaCore.create(extProject);

		if (javaProject == null) {
			return;
		}

		IVirtualComponent c = ComponentCore.createComponent(extProject, false);

		if (c == null) {
			return;
		}

		IVirtualFolder rootFolder = c.getRootFolder();

		IVirtualFolder jsrc = rootFolder.getFolder("/WEB-INF/classes");

		if (jsrc == null) {
			return;
		}

		IClasspathEntry[] cp = javaProject.getRawClasspath();

		for (IClasspathEntry cpe : cp) {
			if (cpe.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				IPath path = cpe.getPath();

				path = path.removeFirstSegments(1);

				if (path.segmentCount() > 0) {
					try {
						IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

						IFolder srcFolder = workspaceRoot.getFolder(cpe.getPath());

						IVirtualResource[] virtualResource = ComponentCore.createResources(srcFolder);

						// create link for source folder only when it is not mapped

						if (virtualResource.length == 0) {
							IPath p = cpe.getPath();

							jsrc.createLink(p.removeFirstSegments(1), 0, null);
						}
					}
					catch (Exception e) {
						ProjectCore.logError(e);
					}
				}
			}
		}
	}

	public static IProject[] getAllPluginsSDKProjects() {
		List<IProject> sdkProjects = new ArrayList<>();

		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (isLiferayFacetedProject(project)) {
				if (SDKUtil.isSDKProject(project)) {
					sdkProjects.add(project);
				}
			}
		}

		return sdkProjects.toArray(new IProject[sdkProjects.size()]);
	}

	public static String getBundleSymbolicNameFromBND(IProject project) {
		String retVal = null;

		IFile bndFile = project.getFile("bnd.bnd");

		if (bndFile.exists()) {
			File file = FileUtil.getFile(bndFile);

			Properties prop = PropertiesUtil.loadProperties(file);

			retVal = prop.getProperty(Constants.BUNDLE_SYMBOLICNAME);
		}

		return retVal;
	}

	public static IFacetedProject getFacetedProject(IProject project) {
		try {
			return ProjectFacetsManager.create(project);
		}
		catch (CoreException ce) {
			return null;
		}
	}

	public static Set<IProjectFacetVersion> getFacetsForPreset(String presetId) {
		IPreset preset = ProjectFacetsManager.getPreset(presetId);

		return preset.getProjectFacets();
	}

	// IDE-270

	public static IProjectFacet getLiferayFacet(IFacetedProject facetedProject) {
		for (IProjectFacetVersion projectFacet : facetedProject.getProjectFacets()) {
			if (isLiferayFacet(projectFacet.getProjectFacet())) {
				return projectFacet.getProjectFacet();
			}
		}

		return null;
	}

	public static String getLiferayPluginType(String projectLocation) {
		if (isLiferaySDKProjectDir(new File(projectLocation))) {
			String suffix = StringPool.EMPTY;

			if (projectLocation.endsWith(ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX)) {
				suffix = ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
			}
			else if (projectLocation.endsWith(ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX)) {
				suffix = ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;
			}
			else if (projectLocation.endsWith(ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX)) {
				suffix = ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
			}
			else if (projectLocation.endsWith(ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX)) {
				suffix = ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
			}
			else if (projectLocation.endsWith(ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX)) {
				suffix = ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;
			}
			else if (projectLocation.endsWith(ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX)) {
				suffix = ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX;
			}

			return suffix.replace("-", StringPool.EMPTY);
		}

		return null;
	}

	public static IFile getPortletXmlFile(IProject project) {
		if ((project == null) || !isLiferayFacetedProject(project)) {
			return null;
		}

		IWebProject webProject = LiferayCore.create(IWebProject.class, project);

		if (webProject == null) {
			return null;
		}

		IFolder defaultDocrootFolder = webProject.getDefaultDocrootFolder();

		if (defaultDocrootFolder == null) {
			return null;
		}

		IFile portletXml = defaultDocrootFolder.getFile(new Path("WEB-INF/portlet.xml"));

		if (FileUtil.exists(portletXml)) {
			return portletXml;
		}

		return null;
	}

	public static IProject getProject(IDataModel model) {
		if (model == null) {
			return null;
		}

		String projectName = model.getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);

		return CoreUtil.getProject(projectName);
	}

	public static IProject getProject(String projectName) {
		return CoreUtil.getProject(projectName);
	}

	public static ProjectRecord getProjectRecordForDir(String dir) {
		ProjectRecord projectRecord = null;
		File projectDir = new File(dir);

		if (isLiferaySDKProjectDir(projectDir)) {

			// determine if this is a previous eclipse project or vanilla

			String[] files = projectDir.list();

			for (String file : files) {
				if (IProjectDescription.DESCRIPTION_FILE_NAME.equals(file)) {
					projectRecord = new ProjectRecord(new File(projectDir, file));
				}
			}

			if (projectRecord == null) {
				projectRecord = new ProjectRecord(projectDir);
			}
		}

		return projectRecord;
	}

	public static String getRelativePathFromDocroot(IWebProject lrproject, String path) {
		IFolder docroot = lrproject.getDefaultDocrootFolder();

		IPath pathValue = new Path(path);

		IPath relativePath = pathValue.makeRelativeTo(docroot.getFullPath());

		String retval = relativePath.toPortableString();

		if (retval.startsWith("/")) {
			return retval;
		}

		return "/" + retval;
	}

	public static String getRequiredSuffix(IProject project) {
		String requiredSuffix = null;

		if (isPortletProject(project)) {
			requiredSuffix = ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
		}
		else if (isHookProject(project)) {
			requiredSuffix = ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;
		}
		else if (isExtProject(project)) {
			requiredSuffix = ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
		}
		else if (isLayoutTplProject(project)) {
			requiredSuffix = ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
		}
		else if (isThemeProject(project)) {
			requiredSuffix = ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;
		}

		return requiredSuffix;
	}

	public static String guessPluginType(IFacetedProjectWorkingCopy fpwc) {
		String pluginType = null;

		String projName = fpwc.getProjectName();
		IPath location = fpwc.getProjectLocation();

		String directoryName = StringPool.EMPTY;

		if (location != null) {
			directoryName = location.lastSegment();
		}

		if (projName.endsWith(ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX) ||
			directoryName.endsWith(ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX)) {

			pluginType = "portlet";
		}
		else if (projName.endsWith(ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX) ||
				 directoryName.endsWith(ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX)) {

			pluginType = "hook";
		}
		else if (projName.endsWith(ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX) ||
				 directoryName.endsWith(ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX)) {

			pluginType = "ext";
		}
		else if (projName.endsWith(ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX) ||
				 directoryName.endsWith(ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX)) {

			pluginType = "layouttpl";
		}
		else if (projName.endsWith(ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX) ||
				 directoryName.endsWith(ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX)) {

			pluginType = "theme";
		}
		else if (projName.endsWith(ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX) ||
				 directoryName.endsWith(ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX)) {

			pluginType = "web";
		}

		return pluginType;
	}

	public static boolean hasFacet(IProject project, IProjectFacet checkProjectFacet) {
		boolean retval = false;

		if ((project == null) || (checkProjectFacet == null)) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);

			if ((facetedProject != null) && (checkProjectFacet != null)) {
				for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
					IProjectFacet projectFacet = facet.getProjectFacet();

					if (checkProjectFacet.equals(projectFacet)) {
						retval = true;

						break;
					}
				}
			}
		}
		catch (CoreException ce) {
		}

		return retval;
	}

	public static boolean hasFacet(IProject project, String facetId) {
		return hasFacet(project, ProjectFacetsManager.getProjectFacet(facetId));
	}

	public static boolean hasProperty(IDataModel model, String propertyName) {
		boolean retval = false;

		if ((model == null) || CoreUtil.isNullOrEmpty(propertyName)) {
			return retval;
		}

		for (Object property : model.getAllProperties()) {
			if (propertyName.equals(property)) {
				retval = true;

				break;
			}
		}

		return retval;
	}

	public static boolean is7xServerDeployableProject(IProject project) {
		ILiferayProject liferayProject = LiferayCore.create(project);

		if (liferayProject instanceof IBundleProject) {
			if (liferayProject instanceof PluginsSDKBundleProject) {
				PluginsSDKBundleProject sdkProject = (PluginsSDKBundleProject)liferayProject;

				SDK sdk = sdkProject.getSDK();

				if (sdk != null) {
					IProject p = sdkProject.getProject();

					IPath location = p.getLocation();

					String projectType = getLiferayPluginType(location.toPortableString());

					Version version = new Version(sdk.getVersion());
					Version sdk70 = ILiferayConstants.V700;

					if ((CoreUtil.compareVersions(version, sdk70) >= 0) &&
						!ISDKConstants.EXT_PLUGIN_PROJECT_FOLDER.equals(projectType)) {

						// sdk 7.x proejct and not ext project

						return true;
					}
					else {

						// sdk 6.x project

						return false;
					}
				}
				else {
					return false;
				}
			}
			else {

				// not sdk project

				return true;
			}
		}
		else {
			return false;
		}
	}

	public static boolean isDynamicWebFacet(IProjectFacet facet) {
		if ((facet != null) && IModuleConstants.JST_WEB_MODULE.equals(facet.getId())) {
			return true;
		}

		return false;
	}

	public static boolean isDynamicWebFacet(IProjectFacetVersion facetVersion) {
		if ((facetVersion != null) && isDynamicWebFacet(facetVersion.getProjectFacet())) {
			return true;
		}

		return false;
	}

	public static boolean isExtProject(IProject project) {
		return hasFacet(project, IPluginFacetConstants.LIFERAY_EXT_PROJECT_FACET);
	}

	public static boolean isFacetedGradleBundleProject(IProject project) {
		if (isWorkspaceWars(project) || _checkGradleThemePlugin(project) || _checkGradleWarPlugin(project)) {
			return true;
		}

		return false;
	}

	public static boolean isFragmentProject(Object resource) throws Exception {
		IProject project = null;

		if (resource instanceof IFile) {
			project = ((IFile)resource).getProject();
		}
		else if (resource instanceof IProject) {
			project = (IProject)resource;
		}

		IFile bndfile = project.getFile("bnd.bnd");

		if (bndfile.exists()) {
			try (InputStream inputStream = bndfile.getContents();
				InputStreamReader inputReader = new InputStreamReader(inputStream);
				BufferedReader reader = new BufferedReader(inputReader)) {

				String fragName;

				while ((fragName = reader.readLine()) != null) {
					if (fragName.contains("Fragment-Host:")) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean isGradleProject(IProject project) {
		boolean retval = false;

		try {
			retval = FileUtil.exists(project) && project.hasNature("org.eclipse.buildship.core.gradleprojectnature");
		}
		catch (Exception e) {
		}

		return retval;
	}

	public static boolean isHookProject(IProject project) {
		return hasFacet(project, IPluginFacetConstants.LIFERAY_HOOK_PROJECT_FACET);
	}

	public static boolean isJavaFacet(IProjectFacet facet) {
		if (facet == null) {
			return false;
		}

		if (JavaFacet.ID.equals(facet.getId()) || IModuleConstants.JST_JAVA.equals(facet.getId())) {
			return true;
		}

		return false;
	}

	public static boolean isJavaFacet(IProjectFacetVersion facetVersion) {
		if (facetVersion == null) {
			return false;
		}

		if (isJavaFacet(facetVersion.getProjectFacet())) {
			return true;
		}

		return false;
	}

	public static boolean isLayoutTplProject(IProject project) {
		return hasFacet(project, IPluginFacetConstants.LIFERAY_LAYOUTTPL_FACET_ID);
	}

	public static boolean isLiferayFacet(IProjectFacet projectFacet) {
		if ((projectFacet != null) && StringUtil.startsWith(projectFacet.getId(), "liferay.")) {
			return true;
		}

		return false;
	}

	public static boolean isLiferayFacet(IProjectFacetVersion projectFacetVersion) {
		if ((projectFacetVersion != null) && isLiferayFacet(projectFacetVersion.getProjectFacet())) {
			return true;
		}

		return false;
	}

	public static boolean isLiferayFacetedProject(IProject project) {
		boolean retval = false;

		if (project == null) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);

			if (facetedProject != null) {
				for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
					IProjectFacet projectFacet = facet.getProjectFacet();

					if (isLiferayFacet(projectFacet)) {
						retval = true;

						break;
					}
				}
			}
		}
		catch (Exception e) {
		}

		return retval;
	}

	public static boolean isLiferayPluginType(String type) {
		if ((type != null) &&
			(ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX.endsWith(type) ||
			 ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX.endsWith(type) ||
			 ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX.endsWith(type) ||
			 ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX.endsWith(type) ||
			 ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX.endsWith(type) ||
			 ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX.endsWith(type))) {

			return true;
		}

		return false;
	}

	public static boolean isLiferaySDKProjectDir(File file) {
		if ((file != null) && file.isDirectory() && isValidLiferayProjectDir(file)) {

			// check for build.xml and docroot

			File[] contents = file.listFiles();

			boolean hasBuildXml = false;

			boolean hasDocroot = false;

			for (File content : contents) {
				if ("build.xml".equals(content.getName())) {
					hasBuildXml = true;

					continue;
				}

				if (ISDKConstants.DEFAULT_DOCROOT_FOLDER.equals(content.getName())) {
					hasDocroot = true;

					continue;
				}
			}

			if (hasBuildXml && hasDocroot) {
				return true;
			}
		}

		return false;
	}

	public static boolean isMavenProject(IProject project) {
		if (project == null) {
			return false;
		}

		boolean retval = false;

		try {
			retval =
				project.hasNature("org.eclipse.m2e.core.maven2Nature") && FileUtil.exists(project.getFile("pom.xml"));
		}
		catch (Exception e) {
		}

		return retval;
	}

	public static boolean isParent(IFolder folder, IResource resource) {
		if ((folder == null) || (resource == null)) {
			return false;
		}

		if ((resource.getParent() != null) && folder.equals(resource.getParent())) {
			return true;
		}
		else {
			boolean retval = isParent(folder, resource.getParent());

			if (retval) {
				return true;
			}
		}

		return false;
	}

	public static boolean isPortletProject(IProject project) {
		return hasFacet(project, IPluginFacetConstants.LIFERAY_PORTLET_PROJECT_FACET);
	}

	public static boolean isThemeProject(IProject project) {
		return hasFacet(project, IPluginFacetConstants.LIFERAY_THEME_FACET_ID);
	}

	public static boolean isValidLiferayProjectDir(File dir) {
		String name = dir.getName();

		if (name.endsWith(ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX)) {

			return true;
		}

		return false;
	}

	public static boolean isWebProject(IProject project) {
		return hasFacet(project, IPluginFacetConstants.LIFERAY_WEB_FACET_ID);
	}

	public static boolean isWorkspaceWars(IProject project) {
		try {
			if (LiferayWorkspaceUtil.hasWorkspace() && FileUtil.exists(project.getFolder("src"))) {
				IProject wsProject = LiferayWorkspaceUtil.getWorkspaceProject();

				File wsRootDir = LiferayWorkspaceUtil.getWorkspaceProjectFile();

				String[] warsNames = LiferayWorkspaceUtil.getWarsDirs(wsProject);

				File[] warsDirs = new File[warsNames.length];

				for (int i = 0; i < warsNames.length; i++) {
					warsDirs[i] = new File(wsRootDir, warsNames[i]);
				}

				IPath location = project.getLocation();

				File projectDir = location.toFile();

				File parentDir = projectDir.getParentFile();

				if (parentDir == null) {
					return false;
				}

				while (true) {
					for (File dir : warsDirs) {
						if (parentDir.equals(dir)) {
							return true;
						}
					}

					parentDir = parentDir.getParentFile();

					if (parentDir == null) {
						return false;
					}
				}
			}
		}
		catch (CoreException ce) {
			ProjectCore.logError(ce);
		}

		return false;
	}

	public static String removePluginSuffix(String string) {
		if (string == null) {
			return null;
		}

		String regex = null;

		if (string.endsWith(ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX)) {
			regex = ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX + "$";
		}
		else if (string.endsWith(ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX)) {
			regex = ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX + "$";
		}
		else if (string.endsWith(ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX)) {
			regex = ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX + "$";
		}
		else if (string.endsWith(ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX)) {
			regex = ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX + "$";
		}
		else if (string.endsWith(ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX)) {
			regex = ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX + "$";
		}
		else if (string.endsWith(ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX)) {
			regex = ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX + "$";
		}
		else {
			return string;
		}

		return string.replaceFirst(regex, StringPool.EMPTY);
	}

	public static void setDefaultRuntime(IDataModel dataModel) {
		DataModelPropertyDescriptor[] validDescriptors = dataModel.getValidPropertyDescriptors(
			IFacetProjectCreationDataModelProperties.FACET_RUNTIME);

		for (DataModelPropertyDescriptor desc : validDescriptors) {
			Object runtime = desc.getPropertyValue();

			if ((runtime instanceof BridgedRuntime) && ServerUtil.isLiferayRuntime((BridgedRuntime)runtime)) {
				dataModel.setProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME, runtime);

				break;
			}
		}
	}

	public static void setGenerateDD(IDataModel model, boolean generateDD) {
		IDataModel ddModel = null;

		if (hasProperty(model, IJ2EEFacetInstallDataModelProperties.GENERATE_DD)) {
			ddModel = model;
		}
		else if (hasProperty(model, IFacetProjectCreationDataModelProperties.FACET_DM_MAP)) {
			FacetDataModelMap map = (FacetDataModelMap)model.getProperty(
				IFacetProjectCreationDataModelProperties.FACET_DM_MAP);

			ddModel = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());
		}

		if (ddModel != null) {
			ddModel.setBooleanProperty(IJ2EEFacetInstallDataModelProperties.GENERATE_DD, generateDD);
		}
	}

	private static boolean _checkGradleThemePlugin(IProject project) {
		IFile buildGradleFile = project.getFile("build.gradle");

		if (!buildGradleFile.exists()) {
			return false;
		}

		try (InputStream ins = buildGradleFile.getContents()) {
			String content = FileUtil.readContents(ins);

			Matcher matcher = _themeBuilderPlugin.matcher(content);

			if ((content != null) && matcher.matches()) {
				return true;
			}

			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	private static boolean _checkGradleWarPlugin(IProject project) {
		IFile buildGradleFile = project.getFile("build.gradle");

		if (!buildGradleFile.exists()) {
			return false;
		}

		try (InputStream ins = buildGradleFile.getContents()) {
			String content = FileUtil.readContents(ins);

			Matcher matcher = _warPlugin.matcher(content);

			if ((content != null) && matcher.matches()) {
				return true;
			}

			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	private static void _fixExtProjectClasspathEntries(IProject project) {
		try {
			boolean fixedAttr = false;

			IJavaProject javaProject = JavaCore.create(project);

			List<IClasspathEntry> newEntries = new ArrayList<>();

			IClasspathEntry[] entries = javaProject.getRawClasspath();

			for (IClasspathEntry entry : entries) {
				IClasspathEntry newEntry = null;

				if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					List<IClasspathAttribute> newAttrs = new ArrayList<>();

					IClasspathAttribute[] attrs = entry.getExtraAttributes();

					if (ListUtil.isNotEmpty(attrs)) {
						for (IClasspathAttribute attr : attrs) {
							IClasspathAttribute newAttr = null;

							if ("owner.project.facets".equals(attr.getName()) &&
								"liferay.plugin".equals(attr.getValue())) {

								newAttr = JavaCore.newClasspathAttribute(attr.getName(), "liferay.ext");
								fixedAttr = true;
							}
							else {
								newAttr = attr;
							}

							newAttrs.add(newAttr);
						}

						newEntry = JavaCore.newSourceEntry(
							entry.getPath(), entry.getInclusionPatterns(), entry.getExclusionPatterns(),
							entry.getOutputLocation(), newAttrs.toArray(new IClasspathAttribute[0]));
					}
				}

				if (newEntry == null) {
					newEntry = entry;
				}

				newEntries.add(newEntry);
			}

			if (fixedAttr) {
				IProgressMonitor monitor = new NullProgressMonitor();

				javaProject.setRawClasspath(newEntries.toArray(new IClasspathEntry[0]), monitor);

				try {
					IProject p = javaProject.getProject();

					p.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				}
				catch (Exception e) {
					ProjectCore.logError(e);
				}
			}

			fixExtProjectSrcFolderLinks(project);
		}
		catch (Exception ex) {
			ProjectCore.logError("Exception trying to fix Ext project classpath entries.", ex);
		}
	}

	private static boolean _isLiferayRuntimePluginClassPath(IClasspathEntry entry) {
		boolean retval = false;

		if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
			IPath p = entry.getPath();

			for (String path : p.segments()) {
				if (path.equals(PluginClasspathContainerInitializer.ID) ||
					path.equals("com.liferay.studio.server.tomcat.runtimeClasspathProvider") ||
					path.equals("com.liferay.ide.eclipse.server.tomcat.runtimeClasspathProvider")) {

					retval = true;

					break;
				}
			}
		}

		return retval;
	}

	private static final Pattern _themeBuilderPlugin = Pattern.compile(
		".*apply.*plugin.*:.*[\'\"]com\\.liferay\\.portal\\.tools\\.theme\\.builder[\'\"].*",
		Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern _warPlugin = Pattern.compile(".*apply.*war.*", Pattern.MULTILINE | Pattern.DOTALL);

	private static class Msgs extends NLS {

		public static String checking;
		public static String importingProject;

		static {
			initializeMessages(ProjectUtil.class.getName(), Msgs.class);
		}

	}

}