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

package com.liferay.ide.project.core.facet;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEModuleFacetInstallDataModelProperties;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public abstract class PluginFacetInstall implements IDelegate, IPluginProjectDataModelProperties {

	@Override
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
		throws CoreException {

		if (!(config instanceof IDataModel)) {
			return;
		}
		else {
			model = (IDataModel)config;

			masterModel = (IDataModel)model.getProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM);

			this.project = project;

			this.monitor = monitor;
		}

		/**
		 *  IDE-195 If the user has the plugins sdk in the workspace,
		 *  trying to write to the P/foo-portlet/.settings/ will find the file first in the the plugins-sdk
		 *  that is in the workspace and will fail to find the file.
		 */
		try {
			IProject p = project.getProject();

			File file = FileUtil.getFile(p.getFile(_PATH_IN_PROJECT));

			IWorkspaceRoot root = CoreUtil.getWorkspaceRoot();

			IFile[] wsFiles = root.findFilesForLocationURI(file.toURI());

			if (ListUtil.isNotEmpty(wsFiles)) {
				for (IFile wsFile : wsFiles) {
					IContainer parent = wsFile.getParent();

					IContainer container = parent.getParent();

					container.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
			}
		}
		catch (Exception ex) {

			// best effort to make sure directories are current

		}

		if (shouldInstallPluginLibraryDelegate()) {
			installPluginLibraryDelegate();
		}

		if (shouldSetupDefaultOutputLocation()) {
			setupDefaultOutputLocation();

			IJavaProject javaProject = JavaCore.create(project);

			IFolder folder = project.getFolder(getDefaultOutputLocation());

			IPath outputLocation = folder.getFullPath();

			javaProject.setOutputLocation(outputLocation, monitor);
		}
	}

	protected void configureDeploymentAssembly(String srcPath, String deployPath) {
		IVirtualComponent vProject = ComponentCore.createComponent(project);

		IVirtualFolder vProjectFolder = vProject.getRootFolder();

		IVirtualFolder deployFolder = vProjectFolder.getFolder(new Path(deployPath));

		try {
			deployFolder.createLink(new Path(srcPath), IResource.FORCE, null);
		}
		catch (CoreException ce) {
			ProjectCore.logError("Unable to create link", ce);
		}

		try {
			IJavaProject javaProject = JavaCore.create(project);

			IPath outputLocation = javaProject.getOutputLocation();

			vProject.setMetaProperty(IModuleConstants.PROJ_REL_JAVA_OUTPUT_PATH, outputLocation.toPortableString());
		}
		catch (JavaModelException jme) {
			ProjectCore.logError("Unable to set java-ouput-path", jme);
		}
	}

	protected void copyToProject(IPath parent, File newFile) throws CoreException, IOException {
		if ((newFile == null) || !shouldCopyToProject(newFile)) {
			return;
		}

		IResource projectEntry = null;

		IPath newFilePath = new Path(newFile.getPath());

		IPath newFileRelativePath = newFilePath.makeRelativeTo(parent);

		if (newFile.isDirectory()) {
			projectEntry = project.getFolder(newFileRelativePath);
		}
		else {
			projectEntry = project.getFile(newFileRelativePath);
		}

		if (FileUtil.exists(projectEntry)) {
			if (projectEntry instanceof IFolder) {

				// folder already exists, we can return

				return;
			}
			else if (projectEntry instanceof IFile) {
				try (InputStream inputStream = Files.newInputStream(newFile.toPath())) {
					((IFile)projectEntry).setContents(inputStream, IResource.FORCE, null);
				}
			}
		}
		else if (projectEntry instanceof IFolder) {
			IFolder newProjectFolder = (IFolder)projectEntry;

			newProjectFolder.create(true, true, null);
		}
		else if (projectEntry instanceof IFile) {
			try (InputStream inputStream = Files.newInputStream(newFile.toPath())) {
				((IFile)projectEntry).create(inputStream, IResource.FORCE, null);
			}
		}
	}

	protected boolean deletePath(IPath path) {
		if (FileUtil.exists(path)) {
			File file = path.toFile();

			return file.delete();
		}

		return false;
	}

	protected IPath getAppServerDir() {
		IRuntime serverRuntime;

		if (masterModel != null) {
			serverRuntime = (IRuntime)masterModel.getProperty(PluginFacetInstallDataModelProvider.FACET_RUNTIME);
		}
		else {
			serverRuntime = getFacetedProject().getPrimaryRuntime();
		}

		return ServerUtil.getAppServerDir(serverRuntime);
	}

	protected abstract String getDefaultOutputLocation();

	protected IDataModel getFacetDataModel(String facetId) {
		IFacetedProjectWorkingCopy fp = getFacetedProject();

		for (IProjectFacetVersion pfv : fp.getProjectFacets()) {
			IProjectFacet projectFacet = pfv.getProjectFacet();

			if (StringUtil.equals(projectFacet.getId(), facetId)) {
				Action action = fp.getProjectFacetAction(pfv.getProjectFacet());

				if (action != null) {
					Object config = action.getConfig();

					IAdapterManager adapterManager = Platform.getAdapterManager();

					return (IDataModel)adapterManager.getAdapter(config, IDataModel.class);
				}
			}
		}

		return null;
	}

	protected IFacetedProjectWorkingCopy getFacetedProject() {
		return (IFacetedProjectWorkingCopy)model.getProperty(IFacetDataModelProperties.FACETED_PROJECT_WORKING_COPY);
	}

	protected String getRuntimeLocation() {
		try {
			org.eclipse.wst.server.core.IRuntime runtime = ServerUtil.getRuntime(project);

			return FileUtil.toOSString(runtime.getLocation());
		}
		catch (CoreException ce) {
			ce.printStackTrace();
		}

		return null;
	}

	protected SDK getSDK() {
		String sdkName = null;

		try {
			sdkName = masterModel.getStringProperty(IPluginProjectDataModelProperties.LIFERAY_SDK_NAME);
		}
		catch (Exception ex) {
			sdkName = model.getStringProperty(IPluginProjectDataModelProperties.LIFERAY_SDK_NAME);
		}

		if (sdkName == null) {
			return null;
		}

		SDKManager sdkManager = SDKManager.getInstance();

		return sdkManager.getSDK(sdkName);
	}

	protected IFolder getWebRootFolder() {
		IDataModel webFacetDataModel = null;

		if (masterModel != null) {
			FacetDataModelMap map = (FacetDataModelMap)masterModel.getProperty(
				IFacetProjectCreationDataModelProperties.FACET_DM_MAP);

			webFacetDataModel = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());
		}
		else {
			webFacetDataModel = getFacetDataModel(IModuleConstants.JST_WEB_MODULE);
		}

		IPath webrootFullPath = null;

		if (webFacetDataModel != null) {
			String configFolder = webFacetDataModel.getStringProperty(
				IJ2EEModuleFacetInstallDataModelProperties.CONFIG_FOLDER);

			IPath fullPath = project.getFullPath();

			webrootFullPath = fullPath.append(configFolder);
		}
		else {
			IVirtualComponent component = ComponentCore.createComponent(project);

			if (component != null) {
				IVirtualFolder rootFolder = component.getRootFolder();

				IContainer container = rootFolder.getUnderlyingFolder();

				webrootFullPath = container.getFullPath();
			}
		}

		IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

		return workspaceRoot.getFolder(webrootFullPath);
	}

	protected void installPluginLibraryDelegate() throws CoreException {
		LibraryInstallDelegate libraryDelegate = (LibraryInstallDelegate)model.getProperty(
			IPluginProjectDataModelProperties.LIFERAY_PLUGIN_LIBRARY_DELEGATE);

		libraryDelegate.execute(monitor);
	}

	protected boolean isProjectInSDK() {
		return masterModel.getBooleanProperty(LIFERAY_USE_SDK_LOCATION);
	}

	protected void processNewFiles(IPath path) throws CoreException {
		try {
			List<File> newFiles = FileListing.getFileListing(path.toFile());

			for (File file : newFiles) {
				try {
					copyToProject(path, file);
				}
				catch (Exception e) {
					ProjectCore.logError(e);
				}
			}
		}
		catch (FileNotFoundException fnfe) {
			throw new CoreException(ProjectCore.createErrorStatus(fnfe));
		}
	}

	protected void setupDefaultOutputLocation() throws CoreException {
		IJavaProject jProject = JavaCore.create(project);

		IFolder folder = project.getFolder(getDefaultOutputLocation());

		if (FileUtil.exists(folder.getParent())) {
			CoreUtil.prepareFolder(folder);

			IPath oldOutputLocation = jProject.getOutputLocation();

			IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

			IFolder oldOutputFolder = workspaceRoot.getFolder(oldOutputLocation);

			jProject.setOutputLocation(folder.getFullPath(), null);

			try {
				if (!folder.equals(oldOutputFolder) && FileUtil.exists(oldOutputFolder)) {
					IContainer outputParent = oldOutputFolder.getParent();

					oldOutputFolder.delete(true, null);

					if (ListUtil.isEmpty(outputParent.members()) && "build".equals(outputParent.getName())) {
						outputParent.delete(true, null);
					}
				}
			}
			catch (Exception e) {

				// best effort

			}
		}
	}

	protected boolean shouldConfigureDeploymentAssembly() {
		return model.getBooleanProperty(CONFIGURE_DEPLOYMENT_ASSEMBLY);
	}

	protected boolean shouldCopyToProject(File file) {
		if (isProjectInSDK()) {
			return true;
		}

		for (String name : ISDKConstants.PORTLET_PLUGIN_ZIP_IGNORE_FILES) {
			if (name.equals(file.getName())) {
				return false;
			}
		}

		return true;
	}

	protected boolean shouldInstallPluginLibraryDelegate() {
		return model.getBooleanProperty(INSTALL_LIFERAY_PLUGIN_LIBRARY_DELEGATE);
	}

	protected boolean shouldSetupDefaultOutputLocation() {
		return model.getBooleanProperty(SETUP_DEFAULT_OUTPUT_LOCATION);
	}

	protected boolean shouldSetupExtClasspath() {
		return model.getBooleanProperty(SETUP_EXT_CLASSPATH);
	}

	protected static final String DEFAULT_DEPLOY_PATH = "/WEB-INF/classes";

	protected IDataModel masterModel = null;
	protected IDataModel model = null;
	protected IProgressMonitor monitor;
	protected IProject project;

	/**
	 * copied from ProjectFacetPreferencesGroup
	 */
	private static final String _PATH_IN_PROJECT = ".settings/org.eclipse.wst.common.project.facet.core.prefs.xml";

}