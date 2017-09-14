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

import com.intellij.openapi.externalSystem.service.settings.AbstractImportFromExternalSystemControl;
import com.intellij.openapi.externalSystem.util.ExternalSystemSettingsControl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.text.StringUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.service.settings.GradleProjectSettingsControl;
import org.jetbrains.plugins.gradle.service.settings.GradleSystemSettingsControl;
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings;
import org.jetbrains.plugins.gradle.settings.GradleSettings;
import org.jetbrains.plugins.gradle.settings.GradleSettingsListener;
import org.jetbrains.plugins.gradle.util.GradleConstants;
import org.jetbrains.plugins.gradle.util.GradleUtil;

/**
 * @author Andy Wu
 */
public class LiferayImportFromGradleControl
	extends AbstractImportFromExternalSystemControl<GradleProjectSettings, GradleSettingsListener, GradleSettings> {

	public LiferayImportFromGradleControl() {
		super(
			GradleConstants.SYSTEM_ID, new GradleSettings(ProjectManager.getInstance().getDefaultProject()),
			_getInitialProjectSettings(), true);
	}

	@Override
	public void setCurrentProject(@Nullable Project currentProject) {
		super.setCurrentProject(currentProject);
		((GradleProjectSettingsControl)getProjectSettingsControl()).setCurrentProject(currentProject);
	}

	@NotNull
	@Override
	protected ExternalSystemSettingsControl<GradleProjectSettings> createProjectSettingsControl(
		@NotNull GradleProjectSettings settings) {

		return new GradleProjectSettingsControl(settings);
	}

	@Nullable
	@Override
	protected ExternalSystemSettingsControl<GradleSettings> createSystemSettingsControl(
		@NotNull GradleSettings settings) {

		return new GradleSystemSettingsControl(settings);
	}

	@Override
	protected void onLinkedProjectPathChange(@NotNull String path) {
		((GradleProjectSettingsControl)getProjectSettingsControl()).update(path, false);
	}

	@NotNull
	private static GradleProjectSettings _getInitialProjectSettings() {
		GradleProjectSettings result = new GradleProjectSettings();

		result.setUseAutoImport(true);

		String gradleHome = GradleUtil.getLastUsedGradleHome();

		if (!StringUtil.isEmpty(gradleHome)) {
			result.setGradleHome(gradleHome);
		}

		return result;
	}

}