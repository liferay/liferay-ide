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
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringUtil;
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
public class ModuleProjectNameListener
	extends FilteredListener<PropertyContentEvent> implements SapphireContentAccessor {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		_updateLocation(op(event));
	}

	protected NewLiferayModuleProjectOp op(PropertyContentEvent event) {
		Element element = SapphireUtil.getElement(event);

		return element.nearest(NewLiferayModuleProjectOp.class);
	}

	private void _updateLocation(NewLiferayModuleProjectOp op) {
		String currentProjectName = get(op.getProjectName());

		if (CoreUtil.isNullOrEmpty(currentProjectName)) {
			return;
		}

		boolean useDefaultLocation = get(op.getUseDefaultLocation());

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

				ILiferayProjectProvider provider = get(op.getProjectProvider());

				if (provider != null) {
					String shortName = provider.getShortName();

					if (StringUtil.startsWith(shortName, "gradle")) {
						gradleModule = true;
					}
					else {
						mavenModule = true;
					}
				}

				boolean themeProject = false;

				if (op instanceof NewLiferayModuleProjectOp) {
					NewLiferayModuleProjectOp moduleProjectOp = op;

					String projectTemplateName = get(moduleProjectOp.getProjectTemplateName());

					for (String projectType : _WAR_TYPE_PROJECT) {
						if (projectType.equals(projectTemplateName)) {
							themeProject = true;
						}
					}
				}

				if ((gradleModule && hasGradleWorkspace) || (mavenModule && hasMavenWorkspace)) {
					IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

					if (FileUtil.exists(liferayWorkspaceProject)) {
						IPath workspaceLocation = liferayWorkspaceProject.getLocation();

						NewLiferayModuleProjectOp moduleProjectOp = op;

						String projectTemplateName = get(moduleProjectOp.getProjectTemplateName());

						if (themeProject) {
							String[] warsNames = LiferayWorkspaceUtil.getWarsDirs(liferayWorkspaceProject);

							// use the first configured wars fodle name

							newLocationBase = PathBridge.create(workspaceLocation.append(warsNames[0]));
						}
						else if ("war-core-ext".equals(projectTemplateName)) {
							String extName = LiferayWorkspaceUtil.getExtDir(liferayWorkspaceProject);

							newLocationBase = PathBridge.create(workspaceLocation.append(extName));
						}
						else {
							String folder = LiferayWorkspaceUtil.getModulesDir(liferayWorkspaceProject);

							if (folder != null) {
								newLocationBase = PathBridge.create(workspaceLocation.append(folder));
							}
						}
					}
				}
				else {
					newLocationBase = PathBridge.create(CoreUtil.getWorkspaceRootLocation());
				}
			}

			if (newLocationBase != null) {
				op.setLocation(newLocationBase);
			}
		}
	}

	private static final String[] _WAR_TYPE_PROJECT = {
		"layout-template", "spring-mvc-portlet", "theme", "war-hook", "war-mvc-portlet"
	};

}