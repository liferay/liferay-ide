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

package com.liferay.ide.maven.ui;

import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.maven.core.model.NewLiferayProfileOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.Profile;

import java.net.URL;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractProjectMarkerResolution implements IMarkerResolution2 {

	public String getDescription() {
		return getLabel();
	}

	public Image getImage() {
		LiferayMavenUI plugin = LiferayMavenUI.getDefault();

		Bundle bundle = plugin.getBundle();

		URL url = bundle.getEntry("/icons/e16/m2e-liferay.png");

		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);

		return imageDescriptor.createImage();
	}

	public void run(IMarker marker) {
		IProject project = MarkerUtil.getProject(marker);

		IProjectConfigurationManager projectManager = MavenPlugin.getProjectConfigurationManager();

		ResolverConfiguration configuration = projectManager.getResolverConfiguration(project);

		List<String> currentProfiles = configuration.getActiveProfileList();

		NewLiferayProfileOp op = NewLiferayProfileOp.TYPE.instantiate();

		ElementList<Profile> selectedProfiles = op.getSelectedProfiles();

		for (String currentProfile : currentProfiles) {
			Profile profile = selectedProfiles.insert();

			profile.setId(currentProfile);
		}

		if (promptUser(project, op) != SapphireDialog.OK) {
			return;
		}

		configuration.setSelectedProfiles(SapphireUtil.getContent(op.getActiveProfilesValue()));

		boolean changed = projectManager.setResolverConfiguration(project, configuration);

		if (changed) {
			WorkspaceJob job = new WorkspaceJob("Updating project " + project.getName()) {

				public IStatus runInWorkspace(IProgressMonitor monitor) {
					try {
						projectManager.updateProjectConfiguration(project, monitor);
					}
					catch (CoreException ce) {
						return ce.getStatus();
					}

					return Status.OK_STATUS;
				}

			};

			job.setRule(projectManager.getRule());

			job.schedule();
		}
	}

	protected abstract int promptUser(IProject project, NewLiferayPluginProjectOp op);

}