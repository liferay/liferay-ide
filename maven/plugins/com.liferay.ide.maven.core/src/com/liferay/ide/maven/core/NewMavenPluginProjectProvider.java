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
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProfileLocation;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.theme.core.util.ThemeUtil;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.metadata.RequiredProperty;
import org.apache.maven.cli.configuration.SettingsXmlConfigurationProcessor;
import org.apache.maven.model.Model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectImportResult;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class NewMavenPluginProjectProvider
	extends LiferayMavenProjectProvider
	implements MavenProfileCreator, NewLiferayProjectProvider<NewLiferayPluginProjectOp> {

	@Override
	public IStatus createNewProject(NewLiferayPluginProjectOp op, IProgressMonitor monitor) throws CoreException {
		ElementList<ProjectName> projectNames = op.getProjectNames();

		IStatus retval = null;

		IMavenConfiguration mavenConfiguration = MavenPlugin.getMavenConfiguration();

		String groupId = get(op.getGroupId());
		String artifactId = get(op.getProjectName());
		String version = get(op.getArtifactVersion());
		String javaPackage = get(op.getGroupId());
		String activeProfilesValue = get(op.getActiveProfilesValue());
		IPortletFramework portletFramework = get(op.getPortletFramework());
		String frameworkName = NewLiferayPluginProjectOpMethods.getFrameworkName(op);

		IPath location = PathBridge.create(get(op.getLocation()));

		// for location we should use the parent location

		if (StringUtil.equals(location.lastSegment(), artifactId)) {

			// use parent dir since maven archetype will generate new dir under this
			// location

			location = location.removeLastSegments(1);
		}

		String archetypeArtifactId = get(op.getArchetype());

		Archetype archetype = new Archetype();

		String[] gav = archetypeArtifactId.split(":");

		String archetypeVersion = gav[gav.length - 1];

		archetype.setGroupId(gav[0]);
		archetype.setArtifactId(gav[1]);

		archetype.setVersion(archetypeVersion);

		LiferayMavenCore liferayMavenCore = LiferayMavenCore.getDefault();

		LiferayArchetypePlugin archetypePlugin = liferayMavenCore.getArchetypePlugin();

		Map<String, String> properties = new HashMap<>();

		List<RequiredProperty> archProps = archetypePlugin.getRequiredProperties(
			new LiferayMavenArchetype(archetype), monitor);

		if (ListUtil.isNotEmpty(archProps)) {
			for (Object prop : archProps) {
				if (prop instanceof RequiredProperty) {
					RequiredProperty rProp = (RequiredProperty)prop;

					PluginType pluginType = get(op.getPluginType());

					if (pluginType.equals(PluginType.theme)) {
						String key = rProp.getKey();

						if (key.equals("themeParent")) {
							properties.put(key, get(op.getThemeParent()));
						}
						else if (key.equals("themeType")) {
							properties.put(key, ThemeUtil.getTemplateExtension(get(op.getThemeFramework())));
						}
					}
					else {
						properties.put(rProp.getKey(), rProp.getDefaultValue());
					}
				}
			}
		}

		ResolverConfiguration resolverConfig = new ResolverConfiguration();

		resolverConfig.setResolveWorkspaceProjects(false);

		if (!CoreUtil.isNullOrEmpty(activeProfilesValue)) {
			resolverConfig.setSelectedProfiles(activeProfilesValue);
		}

		LiferayArchetypeGenerator generator = archetypePlugin.getGenerator();

		Collection<MavenProjectInfo> newMavenProjects = generator.createArchetypeProjects(
			location, new LiferayMavenArchetype(archetype), groupId, artifactId, version, javaPackage, properties,
			false, monitor);

		for (MavenProjectInfo mavenInfo : newMavenProjects) {
			MavenProjectInfo parentMavenProjectInfo = mavenInfo.getParent();

			if (Objects.nonNull(parentMavenProjectInfo)) {
				continue;
			}

			File pomFile = mavenInfo.getPomFile();

			if (!CoreUtil.isNullOrEmpty(activeProfilesValue)) {
				String[] activeProfiles = activeProfilesValue.split(",");

				// find all profiles that should go in user settings file

				List<NewLiferayProfile> newUserSettingsProfiles = getNewProfilesToSave(
					activeProfiles, op.getNewLiferayProfiles(), ProfileLocation.userSettings);

				if (ListUtil.isNotEmpty(newUserSettingsProfiles)) {
					String userSettingsFile = mavenConfiguration.getUserSettingsFile();

					String userSettingsPath = null;

					if (CoreUtil.isNullOrEmpty(userSettingsFile)) {
						userSettingsPath =
							SettingsXmlConfigurationProcessor.DEFAULT_USER_SETTINGS_FILE.getAbsolutePath();
					}
					else {
						userSettingsPath = userSettingsFile;
					}

					try {

						// backup user's settings.xml file

						File settingsXmlFile = new File(userSettingsPath);

						File backupFile = _getBackupFile(settingsXmlFile);

						FileUtils.copyFile(settingsXmlFile, backupFile);

						DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

						DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

						Document pomDocument = docBuilder.parse(settingsXmlFile.getCanonicalPath());

						for (NewLiferayProfile newProfile : newUserSettingsProfiles) {
							createNewLiferayProfileNode(pomDocument, newProfile);
						}

						TransformerFactory transformerFactory = TransformerFactory.newInstance();

						Transformer transformer = transformerFactory.newTransformer();

						DOMSource source = new DOMSource(pomDocument);

						StreamResult result = new StreamResult(settingsXmlFile);

						transformer.transform(source, result);
					}
					catch (Exception e) {
						LiferayMavenCore.logError("Unable to save new Liferay profile to user settings.xml.", e);
					}
				}

				// find all profiles that should go in the project pom

				List<NewLiferayProfile> newProjectPomProfiles = getNewProfilesToSave(
					activeProfiles, op.getNewLiferayProfiles(), ProfileLocation.projectPom);

				try {
					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

					Document pomDocument = docBuilder.parse(pomFile.getCanonicalPath());

					for (NewLiferayProfile newProfile : newProjectPomProfiles) {
						createNewLiferayProfileNode(pomDocument, newProfile);
					}

					TransformerFactory transformerFactory = TransformerFactory.newInstance();

					Transformer transformer = transformerFactory.newTransformer();

					DOMSource source = new DOMSource(pomDocument);

					StreamResult result = new StreamResult(pomFile);

					transformer.transform(source, result);
				}
				catch (IOException | ParserConfigurationException | SAXException | TransformerException ioe) {
					LiferayMavenCore.logError("Unable to save new Liferay profiles to project pom.", ioe);
				}
			}
		}

		ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration(resolverConfig);

		IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

		List<IMavenProjectImportResult> importProjectResults = projectConfigurationManager.importProjects(
			newMavenProjects, importConfiguration, null, monitor);

		List<IProject> newProjects = new ArrayList<>();

		for (IMavenProjectImportResult result : importProjectResults) {
			IProject importProject = result.getProject();

			if ((importProject != null) && importProject.exists()) {
				newProjects.add(importProject);
			}
		}

		if (ListUtil.isNotEmpty(newProjects)) {
			op.setImportProjectStatus(true);

			for (IProject project : newProjects) {
				ProjectName projectName = projectNames.insert();

				projectName.setName(project.getName());
			}
		}

		if (ListUtil.isEmpty(newProjects)) {
			retval = LiferayMavenCore.createErrorStatus("New project was not created due to unknown error");
		}
		else {
			IProject firstProject = newProjects.get(0);

			// add new profiles if it was specified to add to project or parent poms

			if (!CoreUtil.isNullOrEmpty(activeProfilesValue)) {
				String[] activeProfiles = activeProfilesValue.split(",");

				String pluginVersion = getNewLiferayProfilesPluginVersion(
					activeProfiles, op.getNewLiferayProfiles(), archetypeVersion);

				String archVersion = MavenUtil.getMajorMinorVersionOnly(archetypeVersion);

				updateDtdVersion(firstProject, pluginVersion, archVersion);
			}

			PluginType pluginType = get(op.getPluginType());

			if (pluginType.equals(PluginType.portlet)) {
				String portletName = get(op.getPortletName(), false);

				retval = portletFramework.postProjectCreated(firstProject, frameworkName, portletName, monitor);
			}
		}

		if (retval == null) {
			retval = Status.OK_STATUS;
		}

		return retval;
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		IStatus retval = Status.OK_STATUS;

		/**
		 * if the path is a folder and it has a pom.xml that is a package type of 'pom'
		 * then this is a valid location if projectName is null or empty , don't need to check
		 * just return
		 */
		if (CoreUtil.isNullOrEmpty(projectName)) {
			return retval;
		}

		File dir = path.toFile();

		if (FileUtil.notExists(dir)) {
			return retval;
		}

		File pomFile = FileUtil.getFile(path.append(IMavenConstants.POM_FILE_NAME));

		if (FileUtil.exists(pomFile)) {
			MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();

			try {
				Model result = mavenModelManager.readMavenModel(pomFile);

				if (!Objects.equals("pom", result.getPackaging())) {
					retval = LiferayMavenCore.createErrorStatus(
						"\"" + pomFile.getParent() + "\" contains a non-parent maven project.");
				}
				else {
					if (projectName.equals(result.getName())) {
						retval = LiferayMavenCore.createErrorStatus(
							"The project name \"" + projectName + "\" can not be the same as the parent.");
					}
					else {
						IPath newProjectPath = path.append(projectName);

						retval = validateProjectLocation(projectName, newProjectPath);
					}
				}
			}
			catch (CoreException ce) {
				retval = LiferayMavenCore.createErrorStatus("Invalid project location.", ce);

				LiferayMavenCore.log(retval);
			}
		}
		else {
			if (ListUtil.isNotEmpty(dir.listFiles())) {
				retval = LiferayMavenCore.createErrorStatus("Project location is not empty or a parent pom.");
			}
		}

		return retval;
	}

	private File _getBackupFile(File file) {
		Calendar calendar = Calendar.getInstance();

		String suffix = new SimpleDateFormat(
			"yyyyMMddhhmmss"
		).format(
			calendar.getTime()
		);

		return new File(file.getParentFile(), file.getName() + "." + suffix);
	}

}