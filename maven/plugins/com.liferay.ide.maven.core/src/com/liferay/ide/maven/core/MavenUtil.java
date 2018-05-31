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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FileReader;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.cli.configuration.SettingsXmlConfigurationProcessor;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.MavenExecutionPlan;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Settings;

import org.codehaus.plexus.util.xml.Xpp3Dom;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.AbstractProjectScanner;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectImportResult;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.LocalProjectScanner;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.wtp.ProjectUtils;
import org.eclipse.m2e.wtp.WarPluginConfiguration;
import org.eclipse.wst.xml.core.internal.provisional.format.NodeFormatter;

import org.osgi.framework.Version;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class MavenUtil {

	public static Node createNewLiferayProfileNode(Document pomDocument, NewLiferayProfile newLiferayProfile) {
		Node newNode = null;

		String liferayVersion = newLiferayProfile.getLiferayVersion().content();

		try {
			String runtimeName = newLiferayProfile.getRuntimeName().content();

			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(ServerUtil.getRuntime(runtimeName));

			Element root = pomDocument.getDocumentElement();

			Element profiles = NodeUtil.findChildElement(root, "profiles");

			if (profiles == null) {
				newNode = profiles = NodeUtil.appendChildElement(root, "profiles");
			}

			Element newProfile = null;

			if (profiles != null) {
				NodeUtil.appendTextNode(profiles, "\n");

				newProfile = NodeUtil.appendChildElement(profiles, "profile");

				NodeUtil.appendTextNode(profiles, "\n");

				if (newNode == null) {
					newNode = newProfile;
				}
			}

			if (newProfile != null) {
				IPath serverDir = liferayRuntime.getAppServerDir();

				IPath autoDeployDir = serverDir.removeLastSegments(1).append("deploy");

				NodeUtil.appendTextNode(newProfile, "\n\t");

				NodeUtil.appendChildElement(newProfile, "id", newLiferayProfile.getId().content());
				NodeUtil.appendTextNode(newProfile, "\n\t");

				Element propertiesElement = NodeUtil.appendChildElement(newProfile, "properties");

				NodeUtil.appendTextNode(newProfile, "\n\t");
				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");
				NodeUtil.appendChildElement(propertiesElement, "liferay.version", liferayVersion);
				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");
				NodeUtil.appendChildElement(propertiesElement, "liferay.maven.plugin.version", liferayVersion);
				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");
				NodeUtil.appendChildElement(propertiesElement, "liferay.auto.deploy.dir", autoDeployDir.toOSString());
				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");
				NodeUtil.appendChildElement(
					propertiesElement, "liferay.app.server.deploy.dir",
					liferayRuntime.getAppServerDeployDir().toOSString());
				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");
				NodeUtil.appendChildElement(
					propertiesElement, "liferay.app.server.lib.global.dir",
					liferayRuntime.getAppServerLibGlobalDir().toOSString());
				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");
				NodeUtil.appendChildElement(
					propertiesElement, "liferay.app.server.portal.dir",
					liferayRuntime.getAppServerPortalDir().toOSString());
				NodeUtil.appendTextNode(propertiesElement, "\n\t");

				NodeFormatter formatter = new NodeFormatter();

				formatter.format(newNode);
			}
		}
		catch (Exception e) {
			LiferayMavenCore.logError("Unable to add new liferay profile.", e);
		}

		return newNode;
	}

	public static IStatus executeGoals(
			IMavenProjectFacade facade, IMavenExecutionContext context, List<String> goals, IProgressMonitor monitor)
		throws CoreException {

		IMaven maven = MavenPlugin.getMaven();
		MavenProject mavenProject = facade.getMavenProject(monitor);

		MavenExecutionPlan plan = maven.calculateExecutionPlan(mavenProject, goals, true, monitor);

		List<MojoExecution> mojos = plan.getMojoExecutions();

		ResolverConfiguration configuration = facade.getResolverConfiguration();

		configuration.setResolveWorkspaceProjects(true);

		for (MojoExecution mojo : mojos) {
			maven.execute(mavenProject, mojo, monitor);
		}

		return Status.OK_STATUS;
	}

	public static IStatus executeMojoGoal(
			IMavenProjectFacade facade, IMavenExecutionContext context, String goal, IProgressMonitor monitor)
		throws CoreException {

		IStatus retval = null;
		IMaven maven = MavenPlugin.getMaven();

		List<String> goals = Collections.singletonList(goal);
		MavenProject mavenProject = facade.getMavenProject(monitor);

		MavenExecutionPlan plan = maven.calculateExecutionPlan(mavenProject, goals, true, monitor);

		Plugin plugin6x = getPlugin(facade, ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY, monitor);

		String executionArtifactId = null;

		if (plugin6x != null) {
			executionArtifactId = ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID;
		}
		else {
			Plugin plugin7x = getPlugin(facade, ILiferayMavenConstants.SERVICE_BUILDER_PLUGIN_KEY, monitor);

			if (plugin7x != null) {
				executionArtifactId = ILiferayMavenConstants.SERVICE_BUILDER_PLUGIN_ARTIFACT_ID;
			}
		}

		MojoExecution liferayMojoExecution = getExecution(plan, executionArtifactId);

		if (liferayMojoExecution != null) {
			ResolverConfiguration configuration = facade.getResolverConfiguration();

			configuration.setResolveWorkspaceProjects(true);

			maven.execute(mavenProject, liferayMojoExecution, monitor);
		}

		MavenSession session = context.getSession();

		List<Throwable> exceptions = session.getResult().getExceptions();

		if (exceptions.size() == 1) {
			retval = LiferayMavenCore.createErrorStatus(exceptions.get(0));
		}
		else if (exceptions.size() > 1) {
			List<IStatus> statues = new ArrayList<>();

			for (Throwable t : exceptions) {
				statues.add(LiferayMavenCore.createErrorStatus(t));
			}

			IStatus firstStatus = statues.get(0);

			retval = new MultiStatus(
				LiferayMavenCore.PLUGIN_ID, IStatus.ERROR, statues.toArray(new IStatus[0]), firstStatus.getMessage(),
				firstStatus.getException());
		}

		if (retval == null) {
			return Status.OK_STATUS;
		}

		return retval;
	}

	public static MojoExecution getExecution(MavenExecutionPlan plan, String artifactId) {
		if (plan != null) {
			for (MojoExecution execution : plan.getMojoExecutions()) {
				if (artifactId.equals(execution.getArtifactId())) {
					return execution;
				}
			}
		}

		return null;
	}

	public static IFolder getGeneratedThemeResourcesFolder(MavenProject mavenProject, IProject project) {
		IPath m2eLiferayFolder = getM2eLiferayFolder(mavenProject, project);

		return project.getFolder(m2eLiferayFolder).getFolder(ILiferayMavenConstants.THEME_RESOURCES_FOLDER);
	}

	public static Xpp3Dom getLiferayMavenPluginConfig(MavenProject mavenProject) {
		Xpp3Dom retval = null;

		if (mavenProject != null) {
			Plugin plugin = mavenProject.getPlugin(ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY);

			if (plugin != null) {
				retval = (Xpp3Dom)plugin.getConfiguration();
			}
		}

		return retval;
	}

	public static String getLiferayMavenPluginConfig(MavenProject mavenProject, String childElement) {
		String retval = null;

		Xpp3Dom liferayMavenPluginConfig = getLiferayMavenPluginConfig(mavenProject);

		if (liferayMavenPluginConfig != null) {
			Xpp3Dom childNode = liferayMavenPluginConfig.getChild(childElement);

			if (childNode != null) {
				retval = childNode.getValue();
			}
		}

		return retval;
	}

	public static String getLiferayMavenPluginType(MavenProject mavenProject) {
		return getLiferayMavenPluginConfig(mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_TYPE);
	}

	public static String getLocalRepositoryDir() {
		String retval = null;

		IMavenConfiguration mavenConfiguration = MavenPlugin.getMavenConfiguration();

		String userSettings = mavenConfiguration.getUserSettingsFile();

		if ((userSettings == null) || (userSettings.length() == 0)) {
			userSettings = SettingsXmlConfigurationProcessor.DEFAULT_USER_SETTINGS_FILE.getAbsolutePath();
		}

		String globalSettings = MavenPlugin.getMavenConfiguration().getGlobalSettingsFile();

		IMaven maven = MavenPlugin.getMaven();

		try {
			Settings settings = maven.buildSettings(globalSettings, userSettings);

			retval = settings.getLocalRepository();
		}
		catch (CoreException ce) {
			LiferayMavenCore.logError("Unable to get local repository dir.", ce);
		}

		if (retval == null) {
			retval = RepositorySystem.defaultUserLocalRepository.getAbsolutePath();
		}

		return retval;
	}

	public static IPath getM2eLiferayFolder(MavenProject mavenProject, IProject project) {
		String buildOutputDir = mavenProject.getBuild().getDirectory();

		String relativeBuildOutputDir = ProjectUtils.getRelativePath(project, buildOutputDir);

		return new Path(relativeBuildOutputDir).append(ILiferayMavenConstants.M2E_LIFERAY_FOLDER);
	}

	public static String getMajorMinorVersionOnly(String version) {
		String retval = null;

		Matcher matcher = _majorMinorVersion.matcher(version);

		if (matcher.find()) {
			try {
				retval = new Version(matcher.group(1)).toString();
			}
			catch (Exception e) {
			}
		}

		return retval;
	}

	public static Plugin getPlugin(IMavenProjectFacade facade, String pluginKey, IProgressMonitor monitor)
		throws CoreException {

		Plugin retval = null;
		boolean loadedParent = false;
		MavenProject mavenProject = facade.getMavenProject(monitor);

		if (mavenProject != null) {
			retval = mavenProject.getPlugin(pluginKey);
		}

		if (retval == null) {

			// look through all parents to find if the plugin has been declared

			MavenProject parent = mavenProject.getParent();

			if (parent == null) {
				try {
					if (loadParentHierarchy(facade, monitor)) {
						loadedParent = true;
					}
				}
				catch (CoreException ce) {
					LiferayMavenCore.logError("Error loading parent hierarchy", ce);
				}
			}

			while ((parent != null) && (retval == null)) {
				retval = parent.getPlugin(pluginKey);

				parent = parent.getParent();
			}
		}

		if (loadedParent) {
			mavenProject.setParent(null);
		}

		return retval;
	}

	public static IMavenProjectFacade getProjectFacade(IProject project) {
		return getProjectFacade(project, new NullProgressMonitor());
	}

	public static IMavenProjectFacade getProjectFacade(IProject project, IProgressMonitor monitor) {
		IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();
		IFile pomResource = project.getFile(IMavenConstants.POM_FILE_NAME);

		if (FileUtil.exists(pomResource)) {
			return projectManager.create(pomResource, true, monitor);
		}

		return null;
	}

	public static String getVersion(String version) {
		String retval = null;

		DefaultArtifactVersion v = new DefaultArtifactVersion(version);

		retval = v.getMajorVersion() + "." + v.getMinorVersion() + "." + v.getIncrementalVersion();

		if ("0.0.0".equals(retval)) {
			retval = v.getQualifier();
		}

		// try to parse as osgi version if it fails then return 0.0.0

		try {
			Version.parseVersion(retval);
		}
		catch (Exception e) {
			retval = "0.0.0";
		}

		return retval;
	}

	public static String getWarSourceDirectory(IMavenProjectFacade facade) {
		String retval = null;

		try {
			MavenProject mavenProject = facade.getMavenProject(new NullProgressMonitor());
			IProject project = facade.getProject();

			retval = new WarPluginConfiguration(mavenProject, project).getWarSourceDirectory();
		}
		catch (CoreException ce) {
			LiferayMavenCore.logError("Unable to get war source directory", ce);
		}

		return retval;
	}

	public static boolean hasDependency(IProject mavenProject, String groupId, String artifactId) {
		MavenXpp3Reader mavenReader = new MavenXpp3Reader();

		IFile pomFile = mavenProject.getFile("pom.xml");

		if (FileUtil.notExists(pomFile)) {
			return false;
		}

		try (FileReader reader = new FileReader(pomFile.getLocation().toFile())) {
			Model model = mavenReader.read(reader);

			if (model != null) {
				List<Dependency> dependencies = model.getDependencies();

				for (Dependency dependency : dependencies) {
					String tempgroutId = dependency.getGroupId();
					String tempartifactId = dependency.getArtifactId();

					if (groupId.equals(tempgroutId) && artifactId.equals(tempartifactId)) {
						return true;
					}
				}
			}
		}
		catch (Exception e) {
		}

		return false;
	}

	public static void importOpenedProject(String projectName, String location, IProgressMonitor monitor)
		throws InterruptedException {

		MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();

		IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

		File root = workspaceRoot.getLocation().toFile();

		AbstractProjectScanner<MavenProjectInfo> scanner = new LocalProjectScanner(
			root, location, false, mavenModelManager);

		scanner.run(monitor);

		List<MavenProjectInfo> projects = scanner.getProjects();

		List<MavenProjectInfo> mavenProjects = new ArrayList<>();

		_findChildMavenProjects(mavenProjects, projects);

		mavenProjects = _filterProjects(mavenProjects);

		ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration();

		IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

		IProject project = CoreUtil.getProject(projectName);

		List<MavenProjectInfo> resultProjects = mavenProjects;

		Job job = new Job("Updating Maven Project") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ResolverConfiguration configuration = new ResolverConfiguration();

					configuration.setResolveWorkspaceProjects(true);
					configuration.setSelectedProfiles("");

					projectConfigurationManager.enableMavenNature(project, configuration, monitor);
					projectConfigurationManager.importProjects(resultProjects, importConfiguration, monitor);
					projectConfigurationManager.updateProjectConfiguration(project, monitor);
				}
				catch (Exception e) {
					return LiferayMavenCore.createErrorStatus("Error Updating project:" + project.getName(), e);
				}

				return Status.OK_STATUS;
			}

		};

		job.schedule();
	}

	public static List<IMavenProjectImportResult> importProject(String location, IProgressMonitor monitor)
		throws CoreException, InterruptedException {

		MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();

		File root = CoreUtil.getWorkspaceRootFile();

		AbstractProjectScanner<MavenProjectInfo> scanner = new LocalProjectScanner(
			root, location, false, mavenModelManager);

		scanner.run(monitor);

		List<MavenProjectInfo> projects = scanner.getProjects();

		List<MavenProjectInfo> mavenProjects = new ArrayList<>();

		_findChildMavenProjects(mavenProjects, projects);

		mavenProjects = _filterProjects(mavenProjects);

		ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration();

		IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

		return projectConfigurationManager.importProjects(mavenProjects, importConfiguration, monitor);
	}

	public static boolean isMavenProject(IProject project) throws CoreException {
		IFile pomFile = project.getFile(IMavenConstants.POM_FILE_NAME);

		if (FileUtil.exists(project) && project.isAccessible() &&
			(project.hasNature(IMavenConstants.NATURE_ID) || FileUtil.exists(pomFile))) {

			return true;
		}

		return false;
	}

	public static boolean isPomFile(IFile pomFile) {
		if (FileUtil.exists(pomFile) && IMavenConstants.POM_FILE_NAME.equals(pomFile.getName()) &&
			pomFile.getParent() instanceof IProject) {

			return true;
		}

		return false;
	}

	public static boolean loadParentHierarchy(IMavenProjectFacade facade, IProgressMonitor monitor)
		throws CoreException {

		boolean loadedParent = false;
		MavenProject mavenProject = facade.getMavenProject(monitor);

		try {
			if ((mavenProject.getModel().getParent() == null) || (mavenProject.getParent() != null)) {

				/*
				 *  If the method is called without error, we can assume the project has been fully loaded
				 *  No need to continue.
				 */
				return false;
			}
		}
		catch (IllegalStateException ise) {

			// The parent can not be loaded properly

		}

		while ((mavenProject != null) && (mavenProject.getModel().getParent() != null)) {
			if (monitor.isCanceled()) {
				break;
			}

			MavenProject parentProject = MavenPlugin.getMaven().resolveParentProject(mavenProject, monitor);

			if (parentProject != null) {
				mavenProject.setParent(parentProject);
				loadedParent = true;
			}

			mavenProject = parentProject;
		}

		return loadedParent;
	}

	public static void setConfigValue(Xpp3Dom configuration, String childName, Object value) {
		Xpp3Dom childNode = configuration.getChild(childName);

		if (childNode == null) {
			childNode = new Xpp3Dom(childName);

			configuration.addChild(childNode);
		}

		childNode.setValue((value == null) ? null : value.toString());
	}

	private static List<MavenProjectInfo> _filterProjects(List<MavenProjectInfo> mavenProjects) {
		List<MavenProjectInfo> result = new ArrayList<>();

		for (MavenProjectInfo info : mavenProjects) {
			if (info != null) {
				File pomFile = info.getPomFile();

				URI mavenuri = pomFile.getParentFile().toURI();

				if (mavenuri.toString().endsWith("/")) {
					try {
						mavenuri = new URI(mavenuri.toString().substring(0, mavenuri.toString().length() - 1));
					}
					catch (URISyntaxException urise) {
					}
				}

				boolean alreadyExists = false;

				for (IProject project : CoreUtil.getAllProjects()) {
					if (FileUtil.exists(project) && project.getLocationURI().equals(mavenuri)) {
						alreadyExists = true;

						break;
					}
				}

				if (!alreadyExists) {
					result.add(info);
				}
			}
		}

		return result;
	}

	private static void _findChildMavenProjects(List<MavenProjectInfo> results, Collection<MavenProjectInfo> infos) {
		for (MavenProjectInfo info : infos) {
			results.add(info);

			Collection<MavenProjectInfo> children = info.getProjects();

			if (ListUtil.isNotEmpty(children)) {
				_findChildMavenProjects(results, children);
			}
		}
	}

	private static final Pattern _majorMinorVersion = Pattern.compile("([0-9]\\.[0-9])\\..*");

}