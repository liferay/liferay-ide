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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Gregory Amerson
 */
public class RemoteSourcePathComputerDelegate implements ISourcePathComputerDelegate {

	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor)
		throws CoreException {

		IServer server = ServerUtil.getServer(configuration);

		SourcePathComputerVisitor visitor = new SourcePathComputerVisitor(configuration);

		IModule[] modules = server.getModules();

		for (IModule module : modules) {
			ModuleTraverser.traverse(module, visitor, monitor);
		}

		return visitor.getSourceContainers();
	}

	public class SourcePathComputerVisitor implements IModuleVisitor {

		public SourcePathComputerVisitor(ILaunchConfiguration configuration) {
			this.configuration = configuration;
		}

		public void endVisitEarComponent(IVirtualComponent component) throws CoreException {

			// do nothing

		}

		public void endVisitWebComponent(IVirtualComponent component) throws CoreException {

			// do nothing

		}

		public ISourceContainer[] getSourceContainers() throws CoreException {
			Collections.addAll(runtimeClasspath, JavaRuntime.computeUnresolvedSourceLookupPath(configuration));

			IRuntimeClasspathEntry[] entries = runtimeClasspath.toArray(
				new IRuntimeClasspathEntry[runtimeClasspath.size()]);

			IRuntimeClasspathEntry[] resolved = JavaRuntime.resolveSourceLookupPath(entries, configuration);

			return JavaRuntime.getSourceContainers(resolved);
		}

		public void visitArchiveComponent(IPath runtimePath, IPath workspacePath) {

			// do nothing

		}

		public void visitClasspathEntry(IPath rtFolder, IClasspathEntry entry) {

			// do nothing

		}

		public void visitDependentComponent(IPath runtimePath, IPath workspacePath) {

			// do nothing

		}

		public void visitDependentContentResource(IPath runtimePath, IPath workspacePath) {

			// do nothing

		}

		public void visitDependentJavaProject(IJavaProject javaProject) {

			/**
			 * Ensure dependent projects are listed directly in the classpath
			 * list.
			 * This is needed because JavaRuntime.getSourceContainers() won't
			 * resolve them
			 * correctly if they have non-default output folders. In this case,
			 * they resolve to
			 * binary archive folders with no associated source folder for some
			 * reason.
			 */
			runtimeClasspath.add(JavaRuntime.newDefaultProjectClasspathEntry(javaProject));
		}

		public void visitEarResource(IPath runtimePath, IPath workspacePath) {

			// do nothing

		}

		public void visitWebComponent(IVirtualComponent component) throws CoreException {
			IProject project = component.getProject();

			if (project.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject javaProject = JavaCore.create(project);

				runtimeClasspath.add(JavaRuntime.newDefaultProjectClasspathEntry(javaProject));
			}
		}

		public void visitWebResource(IPath runtimePath, IPath workspacePath) {

			// do nothing

		}

		public ILaunchConfiguration configuration;
		public List<IRuntimeClasspathEntry> runtimeClasspath = new ArrayList<>();

	}

}