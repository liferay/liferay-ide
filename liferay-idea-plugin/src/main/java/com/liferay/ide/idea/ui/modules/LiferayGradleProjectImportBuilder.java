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

import com.intellij.externalSystem.JavaProjectData;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.externalSystem.model.DataNode;
import com.intellij.openapi.externalSystem.model.internal.InternalExternalProjectInfo;
import com.intellij.openapi.externalSystem.model.project.ProjectData;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemJdkUtil;
import com.intellij.openapi.externalSystem.service.project.ExternalProjectRefreshCallback;
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager;
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalProjectImportBuilder;
import com.intellij.openapi.externalSystem.service.ui.ExternalProjectDataSelectorDialog;
import com.intellij.openapi.externalSystem.settings.ExternalProjectSettings;
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.java.LanguageLevel;

import icons.GradleIcons;

import java.io.File;

import java.util.List;
import java.util.stream.Stream;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings;
import org.jetbrains.plugins.gradle.settings.GradleSettings;
import org.jetbrains.plugins.gradle.util.GradleBundle;
import org.jetbrains.plugins.gradle.util.GradleConstants;

/**
 * @author Andy Wu
 */
public class LiferayGradleProjectImportBuilder
	extends AbstractExternalProjectImportBuilder<LiferayImportFromGradleControl> {

	public LiferayGradleProjectImportBuilder(@NotNull ProjectDataManager dataManager) {
		super(dataManager, new LiferayImportFromGradleControl(), GradleConstants.SYSTEM_ID);
	}

	@Override
	public Icon getIcon() {
		return GradleIcons.Gradle;
	}

	@NotNull
	@Override
	public String getName() {
		return GradleBundle.message("gradle.name");
	}

	@Override
	public boolean isSuitableSdkType(SdkTypeId sdk) {
		if (sdk == JavaSdk.getInstance()) {
			return true;
		}

		return false;
	}

	@Override
	protected void applyExtraSettings(@NotNull WizardContext context) {
		DataNode<ProjectData> node = getExternalProjectNode();

		if (node == null) {
			return;
		}

		DataNode<JavaProjectData> javaProjectNode = ExternalSystemApiUtil.find(node, JavaProjectData.KEY);

		if (javaProjectNode != null) {
			JavaProjectData data = javaProjectNode.getData();

			context.setCompilerOutputDirectory(data.getCompileOutputPath());

			JavaSdkVersion version = data.getJdkVersion();

			Sdk jdk = _findJdk(version);

			if (jdk != null) {
				context.setProjectJdk(jdk);
			}
		}
	}

	@Override
	protected void beforeCommit(@NotNull DataNode<ProjectData> dataNode, @NotNull Project project) {
		DataNode<JavaProjectData> javaProjectNode = ExternalSystemApiUtil.find(dataNode, JavaProjectData.KEY);

		if (javaProjectNode == null) {
			return;
		}

		JavaProjectData javaProjectData = javaProjectNode.getData();

		LanguageLevel externalLanguageLevel = javaProjectData.getLanguageLevel();

		LanguageLevelProjectExtension languageLevelExtension = LanguageLevelProjectExtension.getInstance(project);

		if (externalLanguageLevel != languageLevelExtension.getLanguageLevel()) {
			languageLevelExtension.setLanguageLevel(externalLanguageLevel);
		}
	}

	@Override
	protected ExternalProjectRefreshCallback createFinalImportCallback(
		@NotNull Project project, @NotNull ExternalProjectSettings projectSettings) {

		return new ExternalProjectRefreshCallback() {

			@Override
			public void onFailure(@NotNull String errorMessage, @Nullable String errorDetails) {
			}

			@Override
			public void onSuccess(@Nullable DataNode<ProjectData> externalProject) {
				if (externalProject == null) {
					return;
				}

				Runnable selectDataTask = () -> {
					ExternalProjectDataSelectorDialog dialog = new ExternalProjectDataSelectorDialog(
						project,
						new InternalExternalProjectInfo(
							GradleConstants.SYSTEM_ID, projectSettings.getExternalProjectPath(), externalProject));

					if (dialog.hasMultipleDataToSelect()) {
						dialog.showAndGet();
					}
					else {
						Disposer.dispose(dialog.getDisposable());
					}
				};

				ProjectDataManager projectDataManager = ServiceManager.getService(ProjectDataManager.class);

				Runnable importTask = () -> projectDataManager.importData(externalProject, project, false);

				GradleSettings gradleProjectSettings = GradleSettings.getInstance(project);

				boolean showSelectiveImportDialog = gradleProjectSettings.showSelectiveImportDialogOnInitialImport();

				Application application = ApplicationManager.getApplication();

				if (showSelectiveImportDialog && !application.isHeadlessEnvironment()) {
					application.invokeLater(
						() -> {
							selectDataTask.run();
							application.executeOnPooledThread(importTask);
						});
				}
				else {
					importTask.run();
				}
			}

		};
	}

	@Override
	protected void doPrepare(@NotNull WizardContext context) {
		String pathToUse = getFileToImport();
		LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

		VirtualFile file = localFileSystem.refreshAndFindFileByPath(pathToUse);

		VirtualFile parent = file.getParent();

		if ((file != null) && !file.isDirectory() && (parent != null)) {
			pathToUse = parent.getPath();
		}

		LiferayImportFromGradleControl importFromGradleControl = getControl(context.getProject());

		importFromGradleControl.setLinkedProjectPath(pathToUse);

		Pair<String, Sdk> sdkPair = ExternalSystemJdkUtil.getAvailableJdk(context.getProject());

		if ((sdkPair != null) && !ExternalSystemJdkUtil.USE_INTERNAL_JAVA.equals(sdkPair.first)) {
			GradleProjectSettings gradleProjectSettings = importFromGradleControl.getProjectSettings();

			gradleProjectSettings.setGradleJvm(sdkPair.first);
		}
	}

	@NotNull
	@Override
	protected File getExternalProjectConfigToUse(@NotNull File file) {
		if (file.isDirectory()) {
			return file;
		}

		return file.getParentFile();
	}

	@Nullable
	private static Sdk _findJdk(@NotNull JavaSdkVersion version) {
		JavaSdk javaSdk = JavaSdk.getInstance();

		ProjectJdkTable projectJdkTable = ProjectJdkTable.getInstance();

		List<Sdk> javaSdks = projectJdkTable.getSdksOfType(javaSdk);

		Stream<Sdk> stream = javaSdks.stream();

		Sdk candidate = stream.filter(
			sdk -> {
				JavaSdkVersion sdkVersion = javaSdk.getVersion(sdk);

				if (version.equals(sdkVersion) ||
					((sdkVersion != null) && version.getMaxLanguageLevel().isAtLeast(version.getMaxLanguageLevel()))) {

					return true;
				}

				return false;
			}
		).findFirst(
		).orElse(
			null
		);

		return candidate;
	}

}