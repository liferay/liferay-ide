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
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.ValidationUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.server.util.ComponentUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jst.common.jdt.internal.classpath.FlexibleProjectContainer;
import org.eclipse.jst.j2ee.internal.J2EEConstants;
import org.eclipse.jst.j2ee.internal.common.classpath.J2EEComponentClasspathContainerUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.IAddReferenceDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.operation.AddReferenceDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.operation.RemoveReferenceDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class PluginPackageResourceListener implements IResourceChangeListener, IResourceDeltaVisitor {

	public static boolean isLiferayProject(IProject project) {
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);

			if (facetedProject == null) {
				return false;
			}

			for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
				IProjectFacet projectFacet = facet.getProjectFacet();

				if (StringUtil.startsWith(projectFacet.getId(), "liferay.")) {
					return true;
				}
			}
		}
		catch (CoreException ce) {
		}

		return false;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (event == null) {
			return;
		}

		try {
			IResourceDelta delta = event.getDelta();

			delta.accept(this);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();

		switch (resource.getType()) {
			case IResource.ROOT:
			case IResource.PROJECT:
			case IResource.FOLDER:
				return true;

			case IResource.FILE:
				if (!shouldProcessResourceDelta(delta)) {
					return false;
				}

				Job job = new WorkspaceJob(Msgs.processingPluginPackageResource) {

					@Override
					public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
						IResource resource = delta.getResource();

						IPath location = resource.getLocation();

						if (!ValidationUtil.isProjectTargetDirFile(location.toFile())) {
							processPropertiesFile((IFile)resource);
						}

						return Status.OK_STATUS;
					}

				};

				job.setRule(CoreUtil.getWorkspaceRoot());

				job.schedule();
		}

		return false;
	}

	protected void addVirtualRef(IVirtualComponent rootComponent, IVirtualReference ref) throws CoreException {
		processVirtualRef(rootComponent, ref, new AddReferenceDataModelProvider());
	}

	protected boolean isLiferayPluginProject(IPath deltaPath) {
		IFile pluginPackagePropertiesFile = _getWorkspaceFile(deltaPath);

		if (FileUtil.exists(pluginPackagePropertiesFile)) {
			return isLiferayProject(pluginPackagePropertiesFile.getProject());
		}

		return false;
	}

	protected IVirtualReference processContext(IVirtualComponent rootComponent, String context) {

		// first check for jar file

		IFile serviceJar = ComponentUtil.findServiceJarForContext(context);

		if (serviceJar == null) {
			return null;
		}

		if (rootComponent == null) {
			return null;
		}

		String type = VirtualArchiveComponent.LIBARCHIVETYPE + IPath.SEPARATOR;

		IPath serviceJarPath = serviceJar.getFullPath();

		IPath relativePath = serviceJarPath.makeRelative();

		IVirtualComponent archive = ComponentCore.createArchiveComponent(
			rootComponent.getProject(), type + relativePath.toString());

		IVirtualReference ref = ComponentCore.createReference(
			rootComponent, archive,
			new Path(
				J2EEConstants.WEB_INF_LIB
			).makeAbsolute());

		ref.setArchiveName(serviceJarPath.lastSegment());

		return ref;
	}

	protected void processPortalDependencyTlds(Properties props, IProject project) {
		String portalDependencyTlds = props.getProperty("portal-dependency-tlds");

		if (portalDependencyTlds == null) {
			return;
		}

		IVirtualComponent component = ComponentCore.createComponent(project);

		if (component == null) {
			return;
		}

		IVirtualFolder virtualFolder = component.getRootFolder();

		IFolder webroot = (IFolder)virtualFolder.getUnderlyingFolder();

		IFolder tldFolder = webroot.getFolder("WEB-INF/tld");

		IPath portalDir = ServerUtil.getPortalDir(project);

		List<IPath> tldFilesToCopy = new ArrayList<>();

		String[] portalTlds = portalDependencyTlds.split(StringPool.COMMA);

		if (portalDir != null) {
			for (String portalTld : portalTlds) {
				IFile tldFile = tldFolder.getFile(portalTld);

				if (!tldFile.exists()) {
					IPath realPortalTld = portalDir.append("WEB-INF/tld/" + portalTld);

					if (FileUtil.exists(realPortalTld)) {
						tldFilesToCopy.add(realPortalTld);
					}
				}
			}
		}

		if (ListUtil.isEmpty(tldFilesToCopy)) {
			return;
		}

		new WorkspaceJob(
			"copy portal tlds"
		) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				CoreUtil.prepareFolder(tldFolder);

				for (IPath tldFileToCopy : tldFilesToCopy) {
					IFile newTldFile = tldFolder.getFile(tldFileToCopy.lastSegment());

					File file = tldFileToCopy.toFile();

					try (InputStream inputStream = Files.newInputStream(file.toPath())) {
						newTldFile.create(inputStream, true, null);
					}
					catch (Exception e) {
						throw new CoreException(ProjectCore.createErrorStatus(e));
					}
				}

				return Status.OK_STATUS;
			}

		}.schedule();
	}

	protected void processPropertiesFile(IFile pluginPackagePropertiesFile) throws CoreException {
		if (FileUtil.notExists(pluginPackagePropertiesFile)) {
			return;
		}

		IJavaProject javaProject = JavaCore.create(pluginPackagePropertiesFile.getProject());

		IPath containerPath = null;

		IClasspathEntry[] entries = javaProject.getRawClasspath();

		for (IClasspathEntry entry : entries) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				IPath path = entry.getPath();

				String segment = path.segment(0);

				if (segment.equals(PluginClasspathContainerInitializer.ID) ||
					segment.equals(SDKClasspathContainer.ID)) {

					containerPath = entry.getPath();

					break;
				}
			}
		}

		if (containerPath != null) {
			IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(containerPath, javaProject);

			String id = containerPath.segment(0);

			if (id.equals(PluginClasspathContainerInitializer.ID) || id.equals(SDKClasspathContainer.ID)) {
				ClasspathContainerInitializer initializer = JavaCore.getClasspathContainerInitializer(id);

				initializer.requestClasspathContainerUpdate(containerPath, javaProject, classpathContainer);
			}
		}

		Properties props = new Properties();

		try (InputStream contents = pluginPackagePropertiesFile.getContents()) {
			props.load(contents);

			// processPortalDependencyTlds(props, pluginPackagePropertiesFile.getProject());

			processRequiredDeploymentContexts(props, pluginPackagePropertiesFile.getProject());
		}
		catch (Exception e) {
			ProjectCore.logError(e);
		}
	}

	protected void processRequiredDeploymentContexts(Properties props, IProject project) {
		IVirtualComponent rootComponent = ComponentCore.createComponent(project);

		if (rootComponent == null) {
			return;
		}

		IClasspathContainer webAppLibrariesContainer =
			J2EEComponentClasspathContainerUtils.getInstalledWebAppLibrariesContainer(project);

		if (webAppLibrariesContainer == null) {
			return;
		}

		IClasspathEntry[] existingEntries = webAppLibrariesContainer.getClasspathEntries();

		List<IVirtualReference> removeRefs = new ArrayList<>();

		for (IClasspathEntry entry : existingEntries) {
			IPath path = entry.getPath();

			String archiveName = path.lastSegment();

			if (archiveName.endsWith("-service.jar")) {
				IFile file = _getWorkspaceFile(path);

				if (FileUtil.exists(file) && ProjectUtil.isLiferayFacetedProject(file.getProject())) {
					for (IVirtualReference ref : rootComponent.getReferences()) {
						if (archiveName.equals(ref.getArchiveName())) {
							removeRefs.add(ref);
						}
					}
				}
			}
		}

		List<IVirtualReference> addRefs = new ArrayList<>();

		String requiredDeploymenContexts = props.getProperty("required-deployment-contexts");

		if (requiredDeploymenContexts != null) {
			String[] contexts = requiredDeploymenContexts.split(StringPool.COMMA);

			if (ListUtil.isNotEmpty(contexts)) {
				for (String context : contexts) {
					IVirtualReference ref = processContext(rootComponent, context);

					if (ref != null) {
						addRefs.add(ref);
					}
				}
			}
		}

		new WorkspaceJob(
			"Update virtual component."
		) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				updateVirtualComponent(rootComponent, removeRefs, addRefs);

				updateWebClasspathContainer(rootComponent, addRefs);

				return Status.OK_STATUS;
			}

		}.schedule();
	}

	protected void processResourceChanged(IResourceDelta delta) throws CoreException {
		IPath deltaPath = delta.getFullPath();

		IFile pluginPackagePropertiesFile = _getWorkspaceFile(deltaPath);

		new WorkspaceJob(
			Msgs.processingPluginPackageResource
		) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				processPropertiesFile(pluginPackagePropertiesFile);

				return Status.OK_STATUS;
			}

		}.schedule();
	}

	protected void processVirtualRef(
			IVirtualComponent rootComponent, IVirtualReference ref, IDataModelProvider provider)
		throws CoreException {

		IDataModel dm = DataModelFactory.createDataModel(provider);

		dm.setProperty(IAddReferenceDataModelProperties.SOURCE_COMPONENT, rootComponent);
		dm.setProperty(IAddReferenceDataModelProperties.TARGET_REFERENCE_LIST, Arrays.asList(ref));

		IStatus status = dm.validateProperty(IAddReferenceDataModelProperties.TARGET_REFERENCE_LIST);

		if ((status == null) || !status.isOK()) {
			throw new CoreException(status);
		}

		try {
			IDataModelOperation dataModelOperation = dm.getDefaultOperation();

			dataModelOperation.execute(new NullProgressMonitor(), null);
		}
		catch (ExecutionException ee) {
			ProjectCore.logError(ee);
		}
	}

	protected void removeVirtualRef(IVirtualComponent rootComponent, IVirtualReference ref) throws CoreException {
		processVirtualRef(rootComponent, ref, new RemoveReferenceDataModelProvider());
	}

	protected boolean shouldProcessResourceChangedEvent(IResourceChangeEvent event) {
		if (event == null) {
			return false;
		}

		IResourceDelta delta = event.getDelta();

		int deltaKind = delta.getKind();

		if ((deltaKind == IResourceDelta.REMOVED) || (deltaKind == IResourceDelta.REMOVED_PHANTOM)) {
			return false;
		}

		return true;
	}

	protected boolean shouldProcessResourceDelta(IResourceDelta delta) {
		IPath fullPath = delta.getFullPath();

		if ((fullPath.lastSegment() == null) ||
			!ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE.equals(fullPath.lastSegment())) {

			return false;
		}

		IProject project = CoreUtil.getLiferayProject(delta.getResource());

		if (project == null) {
			return false;
		}

		IWebProject liferayProject = LiferayCore.create(IWebProject.class, project);

		if (liferayProject == null) {
			return false;
		}

		Path path = new Path("WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE);

		IResource propertiesFile = liferayProject.findDocrootResource(path);

		if (FileUtil.notExists(propertiesFile)) {
			return false;
		}

		IPath filePath = propertiesFile.getFullPath();

		return filePath.equals(fullPath);
	}

	protected void updateVirtualComponent(
			IVirtualComponent rootComponent, List<IVirtualReference> removeRefs, List<IVirtualReference> addRefs)
		throws CoreException {

		for (IVirtualReference ref : removeRefs) {
			removeVirtualRef(rootComponent, ref);
		}

		for (IVirtualReference ref : addRefs) {
			addVirtualRef(rootComponent, ref);
		}
	}

	protected void updateWebClasspathContainer(IVirtualComponent rootComponent, List<IVirtualReference> addRefs)
		throws CoreException {

		IProject project = rootComponent.getProject();

		IJavaProject javaProject = JavaCore.create(project);

		FlexibleProjectContainer container = J2EEComponentClasspathContainerUtils.getInstalledWebAppLibrariesContainer(
			project);

		// If the container is present, refresh it

		if (container == null) {
			return;
		}

		container.refresh();

		// need to regrab this to get newest container

		container = J2EEComponentClasspathContainerUtils.getInstalledWebAppLibrariesContainer(project);

		if (container == null) {
			return;
		}

		IClasspathEntry[] webappEntries = container.getClasspathEntries();

		for (IClasspathEntry entry : webappEntries) {
			IPath path = entry.getPath();

			String archiveName = path.lastSegment();

			for (IVirtualReference ref : addRefs) {
				if (archiveName.equals(ref.getArchiveName())) {
					IVirtualComponent component = ref.getReferencedComponent();

					IFile referencedFile = component.getAdapter(IFile.class);

					IProject referencedFileProject = referencedFile.getProject();

					// IDE-110 IDE-648

					IFolder folder = referencedFileProject.getFolder(
						ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/service");

					ClasspathEntry classpathEntry = (ClasspathEntry)entry;

					classpathEntry.sourceAttachmentPath = folder.getFullPath();
				}
			}
		}

		ClasspathContainerInitializer initializer = JavaCore.getClasspathContainerInitializer(
			"org.eclipse.jst.j2ee.internal.web.container");

		initializer.requestClasspathContainerUpdate(container.getPath(), javaProject, container);
	}

	private IFile _getWorkspaceFile(IPath path) {
		IWorkspaceRoot root = CoreUtil.getWorkspaceRoot();

		IFile file = root.getFile(path);

		if (!file.exists()) {
			file = root.getFileForLocation(path);
		}

		return file;
	}

	private static class Msgs extends NLS {

		public static String processingPluginPackageResource;

		static {
			initializeMessages(PluginPackageResourceListener.class.getName(), Msgs.class);
		}

	}

}