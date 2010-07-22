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

package com.liferay.ide.eclipse.project.core.util;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.IProjectDefinition;
import com.liferay.ide.eclipse.project.core.ProjectCorePlugin;
import com.liferay.ide.eclipse.project.core.facet.ExtPluginFacetInstall;
import com.liferay.ide.eclipse.project.core.facet.HookPluginFacetInstall;
import com.liferay.ide.eclipse.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.eclipse.project.core.facet.PortletPluginFacetInstall;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetInstallDataModelProperties;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FacetDataModelMap;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class ProjectUtil {

	public static final String METADATA_FOLDER = ".metadata";

	public static boolean collectProjectsFromDirectory(
		Collection<File> eclipseProjectFiles, Collection<File> liferayProjectDirs, File directory,
		Set<String> directoriesVisited, boolean recurse, IProgressMonitor monitor) {

		if (monitor.isCanceled()) {
			return false;
		}

		monitor.subTask(NLS.bind(DataTransferMessages.WizardProjectsImportPage_CheckingMessage, directory.getPath()));

		File[] contents = directory.listFiles();

		if (contents == null) {
			return false;
		}

		// Initialize recursion guard for recursive symbolic links
		if (directoriesVisited == null) {
			directoriesVisited = new HashSet<String>();

			try {
				directoriesVisited.add(directory.getCanonicalPath());
			}
			catch (IOException exception) {
				StatusManager.getManager().handle(
					StatusUtil.newStatus(IStatus.ERROR, exception.getLocalizedMessage(), exception));
			}
		}

		// first look for project description files
		final String dotProject = IProjectDescription.DESCRIPTION_FILE_NAME;

		for (int i = 0; i < contents.length; i++) {
			File file = contents[i];

			if (isLiferayProjectDir(file)) {
				// recurse to see if it has project file
				int currentSize = eclipseProjectFiles.size();

				collectProjectsFromDirectory(
					eclipseProjectFiles, liferayProjectDirs, contents[i], directoriesVisited, false, monitor);

				int newSize = eclipseProjectFiles.size();

				if (newSize == currentSize) {
					liferayProjectDirs.add(file);
				}
			}
			else if (file.isFile() && file.getName().equals(dotProject)) {
				if (!eclipseProjectFiles.contains(file) && isLiferayProjectDir(file.getParentFile())) {
					eclipseProjectFiles.add(file);
				}

				// don't search sub-directories since we can't have nested
				// projects
				return true;
			}
		}

		// no project description found, so recurse into sub-directories
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].isDirectory()) {
				if (!contents[i].getName().equals(METADATA_FOLDER)) {
					try {
						String canonicalPath = contents[i].getCanonicalPath();

						if (!directoriesVisited.add(canonicalPath)) {

							// already been here --> do not recurse
							continue;
						}
					}
					catch (IOException exception) {
						StatusManager.getManager().handle(
							StatusUtil.newStatus(IStatus.ERROR, exception.getLocalizedMessage(), exception));

					}

					// dont recurse directories that we have already determined
					// are Liferay projects
					if (!liferayProjectDirs.contains(contents[i]) && recurse) {
						collectProjectsFromDirectory(
							eclipseProjectFiles, liferayProjectDirs, contents[i], directoriesVisited, recurse, monitor);
					}
				}
			}
		}
		return true;
	}

	public static IFacetedProject getFacetedProject(IProject project) {
		try {
			return ProjectFacetsManager.create(project);
		}
		catch (CoreException e) {
			return null;
		}
	}

	public static Set<IProjectFacetVersion> getFacetsForPreset(String presetId) {
		IPreset preset = ProjectFacetsManager.getPreset(presetId);
		return preset.getProjectFacets();
	}

	public static IProjectFacet getLiferayFacet(IFacetedProject facetedProject) {
		for (IProjectFacetVersion projectFacet : facetedProject.getProjectFacets()) {
			if (isLiferayFacet(projectFacet.getProjectFacet())) {
				return projectFacet.getProjectFacet();
			}
		}
		return null;
	}

	public static IProject getProject(IDataModel model) {
		if (model != null) {
			String projectName = model.getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);
			return CoreUtil.getProject(projectName);
		}
		return null;
	}

	public static IProject getProject(String projectName) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}

	public static IFolder getDocroot(IProject project) {
		IContainer retval = null;

		if (project != null) {
			IVirtualComponent comp = ComponentCore.createComponent(project);

			if (comp != null) {
				IVirtualFolder rootFolder = comp.getRootFolder();

				if (rootFolder != null) {
					retval = rootFolder.getUnderlyingFolder();
				}
			}
		}

		return retval instanceof IFolder ? (IFolder) retval : null;
	}

	public static IFolder getDocroot(String projectName) {
		IProject project = CoreUtil.getProject(projectName);

		return getDocroot(project);
	}

	public static SDK getSDK(IFacetedProject facetedProject, IProjectFacet facet)
		throws BackingStoreException {

		Preferences prefs = facetedProject.getPreferences(facet).node("liferay-plugin-project");
		String name = prefs.get(ISDKConstants.PROPERTY_NAME, null);

		return SDKManager.getSDKByName(name);
	}

	public static SDK getSDK(IProject proj, IProjectFacet projectFacet)
		throws BackingStoreException {

		IFacetedProject facetedProject = getFacetedProject(proj);

		return getSDK(facetedProject, projectFacet);
	}

	public static IPackageFragmentRoot[] getSourceContainers(IProject project) {
		IJavaProject jProject = JavaCore.create(project);
		if (jProject == null)
			return new IPackageFragmentRoot[0];
		List<IPackageFragmentRoot> list = new ArrayList<IPackageFragmentRoot>();
		IVirtualComponent vc = ComponentCore.createComponent(project);
		IPackageFragmentRoot[] roots;
		try {
			roots = jProject.getPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++) {
				if (roots[i].getKind() != IPackageFragmentRoot.K_SOURCE)
					continue;
				IResource resource = roots[i].getResource();
				if (null != resource) {
					IVirtualResource[] vResources = ComponentCore.createResources(resource);
					boolean found = false;
					for (int j = 0; !found && j < vResources.length; j++) {
						if (vResources[j].getComponent().equals(vc)) {
							if (!list.contains(roots[i]))
								list.add(roots[i]);
							found = true;
						}
					}
				}
			}
		}
		catch (JavaModelException e) {
			ProjectCorePlugin.logError(e);
		}
		return (IPackageFragmentRoot[]) list.toArray(new IPackageFragmentRoot[list.size()]);
	}

	public static IFolder[] getSourceFolders(IProject project) {
		List<IFolder> sourceFolders = new ArrayList<IFolder>();

		IPackageFragmentRoot[] sources = getSourceContainers(project);

		for (IPackageFragmentRoot source : sources) {
			if (source.getResource() instanceof IFolder) {
				sourceFolders.add(((IFolder) source.getResource()));
			}
		}

		return sourceFolders.toArray(new IFolder[sourceFolders.size()]);
	}

	public static boolean hasFacet(IProject project, String facetId) {
		return hasFacet(project, ProjectFacetsManager.getProjectFacet(facetId));
	}

	public static boolean hasFacet(IProject project, IProjectFacet checkProjectFacet) {
		boolean retval = false;
		if (project == null || checkProjectFacet == null) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			if (facetedProject != null && checkProjectFacet != null) {
				for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
					IProjectFacet projectFacet = facet.getProjectFacet();
					if (checkProjectFacet.equals(projectFacet)) {
						retval = true;
						break;
					}
				}
			}
		}
		catch (CoreException e) {
		}
		return retval;
	}

	public static boolean isDynamicWebFacet(IProjectFacet facet) {
		return facet != null && facet.getId().equals(IModuleConstants.JST_WEB_MODULE);
	}

	public static boolean isExtProject(IProject project) {
		return hasFacet(project, ExtPluginFacetInstall.LIFERAY_EXT_PLUGIN_FACET);
	}

	public static boolean isThemeProject(IProject project) {
		return hasFacet(project, IPluginFacetConstants.LIFERAY_THEME_PLUGIN_FACET_ID);
	}

	public static boolean isHookProject(IProject project) {
		return hasFacet(project, HookPluginFacetInstall.LIFERAY_HOOK_PLUGIN_FACET);
	}

	public static boolean isJavaFacet(IProjectFacet facet) {
		return facet != null && facet.getId().equals(IModuleConstants.JST_JAVA);
	}

	public static boolean isLiferayFacet(IProjectFacet projectFacet) {
		return ProjectCorePlugin.getProjectDefinition(projectFacet) != null;
	}

	public static boolean isLiferayProject(IProject project) {
		boolean retval = false;
		if (project == null) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			if (facetedProject != null) {
				for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
					IProjectFacet projectFacet = facet.getProjectFacet();
					IProjectDefinition projectDefinition = ProjectCorePlugin.getProjectDefinition(projectFacet);

					if (projectDefinition != null) {
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

	public static boolean isLiferayProjectDir(File file) {
		if (file.isDirectory() && isValidLiferayProjectDir(file)) {
			// check for build.xml and docroot
			File[] contents = file.listFiles();

			boolean hasBuildXml = false;

			boolean hasDocroot = false;

			for (File content : contents) {
				if (content.getName().equals("build.xml")) {
					hasBuildXml = true;

					continue;
				}

				if (content.getName().equals("docroot")) {
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

	public static boolean isParent(IFolder folder, IResource resource) {
		if (folder == null || resource == null) {
			return false;
		}

		if (resource.getParent() != null && resource.getParent().equals(folder)) {
			return true;
		}
		else {
			boolean retval = isParent(folder, resource.getParent());
			if (retval == true) {
				return true;
			}
		}

		return false;
	}

	public static boolean isPortletProject(IProject project) {
		return hasFacet(project, PortletPluginFacetInstall.LIFERAY_PORTLET_PLUGIN_FACET);
	}

	public static boolean isValidLiferayProjectDir(File dir) {
		String name = dir.getName();

		if (name.endsWith(ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX) ||
			name.endsWith(ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX)) {
			return true;
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
		else {
			return string;
		}

		return string.replaceFirst(regex, "");
	}

	public static void setGenerateDD(IDataModel model, boolean generateDD) {
		FacetDataModelMap map =
			(FacetDataModelMap) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_DM_MAP);
		IDataModel webFacet = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());
		webFacet.setBooleanProperty(IJ2EEFacetInstallDataModelProperties.GENERATE_DD, generateDD);
	}

	public static void setSDK(IProject project, IProjectFacet facet, SDK sdk)
		throws BackingStoreException, CoreException {
		Preferences prefs = ProjectFacetsManager.create(project).getPreferences(facet).node("liferay-plugin-project");
		prefs.put(ISDKConstants.PROPERTY_NAME, sdk.getName());
		prefs.flush();
	}

	public static IFile createEmptyProjectFile(String fileName, IFolder folder)
		throws CoreException {
	
		IFile emptyFile = folder.getFile(fileName);
	
		if (emptyFile.exists()) {
			return emptyFile;
		}
		else {
			emptyFile.create(new ByteArrayInputStream("".getBytes()), true, null);
		}
	
		return emptyFile;
	}

	public static String getRelativePathFromDocroot(IProject project, String path) {
		IFolder docroot = getDocroot(project);
	
		IPath pathValue = new Path(path);
	
		IPath relativePath = pathValue.makeRelativeTo(docroot.getFullPath());
	
		String retval = relativePath.toPortableString();
	
		return retval.startsWith("/") ? retval : "/" + retval;
	}


}
