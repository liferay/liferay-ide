/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package com.liferay.ide.server.remote;

import java.util.ArrayList;
import java.util.Arrays;
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
 * 
 */
public class RemoteSourcePathComputerDelegate implements
		ISourcePathComputerDelegate {

	/**
	 * {@inheritDoc}
	 */
	public ISourceContainer[] computeSourceContainers(
			ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {
		IServer server = ServerUtil.getServer(configuration);

		SourcePathComputerVisitor visitor = new SourcePathComputerVisitor(
				configuration);

		IModule[] modules = server.getModules();
		for (int i = 0; i < modules.length; i++) {
			ModuleTraverser.traverse(modules[i], visitor, monitor);
		}

		return visitor.getSourceContainers();
	}

	class SourcePathComputerVisitor implements IModuleVisitor {

		final ILaunchConfiguration configuration;

		/**
		 * List<IRuntimeClasspathEntry> of unresolved IRuntimeClasspathEntries
		 */
		List<IRuntimeClasspathEntry> runtimeClasspath = new ArrayList<IRuntimeClasspathEntry>();

		SourcePathComputerVisitor(ILaunchConfiguration configuration) {
			this.configuration = configuration;
		}

		/**
		 * {@inheritDoc}
		 */
		public void visitWebComponent(IVirtualComponent component)
				throws CoreException {
			IProject project = component.getProject();
			if (project.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject javaProject = JavaCore.create(project);
				runtimeClasspath.add(JavaRuntime
						.newDefaultProjectClasspathEntry(javaProject));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void endVisitWebComponent(IVirtualComponent component)
				throws CoreException {
			// do nothing
		}

		/**
		 * {@inheritDoc}
		 */
		public void visitArchiveComponent(IPath runtimePath, IPath workspacePath) {
			// do nothing
		}

		/**
		 * {@inheritDoc}
		 */
		public void visitDependentJavaProject(IJavaProject javaProject) {
			// Ensure dependent projects are listed directly in the classpath list.
			// This is needed because JavaRuntime.getSourceContainers() won't resolve them
			// correctly if they have non-default output folders.  In this case, they resolve to
			// binary archive folders with no associated source folder for some reason.
			runtimeClasspath.add(JavaRuntime
					.newDefaultProjectClasspathEntry(javaProject));
		}
		/**
		 * {@inheritDoc}
		 */
		public void visitDependentComponent(IPath runtimePath,
				IPath workspacePath) {
			// do nothing
		}

		/**
		 * {@inheritDoc}
		 */
		public void visitWebResource(IPath runtimePath, IPath workspacePath) {
			// do nothing
		}

		/**
		 * {@inheritDoc}
		 */
		public void visitDependentContentResource(IPath runtimePath, IPath workspacePath) {
			// do nothing
		}

		/**
		 * {@inheritDoc}
		 */
		public void visitEarResource(IPath runtimePath, IPath workspacePath) {
			// do nothing
		}

		/**
		 * {@inheritDoc}
		 */
		public void endVisitEarComponent(IVirtualComponent component)
				throws CoreException {
			// do nothing
		}

		/**
		 * {@inheritDoc}
		 */
		public void visitClasspathEntry(IPath rtFolder, IClasspathEntry entry) {
			// do nothing
		}

		ISourceContainer[] getSourceContainers() throws CoreException {
			runtimeClasspath.addAll(Arrays.asList(JavaRuntime
					.computeUnresolvedSourceLookupPath(configuration)));
			IRuntimeClasspathEntry[] entries = runtimeClasspath
					.toArray(new IRuntimeClasspathEntry[runtimeClasspath.size()]);
			IRuntimeClasspathEntry[] resolved = JavaRuntime
					.resolveSourceLookupPath(entries, configuration);
			return JavaRuntime.getSourceContainers(resolved);
		}

	}
}
