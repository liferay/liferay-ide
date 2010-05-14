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

package com.liferay.ide.eclipse.portlet.core.util;

import com.liferay.ide.eclipse.core.util.CoreUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PortletUtil {

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

	public static IFolder getFirstSrcFolder(IProject project) {
		IPackageFragmentRoot[] sourceFolders = J2EEProjectUtilities.getSourceContainers(project);

		if (sourceFolders != null && sourceFolders.length > 0) {
			IResource resource = sourceFolders[0].getResource();

			return resource instanceof IFolder ? (IFolder) resource : null;
		}

		return null;
	}

	public static IFolder getFirstSrcFolder(String projectName) {
		IProject project = CoreUtil.getProject(projectName);

		return getFirstSrcFolder(project);
	}

}
