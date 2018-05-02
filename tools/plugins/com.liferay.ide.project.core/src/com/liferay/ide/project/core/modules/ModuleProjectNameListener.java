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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 * @author Andy Wu
 * @author Joye Luo
 */
public class ModuleProjectNameListener extends FilteredListener<PropertyContentEvent> {

	public static void updateLocation(NewLiferayModuleProjectOp op) {
		String currentProjectName = op.getProjectName().content(true);

		if ((currentProjectName == null) || CoreUtil.isNullOrEmpty(currentProjectName.trim())) {
			return;
		}

		boolean useDefaultLocation = op.getUseDefaultLocation().content(true);

		if (useDefaultLocation) {
			Path newLocationBase = null;

			boolean hasLiferayWorkspace = false;
			boolean hasGradleWorkspace = false;
			boolean hasMavenWorkspace = false;

			try {
				hasLiferayWorkspace = LiferayWorkspaceUtil.hasWorkspace();
				hasGradleWorkspace = LiferayWorkspaceUtil.hasGradleWorkspace();
				hasMavenWorkspace = LiferayWorkspaceUtil.hasMavenWorkspace();
			}
			catch (Exception e) {
				ProjectCore.logError("Failed to check LiferayWorkspace project.");
			}

			if (!hasLiferayWorkspace) {
				newLocationBase = PathBridge.create(CoreUtil.getWorkspaceRootLocation());
			}
			else {
				boolean gradleModule = false;
				boolean mavenModule = false;

				ILiferayProjectProvider provider = op.getProjectProvider().content();

				if (provider != null) {
					String shortName = provider.getShortName();

					if (!CoreUtil.empty(shortName) && shortName.startsWith("gradle")) {
						gradleModule = true;
					}
					else {
						mavenModule = true;
					}
				}

				boolean themeProject = false;

				if (op instanceof NewLiferayModuleProjectOp) {
					NewLiferayModuleProjectOp moduleProjectOp = (NewLiferayModuleProjectOp)op;

					String projectTemplateName = moduleProjectOp.getProjectTemplateName().content();

					for (String projectType : _WAR_TYPE_PROJECT) {
						if (projectType.equals(projectTemplateName)) {
							themeProject = true;
						}
					}
				}

				if ((gradleModule && hasGradleWorkspace) || (mavenModule && hasMavenWorkspace)) {
					IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

					if (FileUtil.exists(liferayWorkspaceProject)) {
						if (themeProject) {
							String[] warsNames = LiferayWorkspaceUtil.getWarsDirs(liferayWorkspaceProject);

							// use the first configured wars fodle name

							newLocationBase = PathBridge.create(
								liferayWorkspaceProject.getLocation().append(warsNames[0]));
						}
						else {
							String folder = LiferayWorkspaceUtil.getModulesDir(liferayWorkspaceProject);

							if (folder != null) {
								IPath appendPath = liferayWorkspaceProject.getLocation().append(folder);

								newLocationBase = PathBridge.create(appendPath);
							}
						}
					}
				}
				else {
					newLocationBase = PathBridge.create(CoreUtil.getWorkspaceRoot().getLocation());
				}
			}

			if (newLocationBase != null) {
				op.setLocation(newLocationBase);
			}
		}
	}

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		updateLocation(op(event));
	}

	protected NewLiferayModuleProjectOp op(PropertyContentEvent event) {
		Element element = event.property().element();

		return element.nearest(NewLiferayModuleProjectOp.class);
	}

	private static final String[] _WAR_TYPE_PROJECT =
		{"layout-template", "spring-mvc-portlet", "theme", "war-hook", "war-mvc-portlet"};

}