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

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplatesFactory;

import com.liferay.ide.idea.ui.LiferayIdeaUI;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Joye Luo
 */
public class LiferayWorkspaceTemplatesFactory extends ProjectTemplatesFactory {

	@NotNull
	@Override
	public ProjectTemplate[] createTemplates(@Nullable String group, WizardContext context) {
		return new ProjectTemplate[] {
			new LiferayWorkspaceTemplate(
				"Liferay Gradle Workspace", "Create Liferay Gradle Workspace", new LiferayGradleWorkspaceBuilder()),
			new LiferayWorkspaceTemplate(
				"Liferay Maven Workspace", "Create Liferay Maven Workspace", new LiferayMavenWorkspaceBuilder())
		};
	}

	@Override
	public Icon getGroupIcon(String group) {
		return LiferayIdeaUI.LIFERAY_ICON;
	}

	@NotNull
	@Override
	public String[] getGroups() {
		return new String[] {"Liferay"};
	}

}