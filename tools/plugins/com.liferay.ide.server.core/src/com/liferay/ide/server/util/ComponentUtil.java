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

package com.liferay.ide.server.util;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.ValidationRunner;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class ComponentUtil {

	public static boolean containsMember(IModuleResourceDelta delta, String[] paths) {
		if (delta == null) {
			return false;
		}

		// iterate over the path and find matching child delta

		IModuleResourceDelta[] currentChildren = delta.getAffectedChildren();

		if (currentChildren == null) {
			IModuleResource deltaModuleResource = delta.getModuleResource();

			IFile file = deltaModuleResource.getAdapter(IFile.class);

			if (file != null) {
				IPath fileFullPath = file.getFullPath();

				String filePath = fileFullPath.toString();

				for (String path : paths) {
					if (filePath.contains(path)) {
						return true;
					}
				}
			}

			return false;
		}

		for (int j = 0, jmax = currentChildren.length; j < jmax; j++) {
			IPath moduleRelativePath = currentChildren[j].getModuleRelativePath();

			String moduleRelativePathValue = moduleRelativePath.toString();
			String moduleRelativeLastSegment = moduleRelativePath.lastSegment();

			for (String path : paths) {
				if (moduleRelativePathValue.equals(path) || moduleRelativeLastSegment.equals(path)) {
					return true;
				}
			}

			boolean childContains = containsMember(currentChildren[j], paths);

			if (childContains) {
				return true;
			}
		}

		return false;
	}

	public static IFile findServiceJarForContext(String context) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot root = workspace.getRoot();

		IProject[] projects = root.getProjects();

		for (IProject project : projects) {
			String projectName = project.getName();

			if (projectName.equals(context)) {
				IWebProject lrproject = LiferayCore.create(IWebProject.class, project);

				if (lrproject != null) {
					IResource resource = lrproject.findDocrootResource(
						new Path("WEB-INF/lib/" + project.getName() + "-service.jar"));

					if (FileUtil.exists(resource)) {
						return (IFile)resource;
					}
				}
			}
		}

		return null;
	}

	public static IFolder[] getSourceContainers(IProject project) {
		List<IFolder> sourceFolders = new ArrayList<>();

		IPackageFragmentRoot[] sources = _getSources(project);

		for (IPackageFragmentRoot source : sources) {
			if (source.getResource() instanceof IFolder) {
				sourceFolders.add((IFolder)source.getResource());
			}
		}

		return sourceFolders.toArray(new IFolder[0]);
	}

	public static boolean hasLiferayFacet(IProject project) {
		boolean retval = false;

		if (project == null) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);

			if (facetedProject != null) {
				for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
					IProjectFacet projectFacet = facet.getProjectFacet();

					String projectFacetId = projectFacet.getId();

					if (projectFacetId.startsWith("liferay")) {
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

	public static void validateFile(IFile file, IProgressMonitor monitor) {
		try {
			ValidationRunner.validate(file, ValType.Manual, monitor, false);
		}
		catch (CoreException ce) {
			LiferayServerCore.logError("Error while validating file: " + file.getFullPath(), ce);
		}
	}

	public static void validateFolder(IFolder folder, IProgressMonitor monitor) {
		try {
			Map<IProject, Set<IResource>> projects = new HashMap<>();

			Set<IResource> resources = new HashSet<>();

			folder.accept(
				new IResourceVisitor() {

					public boolean visit(IResource resource) throws CoreException {
						if (resource instanceof IFile || resource instanceof IFile) {
							resources.add(resource);
						}

						return true;
					}

				});

			projects.put(folder.getProject(), resources);

			ValidationRunner.validate(projects, ValType.Manual, monitor, false);
		}
		catch (CoreException ce) {
			LiferayServerCore.logError("Error while validating folder: " + folder.getFullPath(), ce);
		}
	}

	private static IPackageFragmentRoot[] _getSources(IProject project) {
		IJavaProject jProject = JavaCore.create(project);

		if (jProject == null) {
			return new IPackageFragmentRoot[0];
		}

		List<IPackageFragmentRoot> list = new ArrayList<>();
		IVirtualComponent vc = ComponentCore.createComponent(project);
		IPackageFragmentRoot[] roots;

		try {
			roots = jProject.getPackageFragmentRoots();

			for (IPackageFragmentRoot root : roots) {
				if (root.getKind() != IPackageFragmentRoot.K_SOURCE) {
					continue;
				}

				IResource resource = root.getResource();

				if (null != resource) {
					IVirtualResource[] vResources = ComponentCore.createResources(resource);
					boolean found = false;

					for (int j = 0; !found && (j < vResources.length); j++) {
						IVirtualComponent vResourceComponent = vResources[j].getComponent();

						if (vResourceComponent.equals(vc)) {
							if (!list.contains(root)) {
								list.add(root);
							}

							found = true;
						}
					}
				}
			}

			if (list.isEmpty()) {
				for (IPackageFragmentRoot root : roots) {
					if ((root.getKind() == IPackageFragmentRoot.K_SOURCE) && !list.contains(root)) {
						list.add(root);
					}
				}
			}
		}
		catch (JavaModelException jme) {
			LiferayServerCore.logError(jme);
		}

		return list.toArray(new IPackageFragmentRoot[0]);
	}

}