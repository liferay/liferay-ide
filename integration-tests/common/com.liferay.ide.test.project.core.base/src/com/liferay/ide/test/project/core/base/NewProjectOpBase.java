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

package com.liferay.ide.test.project.core.base;

import com.liferay.ide.test.core.base.support.ProjectSupport;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

import org.junit.Assert;
import org.junit.Rule;

/**
 * @author Terry Jia
 */
public abstract class NewProjectOpBase<T extends ExecutableElement> extends ProjectBase {

	@Rule
	public ProjectSupport project = new ProjectSupport();

	protected IProject create(T op, String projectName) {
		Status status = op.execute(ProgressMonitorBridge.create(npm));

		Assert.assertNotNull(status);
		Assert.assertTrue(status.message(), status.ok());

		waitForBuildAndValidation();

		return project(projectName);
	}

	protected IProject createAndBuild(T op, String projectName) {
		Status status = op.validation();

		Assert.assertTrue(status.message(), status.ok());

		IProject project = create(op, projectName);

		verifyProject(project);

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, npm);
		}
		catch (CoreException ce) {
			failTest(ce);
		}

		return project;
	}

	protected abstract String provider();

}