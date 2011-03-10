/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.project.core;

import com.liferay.ide.eclipse.core.ILiferayConstants;
import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author Greg Amerson
 */
public class PluginPackageResourceListener implements IResourceChangeListener {

	public static boolean isLiferayProject(IProject project) {
		boolean retval = false;
		
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			
			if (facetedProject != null) {	
				for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
					IProjectFacet projectFacet = facet.getProjectFacet();
					
					if (projectFacet.getId().startsWith("liferay.")) {
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

	public void resourceChanged(IResourceChangeEvent event) {
		if (event == null) {			
			return;			
		}

		try {
			if (shouldProcessResourceChangedEvent(event)) {
				IResourceDelta delta = event.getDelta();
				delta.accept(new IResourceDeltaVisitor() {

					public boolean visit(IResourceDelta delta)
						throws CoreException {

						try {
							int deltaKind = delta.getKind();

							if (deltaKind == IResourceDelta.REMOVED || deltaKind == IResourceDelta.REMOVED_PHANTOM) {
								return false;
							}

							if (shouldProcessResourceDelta(delta)) {
								processResourceChanged(delta);

								return false;
							}
						}
						catch (Exception e) {
							// do nothing
						}

						return delta.getResource() != null && delta.getResource().getType() != IResource.FILE;
					}
				});
			}
		}
		catch (CoreException e) {
		}
	}

	private IFile getWorkspaceFile(IPath path) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		return root.getFile(path);		
	}

	protected boolean isLiferayPluginProject(IPath deltaPath) {
		IFile pluginPackagePropertiesFile = getWorkspaceFile(deltaPath);
		
		if (pluginPackagePropertiesFile != null && pluginPackagePropertiesFile.exists()) {
			return isLiferayProject(pluginPackagePropertiesFile.getProject());			
		}		
		
		return false;		
	}


	protected void processResourceChanged(IResourceDelta delta)
		throws CoreException {

		IPath deltaPath = delta.getFullPath();
		
		IFile pluginPackagePropertiesFile = getWorkspaceFile(deltaPath);
		
		IProject project = pluginPackagePropertiesFile.getProject();
		
		IJavaProject javaProject = JavaCore.create(project);
		
		IPath containerPath = null;
		
		IClasspathEntry[] entries = javaProject.getRawClasspath();
		
		for (IClasspathEntry entry : entries) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				if (entry.getPath().segment(0).equals(PluginClasspathContainerInitializer.ID)) {
					containerPath = entry.getPath();
					
					break;				
				}
			}
		}
		
		if (containerPath != null) {
			IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(containerPath, javaProject);
			
			PluginClasspathContainerInitializer initializer =
				(PluginClasspathContainerInitializer) JavaCore.getClasspathContainerInitializer(PluginClasspathContainerInitializer.ID);
			
			initializer.requestClasspathContainerUpdate(containerPath, javaProject, classpathContainer);
		}

		Properties props = new Properties();
		
		try {
			props.load(pluginPackagePropertiesFile.getContents());
			
			String portalDependencyTlds = props.getProperty("portal-dependency-tlds");
			
			if (portalDependencyTlds != null) {
				String[] portalTlds = portalDependencyTlds.split(",");
				
				IVirtualComponent comp = ComponentCore.createComponent(pluginPackagePropertiesFile.getProject());
				
				if (comp != null) {
					IFolder webroot = (IFolder) comp.getRootFolder().getUnderlyingFolder();
					
					final IFolder tldFolder = webroot.getFolder("WEB-INF/tld");
					
					IPath portalDir = ServerUtil.getPortalDir(pluginPackagePropertiesFile.getProject());
					
					final List<IPath> tldFilesToCopy = new ArrayList<IPath>();
					
					for (String portalTld : portalTlds) {
						IFile tldFile = tldFolder.getFile(portalTld);
						
						if (!tldFile.exists()) {
							IPath realPortalTld = portalDir.append("WEB-INF/tld/" + portalTld);
							
							if (realPortalTld.toFile().exists()) {
								tldFilesToCopy.add(realPortalTld);
							}							
						}
					}
					
					if (tldFilesToCopy.size() > 0) {
						new WorkspaceJob("copy portal tlds") {

							@Override
							public IStatus runInWorkspace(IProgressMonitor monitor)
								throws CoreException {
								
								CoreUtil.prepareFolder(tldFolder);
								
								for (IPath tldFileToCopy : tldFilesToCopy) {
									IFile newTldFile = tldFolder.getFile(tldFileToCopy.lastSegment());
									
									try {
										newTldFile.create(new FileInputStream(tldFileToCopy.toFile()), true, null);										
									}
									catch (FileNotFoundException e) {
										throw new CoreException(ProjectCorePlugin.createErrorStatus(e));
									}
								}
								
								return Status.OK_STATUS;								
							}
						}.schedule();

					}
				}
			}
		}
		catch (Exception e) {
			ProjectCorePlugin.logError(e);
		}

	}


	protected boolean shouldProcessResourceChangedEvent(IResourceChangeEvent event) {
		if (event == null) {
			return false;
		}

		int eventType = event.getType();

		if (eventType != IResourceChangeEvent.POST_CHANGE) {
			return false;
		}

		IResourceDelta delta = event.getDelta();
		
		int deltaKind = delta.getKind();
		
		if (deltaKind == IResourceDelta.REMOVED || deltaKind == IResourceDelta.REMOVED_PHANTOM) {
			return false;
		}

		return true;
	}


	protected boolean shouldProcessResourceDelta(IResourceDelta delta) {
		if (delta == null) {
			return false;
		}
		
		int deltaKind = delta.getKind();

		if (deltaKind == IResourceDelta.REMOVED || deltaKind == IResourceDelta.REMOVED_PHANTOM) {
			return false;
		}

		IPath fullPath = delta.getFullPath();
		
		if (fullPath == null ||
			!(ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE.equals(fullPath.lastSegment()))) {
			return false;
		}

		IFile file = getWorkspaceFile(fullPath);

		if (file == null || !file.exists() || !file.isAccessible()) {
			return false;
		}

		return true;		
	}

	// private boolean findFileResourceInDelta(IResourceDelta delta, String
	// filename, IPath[] deltaPath) {
	// if (delta == null || filename == null || deltaPath == null ||
	// deltaPath.length < 1) {
	// return false;
	// }
	//		
	// IPath fullPath = delta.getFullPath();
	//		
	// if (fullPath != null && filename.equals(fullPath.lastSegment())) {
	// deltaPath[0] = fullPath;
	// return true;
	// }
	//		
	// for (IResourceDelta childDelta : delta.getAffectedChildren()) {
	// if (findFileResourceInDelta(childDelta, filename, deltaPath)) {
	// return true;
	// }
	// }
	//		
	// return false;
	// }

}
