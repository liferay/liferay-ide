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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.MavenExecutionPlan;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ICallable;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;

/**
 * @author Gregory Amerson
 */
public abstract class ThemePluginBuildParticipant extends AbstractBuildParticipant {

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
		IMavenProjectFacade facade = getMavenProjectFacade();

		if (!shouldBuild(kind, facade)) {
			return null;
		}

		ICallable<IStatus> callable = new ICallable<IStatus>() {

			public IStatus call(IMavenExecutionContext context, IProgressMonitor monitor) throws CoreException {
				return executeThemeMojo(facade, context, monitor);
			}

		};

		IStatus retval = null;

		try {
			retval = executeMaven(facade, callable, monitor);
		}
		catch (Exception e) {
			retval = LiferayMavenCore.createErrorStatus(getGoal() + " build error", e);
		}

		if ((retval != null) && !retval.isOK()) {
			LiferayMavenCore.log(retval);
		}

		try {
			IProject project = facade.getProject();

			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (CoreException ce) {
		}

		monitor.worked(10);

		return null;
	}

	protected void configureExecution(IMavenProjectFacade facade, Xpp3Dom config) {
		IPath m2eLiferayFolder = MavenUtil.getM2eLiferayFolder(facade.getMavenProject(), facade.getProject());

		IPath themeResourcesFolder = m2eLiferayFolder.append(ILiferayMavenConstants.THEME_RESOURCES_FOLDER);

		String targetFolderValue = themeResourcesFolder.toPortableString();

		MavenUtil.setConfigValue(config, ILiferayMavenConstants.PLUGIN_CONFIG_WEBAPP_DIR, targetFolderValue);
	}

	protected IStatus executeMaven(
			IMavenProjectFacade projectFacade, ICallable<IStatus> callable, IProgressMonitor monitor)
		throws CoreException {

		ICallable<IStatus> status = new ICallable<IStatus>() {

			public IStatus call(IMavenExecutionContext context, IProgressMonitor monitor) throws CoreException {
				return projectManager.execute(projectFacade, callable, monitor);
			}

		};

		return this.maven.execute(status, monitor);
	}

	protected IStatus executeThemeMojo(
			IMavenProjectFacade facade, IMavenExecutionContext context, IProgressMonitor monitor)
		throws CoreException {

		IStatus retval = null;

		List<String> goals = Collections.singletonList(getGoal());

		MavenProject mavenProject = facade.getMavenProject(monitor);

		MavenExecutionPlan plan = maven.calculateExecutionPlan(mavenProject, goals, true, monitor);

		monitor.worked(10);

		MojoExecution liferayMojoExecution = MavenUtil.getExecution(
			plan, ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID);

		Xpp3Dom originalConfig = liferayMojoExecution.getConfiguration();

		Xpp3Dom config = Xpp3DomUtils.mergeXpp3Dom(new Xpp3Dom("configuration"), originalConfig);

		configureExecution(facade, config);

		boolean parentHierarchyLoaded = false;

		try {
			parentHierarchyLoaded = MavenUtil.loadParentHierarchy(facade, monitor);

			monitor.worked(10);

			ResolverConfiguration configuration = facade.getResolverConfiguration();

			configuration.setResolveWorkspaceProjects(true);

			liferayMojoExecution.setConfiguration(config);

			maven.execute(mavenProject, liferayMojoExecution, monitor);

			monitor.worked(50);

			MavenSession mavenSession = context.getSession();

			MavenExecutionResult executionResult = mavenSession.getResult();

			List<Throwable> exceptions = executionResult.getExceptions();

			if (exceptions.size() == 1) {
				retval = LiferayMavenCore.createErrorStatus(exceptions.get(0));
			}
			else if (exceptions.size() > 1) {
				List<IStatus> statuses = new ArrayList<>();

				for (Throwable t : exceptions) {
					statuses.add(LiferayMavenCore.createErrorStatus(t));
				}

				retval = LiferayMavenCore.createMultiStatus(IStatus.ERROR, statuses.toArray(new IStatus[0]));
			}

			retval = retval == null ? Status.OK_STATUS : retval;
		}
		catch (CoreException ce) {
			retval = LiferayMavenCore.createErrorStatus(ce);
		}
		finally {
			liferayMojoExecution.setConfiguration(originalConfig);

			if (parentHierarchyLoaded) {
				mavenProject.setParent(null);
			}
		}

		return retval;
	}

	protected abstract String getGoal();

	protected abstract boolean shouldBuild(int kind, IMavenProjectFacade facade);

	protected IMaven maven = MavenPlugin.getMaven();
	protected IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();

}