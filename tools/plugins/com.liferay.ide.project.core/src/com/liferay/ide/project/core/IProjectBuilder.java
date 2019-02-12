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

import com.google.common.collect.ListMultimap;

import com.liferay.ide.core.Artifact;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Gregory Amerson
 */
public interface IProjectBuilder {

	public IStatus buildLang(IFile langFile, IProgressMonitor monitor) throws CoreException;

	public IStatus buildService(IProgressMonitor monitor) throws CoreException;

	public IStatus buildWSDD(IProgressMonitor monitor) throws CoreException;

	/**
	 * @return configuration name -> [Artifact1, Artifact2, Artifact3] ,
	 *  the configuration name could be null
	 */
	public ListMultimap<String, Artifact> getDependencies();

	public IStatus updateProjectDependency(IProject project, List<String[]> dependecies) throws CoreException;

}