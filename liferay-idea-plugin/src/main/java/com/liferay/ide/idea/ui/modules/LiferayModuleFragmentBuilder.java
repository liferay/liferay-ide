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

package com.liferay.ide.idea.ui.modules;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import com.liferay.ide.idea.core.LiferayProjectTypeService;
import com.liferay.ide.idea.ui.LiferayIdeaUI;
import com.liferay.ide.idea.util.BladeCLI;
import com.liferay.ide.idea.util.FileUtil;
import com.liferay.ide.idea.util.LiferayIcons;
import com.liferay.ide.idea.util.SwitchConsumer;
import com.liferay.ide.idea.util.SwitchConsumer.SwitchConsumerBuilder;

import java.io.File;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import java.util.stream.Stream;

import javax.swing.Icon;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class LiferayModuleFragmentBuilder extends ModuleBuilder {

	@Override
	public String getBuilderId() {
		Class<?> clazz = getClass();

		return clazz.getName();
	}

	public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
		return new LiferayModuleFragmentWizardStep(context, this);
	}

	@Override
	public String getDescription() {
		return _LIFERAY_FRAGMENT_MODULES;
	}

	@SuppressWarnings("rawtypes")
	public ModuleType getModuleType() {
		return StdModuleTypes.JAVA;
	}

	@Override
	public Icon getNodeIcon() {
		return LiferayIcons.LIFERAY_ICON;
	}

	@Override
	public String getPresentableName() {
		return _LIFERAY_FRAGMENT_MODULES;
	}

	public void setBsnName(String bsn) {
		_bsn = bsn;
	}

	public void setFragmentHost(String fragmentHost) {
		_fragmentHost = fragmentHost;
	}

	public void setOverrideFiles(String[] overrideFiles) {
		_overrideFiles = overrideFiles;
	}

	@Override
	public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
		VirtualFile projectRoot = _createAndGetContentEntry();

		Project project = rootModel.getProject();

		ProjectType liferayProjectType = LiferayProjectTypeService.getProjectType(project);

		_createProject(projectRoot, liferayProjectType.getId());

		File hostBundle = new File(
			LiferayIdeaUI.USER_BUNDLES_DIR, _fragmentHost.substring(0, _fragmentHost.lastIndexOf(".jar")));

		SwitchConsumerBuilder<File> switch_ = SwitchConsumer.newBuilder();

		SwitchConsumer<File> switchConsumer = switch_.addCase(
			f -> f.getName().equals("portlet.properties"), f -> _copyPortletExtProperties(projectRoot, f)
		).addCase(
			f -> f.getName().contains("default.xml"), f -> _createDefaultExtXmlFile(projectRoot, f)
		).setDefault(
			f -> _copyOtherResource(projectRoot, f)
		).build();

		Stream.of(
			_overrideFiles
		).map(
			overrideFile -> new File(hostBundle, overrideFile)
		).filter(
			file -> file.exists()
		).forEach(
			switchConsumer
		);

		rootModel.addContentEntry(projectRoot);

		if (myJdk != null) {
			rootModel.setSdk(myJdk);
		}
		else {
			rootModel.inheritSdk();
		}
	}

	public void setVersion(String version) {
		_version = version;
	}

	private void _copyOtherResource(VirtualFile projectRoot, File fragmentFile) {
		File parentDir = fragmentFile.getParentFile();

		String parent = parentDir.getPath();

		parent = parent.replaceAll("\\\\", "/");

		String metaInfResources = "META-INF/resources";

		parent = parent.substring(parent.indexOf(metaInfResources) + metaInfResources.length());

		File folder = _getProjectFile(projectRoot, "src/main/resources/META-INF/resources");

		folder.mkdirs();

		if (!parent.equals("resources") && !parent.equals("")) {
			folder = new File(folder, parent);

			folder.mkdirs();
		}

		FileUtil.copyFileToDir(fragmentFile, folder);
	}

	private void _copyPortletExtProperties(VirtualFile projectRoot, File f) {
		File folder = _getProjectFile(projectRoot, "src/main/java");

		FileUtil.copyFileToDir(f, "portlet-ext.properties", folder);
	}

	private VirtualFile _createAndGetContentEntry() {
		String path = FileUtilRt.toSystemIndependentName(getContentEntryPath());

		File file = new File(path);

		file.mkdirs();

		LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

		return localFileSystem.refreshAndFindFileByPath(path);
	}

	private void _createDefaultExtXmlFile(VirtualFile projectRoot, File f) {
		File folder = _getProjectFile(projectRoot, "src/main/resources/resource-actions");

		folder.mkdirs();

		FileUtil.copyFileToDir(f, "default-ext.xml", folder);

		try {
			File extFile = _getProjectFile(projectRoot, "src/main/resources/portlet-ext.properties");

			extFile.createNewFile();

			String extFileContent =
				"resource.actions.configs=resource-actions/default.xml,resource-actions/default-ext.xml";

			FileUtil.writeFile(extFile, extFileContent, null);
		}
		catch (Exception e) {
		}
	}

	private void _createProject(VirtualFile projectRoot, String projectTypeId) {
		StringBuilder sb = new StringBuilder();
		VirtualFile parentDir = projectRoot.getParent();

		sb.append("create ");
		sb.append("-d \"");
		sb.append(parentDir.getPath());
		sb.append("\" ");

		if ((projectTypeId != null) && projectTypeId.equals(LiferayProjectType.LIFERAY_MAVEN_WORKSPACE)) {
			sb.append("-b ");
			sb.append("maven ");
		}

		sb.append("-t fragment ");

		if (!_bsn.equals("")) {
			sb.append("-h ");
			sb.append(_bsn);
			sb.append(" ");
		}

		if (!_version.equals("")) {
			sb.append("-H ");
			sb.append(_version);
			sb.append(" ");
		}

		sb.append("\"");
		sb.append(projectRoot.getName());
		sb.append("\" ");

		BladeCLI.execute(sb.toString());
	}

	private File _getProjectFile(VirtualFile projectRoot, String path) {
		FileSystem fileSystem = FileSystems.getDefault();

		Path finalPath = fileSystem.getPath(projectRoot.getPath(), path);

		return finalPath.toFile();
	}

	private static final String _LIFERAY_FRAGMENT_MODULES = "Liferay Fragment Modules";

	private String _bsn;
	private String _fragmentHost;
	private String[] _overrideFiles;
	private String _version;

}