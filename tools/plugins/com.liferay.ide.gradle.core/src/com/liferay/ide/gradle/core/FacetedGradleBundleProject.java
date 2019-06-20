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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.util.FileUtil;

import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author Andy Wu
 */
public class FacetedGradleBundleProject extends LiferayGradleProject implements IWebProject {

	public FacetedGradleBundleProject(IProject project) {
		super(project);
	}

	@Override
	public IResource findDocrootResource(IPath path) {
		return null;
	}

	@Override
	public String getBundleShape() {
		return "war";
	}

	@Override
	public IFolder getDefaultDocrootFolder() {
		IFolder webAppDir = getProject().getFolder("src/main/webapp");

		if (FileUtil.exists(webAppDir)) {
			return webAppDir;
		}
		else {
			return null;
		}
	}

	@Override
	public IFile getDescriptorFile(String name) {
		IFolder defaultDocrootFolder = getDefaultDocrootFolder();

		if (defaultDocrootFolder != null) {
			IFile file = defaultDocrootFolder.getFile("WEB-INF/" + name);

			if (FileUtil.exists(file)) {
				return file;
			}
		}

		return null;
	}

	@Override
	public IPath getLibraryPath(String filename) {
		IJavaProject javaProject = JavaCore.create(getProject());

		try {
			Optional<IPath> option = Stream.of(
				javaProject.getResolvedClasspath(true)
			).map(
				IClasspathEntry::getPath
			).filter(
				path -> {
					String lastSegment = path.lastSegment();

					if (lastSegment.startsWith(filename) && lastSegment.endsWith(".jar")) {
						return true;
					}

					return false;
				}
			).findFirst();

			if (option.isPresent()) {
				return option.get();
			}
		}
		catch (JavaModelException jme) {
		}

		return null;
	}

}