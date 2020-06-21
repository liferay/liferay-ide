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
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.util.Objects;

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

				boolean warProject = false;

				if (op instanceof NewLiferayModuleProjectOp) {
					NewLiferayModuleProjectOp moduleProjectOp = op;

					String projectTemplateName = get(moduleProjectOp.getProjectTemplateName());

					for (String projectType : _WAR_TYPE_PROJECT) {
						if (projectType.equals(projectTemplateName)) {
							warProject = true;
						}
					}
				}

				if ((gradleModule && hasGradleWorkspace) || (mavenModule && hasMavenWorkspace)) {
					IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

					if (FileUtil.exists(workspaceProject)) {
						IPath workspaceLocation = workspaceProject.getLocation();

						NewLiferayModuleProjectOp moduleProjectOp = op;

						String projectTemplateName = get(moduleProjectOp.getProjectTemplateName());

						if (warProject) {
							IWorkspaceProject liferayWorkspaceProject =
								LiferayWorkspaceUtil.getLiferayWorkspaceProject();
							String[] defaultWarDirs = null;

							if (gradleModule) {
								defaultWarDirs = liferayWorkspaceProject.getWorkspaceWarDirs();
							}

							if (Objects.nonNull(defaultWarDirs)) {
								newLocationBase = PathBridge.create(workspaceLocation.append(defaultWarDirs[0]));
							}
							else {
								newLocationBase = PathBridge.create(workspaceLocation);
							}
						}
						else if ("war-core-ext".equals(projectTemplateName)) {
							String extName = LiferayWorkspaceUtil.getExtDir(workspaceProject);

							newLocationBase = PathBridge.create(workspaceLocation.append(extName));
						}
						else {
							String folder = LiferayWorkspaceUtil.getModulesDir(workspaceProject);

							if (!StringUtil.equals(folder, "*")) {
								newLocationBase = PathBridge.create(workspaceLocation.append(folder));
							}
							else {
								newLocationBase = PathBridge.create(workspaceLocation);
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

	private static final String[] _WAR_TYPE_PROJECT = {"layout-template", "theme", "war-hook", "war-mvc-portlet"};

}