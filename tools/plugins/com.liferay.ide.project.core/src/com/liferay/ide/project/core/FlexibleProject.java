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

import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IResourceBundleProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.common.jdt.internal.javalite.JavaCoreLite;
import org.eclipse.jst.common.jdt.internal.javalite.JavaLiteUtilities;
import org.eclipse.jst.j2ee.componentcore.J2EEModuleVirtualComponent;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

/**
 * @author Gregory Amerson
 */
public abstract class FlexibleProject extends BaseLiferayProject implements IResourceBundleProject, IWebProject {

	public FlexibleProject(IProject project) {
		super(project);
	}

	@Override
	public IResource findDocrootResource(IPath path) {
		IVirtualFolder docroot = _getVirtualDocroot(getProject());

		if (docroot == null) {
			return null;
		}

		IVirtualResource virtualResource = docroot.findMember(path);

		if ((virtualResource == null) || !virtualResource.exists()) {
			return null;
		}

		for (IResource resource : virtualResource.getUnderlyingResources()) {
			if (FileUtil.exists(resource) && resource instanceof IFile) {
				return resource;
			}
		}

		return null;
	}

	@Override
	public IFolder getDefaultDocrootFolder() {
		return _getDefaultDocroot(getProject());
	}

	@Override
	public List<IFile> getDefaultLanguageProperties() {
		return PropertiesUtil.getDefaultLanguagePropertiesFromPortletXml(
			getDescriptorFile(ILiferayConstants.PORTLET_XML_FILE));
	}

	@Override
	public IFile getDescriptorFile(String name) {
		IFile retval = null;

		IFolder defaultDocrootFolder = getDefaultDocrootFolder();

		if (FileUtil.exists(defaultDocrootFolder)) {
			retval = defaultDocrootFolder.getFile(
				new Path(
					"WEB-INF"
				).append(
					name
				));
		}

		if (retval != null) {
			return retval;
		}

		// fallback to looping through all virtual folders

		IVirtualFolder webappRoot = _getVirtualDocroot(getProject());

		if (webappRoot == null) {
			return retval;
		}

		for (IContainer container : webappRoot.getUnderlyingFolders()) {
			if (FileUtil.exists(container)) {
				IFile descriptorFile = container.getFile(
					new Path(
						"WEB-INF"
					).append(
						name
					));

				if (descriptorFile.exists()) {
					retval = descriptorFile;

					break;
				}
			}
		}

		return retval;
	}

	@Override
	public IFolder[] getSourceFolders() {
		List<IFolder> retval = new ArrayList<>();

		List<IContainer> sourceFolders = JavaLiteUtilities.getJavaSourceContainers(JavaCoreLite.create(getProject()));

		if (ListUtil.isNotEmpty(sourceFolders)) {
			for (IContainer sourceFolder : sourceFolders) {
				if (sourceFolder instanceof IFolder) {
					retval.add((IFolder)sourceFolder);
				}
			}
		}

		return retval.toArray(new IFolder[retval.size()]);
	}

	public boolean pathInDocroot(IPath path) {
		if (path == null) {
			return false;
		}

		IVirtualFolder webappRoot = _getVirtualDocroot(getProject());

		if (webappRoot == null) {
			return false;
		}

		for (IContainer container : webappRoot.getUnderlyingFolders()) {
			boolean docrootResource = false;

			IPath fullPath = container.getFullPath();

			if (FileUtil.exists(container) && fullPath.isPrefixOf(path)) {
				docrootResource = true;
			}

			if (docrootResource) {
				return true;
			}
		}

		return false;
	}

	private static IFolder _getDefaultDocroot(IProject project) {
		IVirtualFolder webappRoot = _getVirtualDocroot(project);

		if (webappRoot == null) {
			return null;
		}

		try {
			IPath defaultFolder = J2EEModuleVirtualComponent.getDefaultDeploymentDescriptorFolder(webappRoot);

			if (defaultFolder == null) {
				return null;
			}

			IFolder folder = project.getFolder(defaultFolder);

			if (folder.exists()) {
				return folder;
			}
		}
		catch (Exception e) {
			ProjectCore.logError("Could not determine default docroot", e);
		}

		return null;
	}

	private static IVirtualFolder _getVirtualDocroot(IProject project) {
		if ((project == null) || !project.isOpen()) {
			return null;
		}

		IVirtualComponent component = ComponentCore.createComponent(project);

		if (component != null) {
			return component.getRootFolder();
		}

		return null;
	}

}