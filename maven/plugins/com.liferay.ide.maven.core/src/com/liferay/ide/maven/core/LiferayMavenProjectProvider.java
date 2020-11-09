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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.maven.core.aether.AetherUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.descriptor.UpdateDescriptorVersionOperation;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.core.model.ProfileLocation;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.server.util.ComponentUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Model;
import org.apache.maven.settings.Activation;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 * @author Terry Jia
 * @author Seiphon Wang
 */
@SuppressWarnings("restriction")
public class LiferayMavenProjectProvider extends AbstractLiferayProjectProvider implements SapphireContentAccessor {

	public LiferayMavenProjectProvider() {
		super(new Class<?>[] {IProject.class});
	}

	@Override
	public <T> List<T> getData(String key, Class<T> type, Object... params) {
		List<T> retval = null;

		if (key.equals("profileIds")) {
			List<T> profileIds = new ArrayList<>();

			try {
				IMaven maven = MavenPlugin.getMaven();

				Settings settings = maven.getSettings();

				List<Profile> profiles = settings.getProfiles();

				for (Profile profile : profiles) {
					Activation activation = profile.getActivation();

					if ((activation != null) && activation.isActiveByDefault()) {
						continue;
					}

					profileIds.add(type.cast(profile.getId()));
				}

				if ((params[0] != null) && (params[0] instanceof File)) {
					File locationDir = (File)params[0];

					File pomFile = new File(locationDir, IMavenConstants.POM_FILE_NAME);

					if (FileUtil.notExists(pomFile) && FileUtil.exists(locationDir.getParentFile())) {

						// try one level up for when user is adding new module

						pomFile = new File(locationDir.getParentFile(), IMavenConstants.POM_FILE_NAME);
					}

					if (FileUtil.exists(pomFile)) {
						Model model = maven.readModel(pomFile);

						File parentDir = pomFile.getParentFile();

						while (model != null) {
							for (org.apache.maven.model.Profile p : model.getProfiles()) {
								profileIds.add(type.cast(p.getId()));
							}

							parentDir = parentDir.getParentFile();

							if (FileUtil.exists(parentDir)) {
								try {
									model = maven.readModel(new File(parentDir, IMavenConstants.POM_FILE_NAME));
								}
								catch (Exception e) {
									model = null;
								}
							}
						}
					}
				}
			}
			catch (CoreException ce) {
				LiferayMavenCore.logError(ce);
			}

			retval = profileIds;
		}
		else if (key.equals("liferayVersions")) {
			List<T> possibleVersions = new ArrayList<>();

			RepositorySystem system = AetherUtil.newRepositorySystem();

			RepositorySystemSession session = AetherUtil.newRepositorySystemSession(system);

			String groupId = params[0].toString();
			String artifactId = params[1].toString();

			String coords = groupId + ":" + artifactId + ":[6,)";

			Artifact artifact = new DefaultArtifact(coords);

			VersionRangeRequest rangeRequest = new VersionRangeRequest();

			rangeRequest.setArtifact(artifact);
			rangeRequest.addRepository(AetherUtil.newCentralRepository());
			rangeRequest.addRepository(AetherUtil.newLiferayRepository());

			try {
				VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);

				List<Version> versions = rangeResult.getVersions();

				for (Version version : versions) {
					String val = version.toString();

					if (!val.equals("6.2.0") && !val.contains("7.0.0")) {
						possibleVersions.add(type.cast(val));
					}
				}

				retval = possibleVersions;
			}
			catch (VersionRangeResolutionException vrre) {
			}
		}
		else if (key.equals("parentVersion")) {
			List<T> version = new ArrayList<>();

			File locationDir = (File)params[0];

			File parentPom = new File(locationDir, IMavenConstants.POM_FILE_NAME);

			if (FileUtil.exists(parentPom)) {
				try {
					IMaven maven = MavenPlugin.getMaven();

					Model model = maven.readModel(parentPom);

					version.add(type.cast(model.getVersion()));

					retval = version;
				}
				catch (CoreException ce) {
					LiferayMavenCore.logError("unable to get parent version", ce);
				}
			}
		}
		else if (key.equals("parentGroupId")) {
			List<T> groupId = new ArrayList<>();

			File locationDir = (File)params[0];

			File parentPom = new File(locationDir, IMavenConstants.POM_FILE_NAME);

			if (FileUtil.exists(parentPom)) {
				try {
					IMaven maven = MavenPlugin.getMaven();

					Model model = maven.readModel(parentPom);

					groupId.add(type.cast(model.getGroupId()));

					retval = groupId;
				}
				catch (CoreException ce) {
					LiferayMavenCore.logError("unable to get parent groupId", ce);
				}
			}
		}
		else if (key.equals("archetypeGAV")) {
			String frameworkType = (String)params[0];

			String value = LiferayMavenCore.getPreferenceString("archetype-gav-" + frameworkType, "");

			retval = Collections.singletonList(type.cast(value));
		}

		return retval;
	}

	@Override
	public ILiferayProject provide(Class<?> type, Object adaptable) {
		if (adaptable instanceof IProject) {
			IProject project = (IProject)adaptable;

			try {
				if (MavenUtil.isMavenProject(project)) {
					boolean hasLiferayNature = LiferayNature.hasNature(project);
					boolean hasLiferayFacet = ComponentUtil.hasLiferayFacet(project);

					if ((hasLiferayNature ||
						 MavenUtil.hasDependency(project, "com.liferay.portal", "com.liferay.portal.kernel") ||
						 MavenUtil.hasDependency(project, "com.liferay.faces", "com.liferay.faces.bridge.ext") ||
						 MavenUtil.hasDependency(project, "com.liferay.portal", "release.dxp.api") ||
						 MavenUtil.hasDependency(project, "com.liferay.portal", "release.portal.api")) &&
						hasLiferayFacet && ((type == null) || type.isAssignableFrom(FacetedMavenBundleProject.class))) {

						return new FacetedMavenBundleProject(project);
					}
					else if (hasLiferayFacet && type.isAssignableFrom(FacetedMavenProject.class)) {
						return new FacetedMavenProject(project);
					}
					else if (hasLiferayNature && type.isAssignableFrom(MavenBundlePluginProject.class)) {
						return new MavenBundlePluginProject(project);
					}
					else if (type.isAssignableFrom(LiferayMavenProject.class)) {

						// return dummy maven project that can't lookup docroot resources

						return new LiferayMavenProject(project) {

							@Override
							public IFolder[] getSourceFolders() {
								return null;
							}

						};
					}
				}
			}
			catch (CoreException ce) {
				LiferayMavenCore.logError(
					"Unable to create ILiferayProject from maven project " + project.getName(), ce);
			}
		}

		return null;
	}

	protected String getNewLiferayProfilesPluginVersion(
		String[] activeProfiles, List<NewLiferayProfile> newLiferayProfiles, String archetypeVersion) {

		org.osgi.framework.Version minVersion = new org.osgi.framework.Version(archetypeVersion.substring(0, 3));

		try {
			IMaven maven = MavenPlugin.getMaven();

			Settings settings = maven.getSettings();

			List<Profile> profiles = settings.getProfiles();

			org.osgi.framework.Version minNewVersion = new org.osgi.framework.Version(archetypeVersion.substring(0, 3));

			org.osgi.framework.Version minExistedVersion = new org.osgi.framework.Version(
				archetypeVersion.substring(0, 3));

			for (String activeProfile : activeProfiles) {
				for (NewLiferayProfile newProfile : newLiferayProfiles) {
					if (StringUtil.equals(activeProfile, get(newProfile.getId()))) {
						String liferayVersion = get(newProfile.getLiferayVersion());

						org.osgi.framework.Version shortLiferayVersion = new org.osgi.framework.Version(
							liferayVersion.substring(0, 3));

						org.osgi.framework.Version shortPluginVersion = new org.osgi.framework.Version(
							archetypeVersion.substring(0, 3));

						if (shortLiferayVersion.compareTo(shortPluginVersion) < 0) {
							minNewVersion = shortLiferayVersion;
						}
						else {
							minNewVersion = shortPluginVersion;
						}
					}
				}

				minVersion = (minVersion.compareTo(minNewVersion) < 0) ? minVersion : minNewVersion;

				for (Profile existProfile : profiles) {
					if (activeProfile.equals(existProfile.getId())) {
						Properties properties = existProfile.getProperties();

						String liferayVersion = properties.getProperty("liferay.version");
						String pluginVersion = properties.getProperty("liferay.maven.plugin.version");

						if ((pluginVersion != null) && (liferayVersion != null)) {
							org.osgi.framework.Version shortLiferayVersion = new org.osgi.framework.Version(
								liferayVersion.substring(0, 3));

							org.osgi.framework.Version shortPluginVersion = new org.osgi.framework.Version(
								pluginVersion.substring(0, 3));

							if (shortLiferayVersion.compareTo(shortPluginVersion) < 0) {
								minExistedVersion = shortLiferayVersion;
							}
						}
					}
				}

				minVersion = (minVersion.compareTo(minExistedVersion) < 0) ? minVersion : minExistedVersion;
			}
		}
		catch (Exception e) {
		}

		return minVersion.toString();
	}

	protected List<NewLiferayProfile> getNewProfilesToSave(
		String[] activeProfiles, List<NewLiferayProfile> newLiferayProfiles, ProfileLocation location) {

		List<NewLiferayProfile> profilesToSave = new ArrayList<>();

		for (String activeProfile : activeProfiles) {
			for (NewLiferayProfile newProfile : newLiferayProfiles) {
				if (StringUtil.equals(activeProfile, get(newProfile.getId())) &&
					location.equals(get(newProfile.getProfileLocation()))) {

					profilesToSave.add(newProfile);
				}
			}
		}

		return profilesToSave;
	}

	protected void updateDtdVersion(IProject project, String dtdVersion, String archetypeVesion) {
		String tmpPublicId = dtdVersion;
		String tmpSystemId = dtdVersion.replaceAll("\\.", "_");

		IStructuredModel editModel = null;

		IFile[] metaFiles = _getLiferayMetaFiles(project);

		for (IFile file : metaFiles) {
			try {
				IModelManager modelManager = StructuredModelManager.getModelManager();

				editModel = modelManager.getModelForEdit(file);

				if ((editModel != null) && (editModel instanceof IDOMModel)) {
					IDOMModel editDoModel = (IDOMModel)editModel;

					IDOMDocument xmlDocument = editDoModel.getDocument();

					DocumentTypeImpl docType = (DocumentTypeImpl)xmlDocument.getDoctype();

					String newPublicId = _getNewDoctTypeSetting(docType.getPublicId(), tmpPublicId, _publicidPattern);

					if (newPublicId != null) {
						docType.setPublicId(newPublicId);
					}

					String newSystemId = _getNewDoctTypeSetting(docType.getSystemId(), tmpSystemId, _systemidPattern);

					if (newSystemId != null) {
						docType.setSystemId(newSystemId);
					}

					editModel.save();
				}
			}
			catch (Exception e) {
				IStatus error = ProjectCore.createErrorStatus(
					"Unable to upgrade deployment meta file for " + file.getName(), e);

				ProjectCore.logError(error);
			}
			finally {
				if (editModel != null) {
					editModel.releaseFromEdit();
				}
			}
		}

		ProjectCore.operate(project, UpdateDescriptorVersionOperation.class, archetypeVesion, dtdVersion);
	}

	private IFile[] _getLiferayMetaFiles(IProject project) {
		List<IFile> files = new ArrayList<>();

		for (String name : _FILE_NAMES) {
			files.addAll(new SearchFilesVisitor().searchFiles(project, name));
		}

		return files.toArray(new IFile[0]);
	}

	private String _getNewDoctTypeSetting(String doctypeSetting, String newValue, Pattern p) {
		String newDoctTypeSetting = null;

		Matcher m = p.matcher(doctypeSetting);

		if (m.find()) {
			String oldVersionString = m.group(m.groupCount());

			newDoctTypeSetting = doctypeSetting.replace(oldVersionString, newValue);
		}

		return newDoctTypeSetting;
	}

	private static final String[] _FILE_NAMES = {
		"liferay-portlet.xml", "liferay-display.xml", "service.xml", "liferay-hook.xml", "liferay-layout-templates.xml",
		"liferay-look-and-feel.xml", "liferay-portlet-ext.xml"
	};

	private static final Pattern _publicidPattern = Pattern.compile(
		"-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)",
		Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern _systemidPattern = Pattern.compile(
		"^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd",
		Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

}