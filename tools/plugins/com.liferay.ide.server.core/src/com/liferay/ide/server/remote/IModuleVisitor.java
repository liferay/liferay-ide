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

package com.liferay.ide.server.remote;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * @author Simon Jiang
 */
public interface IModuleVisitor {

	/**
	 * Post process EAR resource.
	 *
	 * @param component
	 *            EAR component to process
	 * @throws CoreException
	 */
	public void endVisitEarComponent(IVirtualComponent component) throws CoreException;

	/**
	 * Post process web component
	 *
	 * @param component
	 *            web component to process
	 * @throws CoreException
	 */
	public void endVisitWebComponent(IVirtualComponent component) throws CoreException;

	/**
	 * Process archive component.
	 *
	 * @param runtimePath
	 *            path for component at runtime
	 * @param workspacePath
	 *            path to component in workspace
	 */
	public void visitArchiveComponent(IPath runtimePath, IPath workspacePath);

	/**
	 * Process dependent component.
	 *
	 * @param runtimePath
	 *            path for component at runtime
	 * @param workspacePath
	 *            path to component in workspace
	 */
	public void visitDependentComponent(IPath runtimePath, IPath workspacePath);

	/**
	 * Process a content resource from dependent component.
	 *
	 * @param runtimePath
	 *            path for resource at runtime
	 * @param workspacePath
	 *            path to resource in workspace
	 */
	public void visitDependentContentResource(IPath runtimePath, IPath workspacePath);

	/**
	 * Process Dependent Java project. Useful for determining source paths.
	 *
	 * @param IJavaProject
	 *            dependent Java project
	 */
	public void visitDependentJavaProject(IJavaProject javaProject);

	/**
	 * Process EAR resource.
	 *
	 * @param runtimePath
	 *            path for resource at runtime
	 * @param workspacePath
	 *            path to resource in workspace
	 */
	public void visitEarResource(IPath runtimePath, IPath workspacePath);

	/**
	 * Process web component
	 *
	 * @param component
	 *            web component to process
	 * @throws CoreException
	 */
	public void visitWebComponent(IVirtualComponent component) throws CoreException;

	/**
	 * Process web resource.
	 *
	 * @param runtimePath
	 *            path for resource at runtime
	 * @param workspacePath
	 *            path to resource in workspace
	 */
	public void visitWebResource(IPath runtimePath, IPath workspacePath);

}