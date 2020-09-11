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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.Event;
import com.liferay.ide.core.EventListener;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.workspace.ProjectChangedEvent;
import com.liferay.ide.core.workspace.ProjectDeletedEvent;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.remote.IRemoteServerPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.jdt.IClasspathManager;
import org.eclipse.m2e.jdt.MavenJdtPlugin;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public abstract class LiferayMavenProject extends BaseLiferayProject implements EventListener, IMavenProject {

	public LiferayMavenProject(IProject project) {
		super(project);

		IPath projectPath = project.getFullPath();

		_importantResources = new IPath[] {projectPath.append("pom.xml")};
	}

	@Override
	public <T> T adapt(Class<T> adapterType) {
		T adapter = super.adapt(adapterType);

		if (adapter != null) {
			return adapter;
		}

		IMavenProjectFacade facade = MavenUtil.getProjectFacade(getProject(), new NullProgressMonitor());

		if (facade != null) {
			if (IProjectBuilder.class.equals(adapterType)) {
				IProjectBuilder projectBuilder = new MavenProjectBuilder(getProject());

				return adapterType.cast(projectBuilder);
			}
			else if (IRemoteServerPublisher.class.equals(adapterType)) {
				IRemoteServerPublisher remoteServerPublisher = new MavenProjectRemoteServerPublisher(getProject());

				return adapterType.cast(remoteServerPublisher);
			}
		}

		return null;
	}

	public IPath getLibraryPath(String filename) {
		IPath[] libs = getUserLibs();

		if (ListUtil.isNotEmpty(libs)) {
			for (IPath lib : libs) {
				String lastSegment = FileUtil.getLastSegment(lib.removeFileExtension());

				if (lastSegment.startsWith(filename)) {
					return lib;
				}
			}
		}

		return null;
	}

	public String getLiferayMavenPluginVersion() {
		String retval = null;

		IMavenProjectRegistry registry = MavenPlugin.getMavenProjectRegistry();

		IMavenProjectFacade projectFacade = registry.getProject(getProject());

		if (projectFacade != null) {
			try {
				NullProgressMonitor npm = new NullProgressMonitor();

				MavenProject mavenProject = projectFacade.getMavenProject(npm);

				if (mavenProject != null) {
					Plugin liferayMavenPlugin = MavenUtil.getPlugin(
						projectFacade, ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY, npm);

					retval = liferayMavenPlugin.getVersion();
				}
			}
			catch (CoreException ce) {
			}
		}

		return retval;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String retval = defaultValue;

		if ((key.equals("theme.type") || key.equals("theme.parent")) && ProjectUtil.isThemeProject(getProject())) {
			IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade(getProject());

			if (projectFacade != null) {
				MavenProject mavenProject = projectFacade.getMavenProject();

				if (key.equals("theme.type")) {
					retval = MavenUtil.getLiferayMavenPluginConfig(
						mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_THEME_TYPE);
				}
				else {
					retval = MavenUtil.getLiferayMavenPluginConfig(
						mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_PARENT_THEME);
				}
			}
		}

		return retval;
	}

	@Override
	public IFolder getSourceFolder(String classification) {
		IFolder retval = super.getSourceFolder(classification);

		List<IFolder> sourceFolders = CoreUtil.getSourceFolders(JavaCore.create(getProject()));

		for (IFolder folder : sourceFolders) {
			if (StringUtil.equals(folder.getName(), classification)) {
				retval = folder;

				break;
			}
		}

		return retval;
	}

	@Override
	public IPath[] getUserLibs() {
		List<IPath> libs = new ArrayList<>();

		MavenJdtPlugin plugin = MavenJdtPlugin.getDefault();

		IClasspathManager buildPathManager = plugin.getBuildpathManager();

		try {
			IClasspathEntry[] classpath = buildPathManager.getClasspath(
				getProject(), IClasspathManager.CLASSPATH_RUNTIME, true, new NullProgressMonitor());

			for (IClasspathEntry entry : classpath) {
				libs.add(entry.getPath());
			}
		}
		catch (CoreException ce) {
			LiferayMavenCore.logError("Unable to get maven classpath.", ce);
		}

		return libs.toArray(new IPath[0]);
	}

	@Override
	public boolean isStale() {
		return _stale;
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof ProjectDeletedEvent) {
			_stale = true;

			return;
		}

		Optional.of(
			event
		).filter(
			e -> !isStale()
		).filter(
			ProjectChangedEvent.class::isInstance
		).map(
			ProjectChangedEvent.class::cast
		).filter(
			projectChangedEvent -> hasResourcesAffected(projectChangedEvent, getProject(), _importantResources)
		).ifPresent(
			e -> _stale = true
		);
	}

	private IPath[] _importantResources;
	private volatile boolean _stale = false;

}