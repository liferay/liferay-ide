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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;

import java.util.Objects;

import org.apache.commons.configuration.PropertiesConfiguration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Simon Jiang
 */
public class ConfigureWorkspaceProductOpMethods {

	public static final Status execute(
		ConfigureWorkspaceProductOp configureProductOp, ProgressMonitor progressMonitor) {

		try {
			final String productKey = _getter.get(configureProductOp.getProductVersion());

			IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			if (Objects.nonNull(workspaceProject)) {
				IPath worpacePath = workspaceProject.getLocation();

				IPath gradlePropertiesPath = worpacePath.append("gradle.properties");

				if (FileUtil.notExists(gradlePropertiesPath.toFile())) {
					return Status.createOkStatus();
				}

				_writePropertyValue(
					gradlePropertiesPath.toFile(), WorkspaceConstants.WORKSPACE_PRODUCT_PROPERTY, productKey);

				IFile gradlePropertiesIFile = workspaceProject.getFile(gradlePropertiesPath);

				SafeRunner.run(
					new ISafeRunnable() {

						@Override
						public void run() throws Exception {
							gradlePropertiesIFile.refreshLocal(IResource.DEPTH_INFINITE, null);
						}

					});
			}
		}
		catch (Exception e) {
			ProjectCore.logError("Failed to update workspace product information.", e);
		}

		return Status.createOkStatus();
	}

	private static void _writePropertyValue(File propertyFile, String key, String value) throws Exception {
		PropertiesConfiguration config = new PropertiesConfiguration(propertyFile);

		config.setProperty(key, value);

		config.save();
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};

}