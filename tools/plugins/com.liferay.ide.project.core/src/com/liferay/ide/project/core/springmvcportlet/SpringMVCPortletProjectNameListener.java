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

package com.liferay.ide.project.core.springmvcportlet;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 * @author Seiphon Wang
 */
public class SpringMVCPortletProjectNameListener
	extends FilteredListener<PropertyContentEvent> implements SapphireContentAccessor {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		_updateLocation(op(event));
	}

	protected NewSpringMVCPortletProjectOp op(PropertyContentEvent event) {
		Element element = SapphireUtil.getElement(event);

		return element.nearest(NewSpringMVCPortletProjectOp.class);
	}

	private void _updateLocation(NewSpringMVCPortletProjectOp op) {
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

				if ((gradleModule && hasGradleWorkspace) || (mavenModule && hasMavenWorkspace)) {
					IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

					if (FileUtil.exists(liferayWorkspaceProject)) {
						IPath workspaceLocation = liferayWorkspaceProject.getLocation();

						String[] warsNames = LiferayWorkspaceUtil.getWarsDirs(liferayWorkspaceProject);

						newLocationBase = PathBridge.create(workspaceLocation.append(warsNames[0]));
					}
				}
				else {
					newLocationBase = PathBridge.create(CoreUtil.getWorkspaceRootLocation());
				}
			}

			if (newLocationBase != null) {
				op.setLocation(newLocationBase.append(currentProjectName));
			}
		}
	}

}