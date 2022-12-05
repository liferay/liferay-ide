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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourcePathComputer;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PortalSourcePathComputerDelegate extends JavaSourcePathComputer {

	public static final String ID = "com.liferay.ide.server.core.portal.sourcePathComputer";

	@Override
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor)
		throws CoreException {

		List<ISourceContainer> sourceContainers = new ArrayList<>();

		IServer server = ServerUtil.getServer(configuration);

		PortalServerBehavior behavior = (PortalServerBehavior)server.loadAdapter(PortalServerBehavior.class, monitor);

		Set<IProject> watchProjects = behavior.getWatchProjects();

		Stream<IProject> stream = watchProjects.stream();

		stream.map(
			project -> JavaCore.create(project)
		).filter(
			javaProject -> javaProject != null
		).forEach(
			javaProject -> _addSourceContainers(configuration, monitor, sourceContainers, javaProject.getProject())
		);

		Stream.of(
			CoreUtil.getAllProjects()
		).map(
			project -> LiferayCore.create(IWorkspaceProject.class, project)
		).filter(
			workspaceProject -> workspaceProject != null
		).map(
			workspaceProject -> CoreUtil.getJavaProjects(workspaceProject.getChildProjects())
		).flatMap(
			Collection::stream
		).forEach(
			childProject -> _addSourceContainers(configuration, monitor, sourceContainers, childProject)
		);

		IWorkspaceProject workspaceProject = LiferayCore.create(IWorkspaceProject.class, server);

		if (workspaceProject != null) {
			IProject project = workspaceProject.getProject();

			try {
				IJavaProject javaProject = JavaCore.create(project);

				if ((javaProject != null) && javaProject.isOpen()) {
					_addSourceContainers(configuration, monitor, sourceContainers, workspaceProject.getProject());
				}
			}
			catch (Exception e) {
			}
		}

		Stream.of(
			server.getModules()
		).map(
			IModule::getProject
		).filter(
			Objects::nonNull
		).map(
			project -> LiferayCore.create(ILiferayProject.class, project)
		).filter(
			Objects::nonNull
		).forEach(
			liferayProject -> _addSourceContainers(
				configuration, monitor, sourceContainers, liferayProject.getProject())
		);

		_addMarketplaceWebSurceContainers(configuration, sourceContainers);

		return sourceContainers.toArray(new ISourceContainer[0]);
	}

	@Override
	public String getId() {
		return ID;
	}

	private void _addMarketplaceWebSurceContainers(
		ILaunchConfiguration configuration, List<ISourceContainer> sourceContainers) {

		try {
			IServer server = ServerUtil.getServer(configuration);

			IRuntime runtime = server.getRuntime();

			PortalBundle portalBundle = LiferayServerCore.newPortalBundle(runtime.getLocation());

			File[] marketplactLPkgFiles = _getMarketplaceLpkgFiles(portalBundle);

			if (Objects.isNull(marketplactLPkgFiles)) {
				return;
			}

			PortalRuntime portalRuntime = (PortalRuntime)runtime.loadAdapter(
				PortalRuntime.class, new NullProgressMonitor());

			Set<IRuntimeClasspathEntry> runtimeClasspathSet = new CopyOnWriteArraySet<>();

			LiferayServerCore serverCore = LiferayServerCore.getDefault();

			IPath serverCoreLocation = serverCore.getStateLocation();

			IPath marketPlaceFolder = serverCoreLocation.append(portalRuntime.getPortalVersion());

			Stream.of(
				marketplactLPkgFiles
			).parallel(
			).forEach(
				file -> _loadSourceJar(file, marketPlaceFolder, runtimeClasspathSet)
			);

			IRuntimeClasspathEntry[] entries = runtimeClasspathSet.toArray(new IRuntimeClasspathEntry[0]);

			IRuntimeClasspathEntry[] resolved = JavaRuntime.resolveSourceLookupPath(entries, configuration);

			ISourceContainer[] marketSourceContainers = JavaRuntime.getSourceContainers(resolved);

			Stream.of(
				marketSourceContainers
			).filter(
				computedSourceContainer -> !sourceContainers.contains(computedSourceContainer)
			).forEach(
				sourceConntainer -> sourceContainers.add(sourceConntainer)
			);
		}
		catch (Exception exception) {
		}
	}

	private void _addSourceContainers(
		ILaunchConfiguration configuration, IProgressMonitor monitor, List<ISourceContainer> sourceContainers,
		IProject project) {

		if (FileUtil.notExists(project)) {
			return;
		}

		if (!project.isOpen()) {
			return;
		}

		IJavaProject javaProject = JavaCore.create(project);

		if ((javaProject == null) || !javaProject.isOpen()) {
			return;
		}

		String projectName = project.getName();

		try {
			DebugPlugin debugPlugin = DebugPlugin.getDefault();

			ILaunchManager manager = debugPlugin.getLaunchManager();

			ILaunchConfigurationType launchConfigurationType = manager.getLaunchConfigurationType(
				"org.eclipse.jdt.launching.localJavaApplication");

			ILaunchConfigurationWorkingCopy sourceLookupConfig = launchConfigurationType.newInstance(
				null, configuration.getName());

			sourceLookupConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);

			ISourceContainer[] computedSourceContainers = super.computeSourceContainers(sourceLookupConfig, monitor);

			Stream.of(
				computedSourceContainers
			).filter(
				computedSourceContainer -> !sourceContainers.contains(computedSourceContainer)
			).forEach(
				sourceContainers::add
			);
		}
		catch (CoreException ce) {
			LiferayServerCore.logError("Unable to add source container for project " + projectName, ce);
		}
	}

	private File[] _getMarketplaceLpkgFiles(PortalBundle portalBundle) {
		IPath osgiBundlesDir = portalBundle.getOSGiBundlesDir();

		IPath marketplacePath = osgiBundlesDir.append("marketplace");

		File marketplace = marketplacePath.toFile();

		return marketplace.listFiles(
			new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.matches(".*\\.lpkg");
				}

			});
	}

	private void _loadSourceJar(File file, IPath marketPlaceFolder, Set<IRuntimeClasspathEntry> runtimeClasspathSet) {
		try (JarFile jar = new JarFile(file)) {
			Enumeration<JarEntry> jarEntryEnumeration = jar.entries();

			while (jarEntryEnumeration.hasMoreElements()) {
				JarEntry entry = jarEntryEnumeration.nextElement();

				String entryName = entry.getName();

				if (entryName.endsWith(".jar")) {
					try (InputStream inputStream = jar.getInputStream(entry)) {
						IPath jarFilePath = marketPlaceFolder.append(entryName);

						if (FileUtil.exists(jarFilePath)) {
							runtimeClasspathSet.add(JavaRuntime.newArchiveRuntimeClasspathEntry(jarFilePath));

							continue;
						}

						FileUtil.writeFile(jarFilePath.toFile(), inputStream);

						runtimeClasspathSet.add(JavaRuntime.newArchiveRuntimeClasspathEntry(jarFilePath));
					}
				}
			}
		}
		catch (Exception e) {
		}
	}

}