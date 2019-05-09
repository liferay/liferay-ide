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

package com.liferay.ide.core;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public interface IProjectBuilder {

	public IStatus buildLang(IFile langFile, IProgressMonitor monitor) throws CoreException;

	public IStatus buildService(IProgressMonitor monitor) throws CoreException;

	public IStatus buildWSDD(IProgressMonitor monitor) throws CoreException;

	public List<Artifact> getDependencies(String configration);

	public IStatus updateDependencies(IProject project, List<Artifact> dependencies) throws CoreException;

}