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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.core.util.MultiStatusBuilder;
import com.liferay.ide.project.core.AbstractProjectBuilder;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.MavenExecutionPlan;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.util.xml.Xpp3Dom;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ICallable;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 * @author Charles Wu
 */
@SuppressWarnings("restriction")
public class MavenProjectBuilder extends AbstractProjectBuilder implements IWorkspaceProjectBuilder {

	public MavenProjectBuilder(IProject project) {
		super(project);
	}

	public IStatus buildLang(IFile langFile, IProgressMonitor monitor) throws CoreException {
		IProgressMonitor sub = SubMonitor.convert(monitor, 100);

		sub.beginTask(Msgs.buildingLanguages, 100);

		IMavenProjectFacade facade = MavenUtil.getProjectFacade(getProject(), sub);

		sub.worked(10);

		ICallable<IStatus> callable = new ICallable<IStatus>() {

			public IStatus call(IMavenExecutionContext context, IProgressMonitor monitor) throws CoreException {
				return MavenUtil.executeMojoGoal(
					facade, context, ILiferayMavenConstants.PLUGIN_GOAL_BUILD_LANG, monitor);
			}

		};

		IStatus retval = executeMaven(facade, callable, sub);

		sub.worked(80);

		getProject().refreshLocal(IResource.DEPTH_INFINITE, sub);

		sub.worked(10);
		sub.done();

		return retval;
	}

	public IStatus buildSB(IFile serviceXmlFile, String goal, IProgressMonitor monitor) throws CoreException {
		IProject serviceProject = serviceXmlFile.getProject();

		IMavenProjectFacade facade = MavenUtil.getProjectFacade(serviceProject, monitor);

		monitor.worked(10);

		IStatus retval = null;

		if (runMavenGoal(serviceProject, goal, monitor)) {
			retval = Status.OK_STATUS;
		}
		else {
			retval = LiferayMavenCore.createErrorStatus("run build-service error");
		}

		refreshSiblingProject(facade, monitor);

		serviceProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);

		monitor.worked(10);
		monitor.done();

		return retval;
	}

	@Override
	public IStatus buildService(IProgressMonitor monitor) throws CoreException {
		IFile serviceFile = preBuildService(monitor);

		IProgressMonitor sub = SubMonitor.convert(monitor, 100);

		sub.beginTask(Msgs.buildingServices, 100);

		return buildSB(serviceFile, MavenGoalUtil.getMavenBuildServiceGoal(getProject()), sub);
	}

	@Override
	public IStatus buildWSDD(IProgressMonitor monitor) throws CoreException {
		IFile serviceFile = preBuildService(monitor);

		IProgressMonitor sub = SubMonitor.convert(monitor, 100);

		sub.beginTask(Msgs.buildingServices, 100);

		return buildSB(serviceFile, MavenGoalUtil.getMavenBuildWSDDGoal(getProject()), sub);
	}

	public IStatus execGoals(List<String> goals, IProgressMonitor monitor) throws CoreException {
		IStatus retval = null;

		IMavenProjectFacade facade = MavenUtil.getProjectFacade(getProject(), monitor);

		ICallable<IStatus> callable = new ICallable<IStatus>() {

			public IStatus call(IMavenExecutionContext context, IProgressMonitor monitor) throws CoreException {
				IStatus execStatus = MavenUtil.executeGoals(facade, context, goals, monitor);

				MavenSession session = context.getSession();

				List<Throwable> exceptions = session.getResult().getExceptions();

				MultiStatusBuilder multiStatusBuilder = LiferayMavenCore.newMultiStatus();

				multiStatusBuilder.add(execStatus);
				multiStatusBuilder.addAll(exceptions);

				return multiStatusBuilder.retval();
			}

		};

		retval = executeMaven(facade, callable, monitor);

		return retval;
	}

	public IStatus execJarMojo(IMavenProjectFacade projectFacade, IProgressMonitor monitor) throws CoreException {
		IStatus retval = null;

		ICallable<IStatus> callable = new ICallable<IStatus>() {

			public IStatus call(IMavenExecutionContext context, IProgressMonitor monitor) throws CoreException {
				MavenProject mavenProject = projectFacade.getMavenProject();

				if (mavenProject == null) {
					mavenProject = projectFacade.getMavenProject(monitor);
				}

				IMaven maven = MavenPlugin.getMaven();

				MavenExecutionPlan plan = maven.calculateExecutionPlan(
					mavenProject, Arrays.asList("jar:jar"), true, monitor);

				List<MojoExecution> mojoExecutions = plan.getMojoExecutions();

				if (mojoExecutions != null) {
					for (MojoExecution mojoExecution : mojoExecutions) {
						MavenPlugin.getMaven().execute(mavenProject, mojoExecution, monitor);
					}
				}

				return Status.OK_STATUS;
			}

		};

		retval = executeMaven(projectFacade, callable, monitor);

		return retval;
	}

	public IProject getPortletProject(IMavenProjectFacade projectFacade, IProgressMonitor monitor) {
		IProject retVal = null;

		try {
			Xpp3Dom config = MavenUtil.getLiferayMavenPluginConfig(projectFacade.getMavenProject());

			if (config != null) {
				Xpp3Dom webAppDir = config.getChild(ILiferayMavenConstants.PLUGIN_CONFIG_WEBAPPBASE_DIR);
				Xpp3Dom pluginName = config.getChild(ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_NAME);

				// this should be the name path of a project that should be in user's workspace
				// that we can refresh

				if (webAppDir != null) {
					String webAppDirValue = webAppDir.getValue();

					String projectPath = Path.fromOSString(webAppDirValue).lastSegment();

					IWorkspace workspace = ResourcesPlugin.getWorkspace();

					retVal = workspace.getRoot().getProject(projectPath);
				}
				else if (pluginName != null) {
					String pluginNameValue = pluginName.getValue();

					retVal = CoreUtil.getProject(pluginNameValue);
				}
			}
		}
		catch (Exception e) {
			LiferayMavenCore.logError("Could not refresh sibling service project.", e);
		}

		return retVal;
	}

	public IStatus initBundle(IProject project, String bundleUrl, IProgressMonitor monitor) throws CoreException {
		if (bundleUrl != null) {
			File pomFile = FileUtil.getFile(project.getFile("pom.xml"));

			MavenXpp3Reader mavenReader = new MavenXpp3Reader();
			MavenXpp3Writer mavenWriter = new MavenXpp3Writer();

			try (FileReader reader = new FileReader(pomFile)) {
				Model model = mavenReader.read(reader);

				if (model != null) {
					Build build = model.getBuild();

					Plugin plugin = build.getPluginsAsMap().get("com.liferay:com.liferay.portal.tools.bundle.support");

					if (plugin != null) {
						try (FileWriter fileWriter = new FileWriter(pomFile)) {
							Xpp3Dom origin = (Xpp3Dom)plugin.getConfiguration();
							Xpp3Dom newConfiguration = new Xpp3Dom("configuration");
							Xpp3Dom url = new Xpp3Dom("url");

							url.setValue(bundleUrl);

							newConfiguration.addChild(url);

							plugin.setConfiguration(Xpp3Dom.mergeXpp3Dom(newConfiguration, origin));

							mavenWriter.write(fileWriter, model);
						}
					}
				}
			}
			catch (Exception e) {
				LiferayMavenCore.logError("Could not write file in" + pomFile, e);
			}
		}

		IMavenProjectFacade facade = MavenUtil.getProjectFacade(project, monitor);

		if (_execMavenLaunch(project, MavenGoalUtil.getMavenInitBundleGoal(project), facade, monitor)) {
			return Status.OK_STATUS;
		}

		return LiferayMavenCore.createErrorStatus("run init-bundle error");
	}

	public IFile preBuildService(IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();

		IFile retval = getDocrootFile("WEB-INF/" + ILiferayConstants.SERVICE_XML_FILE);

		if (retval == null) {
			IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade(project);

			if (projectFacade != null) {
				IProject portletProject = getPortletProject(projectFacade, monitor);

				if (portletProject != null) {
					retval = new MavenProjectBuilder(
						portletProject).getDocrootFile("WEB-INF/" + ILiferayConstants.SERVICE_XML_FILE);
				}
			}
		}

		// add support for 7.0 service builder templates

		if (retval == null) {
			retval = project.getFile("service.xml");
		}

		return retval;
	}

	public void refreshSiblingProject(IMavenProjectFacade projectFacade, IProgressMonitor monitor)
		throws CoreException {

		// need to look up project configuration and refresh the *-service project
		// associated with this project

		try {
			Plugin plugin6x = MavenUtil.getPlugin(
				projectFacade, ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY, monitor);

			if (plugin6x != null) {
				Xpp3Dom config = (Xpp3Dom)plugin6x.getConfiguration();

				Xpp3Dom apiBaseDir = config.getChild(ILiferayMavenConstants.PLUGIN_CONFIG_API_BASE_DIR);

				String apiBaseDirValue = apiBaseDir.getValue();

				IWorkspace workspace = ResourcesPlugin.getWorkspace();

				IFile apiBasePomFile = workspace.getRoot().getFileForLocation(
					new Path(apiBaseDirValue).append(IMavenConstants.POM_FILE_NAME));

				IMavenProjectFacade apiBaseFacade = this.projectManager.create(apiBasePomFile, true, monitor);

				apiBaseFacade.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
			else {
				Plugin plugin7x = MavenUtil.getPlugin(
					projectFacade, ILiferayMavenConstants.SERVICE_BUILDER_PLUGIN_KEY, monitor);

				if (plugin7x != null) {
					Xpp3Dom config = (Xpp3Dom)plugin7x.getConfiguration();

					Xpp3Dom apiDirName = config.getChild("apiDirName");

					String apiDirNameValue = apiDirName.getValue();

					int startIndex = apiDirNameValue.indexOf("../");
					int endIndex = apiDirNameValue.indexOf("/src/main/java");

					String projectName = apiDirNameValue.substring(startIndex + 3, endIndex);

					IProject project = CoreUtil.getProject(projectName);

					if (project != null) {
						project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					}
				}
			}
		}
		catch (Exception e) {
			LiferayMavenCore.logError("Could not refresh sibling service project.", e);
		}
	}

	public boolean runMavenGoal(IProject project, String goal, IProgressMonitor monitor) throws CoreException {
		IMavenProjectFacade facade = MavenUtil.getProjectFacade(project, monitor);

		return _execMavenLaunch(project, goal, facade, monitor);
	}

	@Override
	public IStatus updateProjectDependency(IProject project, List<String[]> dependencies) throws CoreException {
		IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade(project, new NullProgressMonitor());

		if (projectFacade != null) {
			MavenProject mavenProject = projectFacade.getMavenProject(new NullProgressMonitor());

			List<Dependency> existedDependencies = mavenProject.getDependencies();

			IMaven maven = MavenPlugin.getMaven();
			File pomFile = new File(project.getLocation().toOSString(), IMavenConstants.POM_FILE_NAME);

			Model model = maven.readModel(pomFile);

			for (String[] dependency : dependencies) {
				Dependency de = new Dependency();

				de.setGroupId(dependency[0]);
				de.setArtifactId(dependency[1]);
				de.setVersion(dependency[2]);
				String newKey = de.getManagementKey();

				boolean existed = false;

				for (Dependency existedDependency : existedDependencies) {
					String existedKey = existedDependency.getManagementKey();

					if (existedKey.equals(newKey)) {
						existed = true;
						break;
					}
				}

				if ((existed == false) && (model != null)) {
					model.addDependency(de);
				}
			}

			try (OutputStream out = Files.newOutputStream(pomFile.toPath())) {
				maven.writeModel(model, out);
				out.flush();
				out.close();

				WorkspaceJob job = new WorkspaceJob("Updating project " + project.getName()) {

					public IStatus runInWorkspace(IProgressMonitor monitor) {
						try {
							project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
							MavenPlugin.getProjectConfigurationManager().updateProjectConfiguration(project, monitor);
						}
						catch (CoreException ce) {
							return ce.getStatus();
						}

						return Status.OK_STATUS;
					}

				};

				job.schedule();
			}
			catch (Exception e) {
				return LiferayMavenCore.createErrorStatus("Error updating maven project dependency", e);
			}
		}

		return Status.OK_STATUS;
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

	protected IMaven maven = MavenPlugin.getMaven();
	protected IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();

	protected static class Msgs extends NLS {

		public static String buildingLanguages;
		public static String buildingServices;
		public static String buildingWSDD;

		static {
			initializeMessages(MavenProjectBuilder.class.getName(), Msgs.class);
		}

	}

	private boolean _execMavenLaunch(
			IProject project, String goal, IMavenProjectFacade facade, IProgressMonitor monitor)
		throws CoreException {

		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(
			_launchConfigurationTypeId);

		IPath basedirLocation = project.getLocation();

		String newName = launchManager.generateLaunchConfigurationName(basedirLocation.lastSegment());

		ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, newName);

		workingCopy.setAttribute(
			IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-Dmaven.multiModuleProjectDirectory");
		workingCopy.setAttribute(_attrPomDir, basedirLocation.toString());
		workingCopy.setAttribute(_attrGoals, goal);

		// workingCopy.setAttribute( ATTR_UPDATE_SNAPSHOTS, Boolean.TRUE );

		workingCopy.setAttribute(_attrWorkspaceResolution, Boolean.TRUE);
		workingCopy.setAttribute(_attrSkipTests, Boolean.TRUE);

		if (facade != null) {
			ResolverConfiguration configuration = facade.getResolverConfiguration();

			String selectedProfiles = configuration.getSelectedProfiles();

			if ((selectedProfiles != null) && (selectedProfiles.length() > 0)) {
				workingCopy.setAttribute(_attrProfiles, selectedProfiles);
			}

			new LaunchHelper().launch(workingCopy, "run", monitor);

			return true;
		}
		else {
			return false;
		}
	}

	private String _attrGoals = "M2_GOALS";
	private String _attrPomDir = IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY;
	private String _attrProfiles = "M2_PROFILES";
	private String _attrSkipTests = "M2_SKIP_TESTS";
	private String _attrWorkspaceResolution = "M2_WORKSPACE_RESOLUTION";
	private String _launchConfigurationTypeId = "org.eclipse.m2e.Maven2LaunchConfigurationType";

}