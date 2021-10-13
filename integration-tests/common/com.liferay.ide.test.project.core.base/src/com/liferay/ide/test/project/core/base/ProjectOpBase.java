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
public abstract class ProjectOpBase<T extends ExecutableElement> extends ProjectBase {

	@Rule
	public ProjectSupport project = new ProjectSupport();

	protected IProject createOrImport(T op, String projectName) {
		Status status = op.execute(ProgressMonitorBridge.create(npm));

		Assert.assertNotNull(status);
		Assert.assertTrue(status.message(), status.ok());

		waitForBuildAndValidation();

		return project(projectName);
	}

	protected IProject createOrImportAndBuild(T op, String projectName) {
		return createOrImportAndBuild(op, projectName, null, true);
	}

	protected IProject createOrImportAndBuild(
		T op, String projectName, String expectedWarningMessage, boolean verifyProject) {

		Status status = op.validation();

		if (expectedWarningMessage != null) {
			Assert.assertFalse(status.message(), status.ok());
			Assert.assertEquals(expectedWarningMessage, status.message());
		}

		IProject project = createOrImport(op, projectName);

		if (verifyProject) {
			verifyProject(project);
		}

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, npm);
		}
		catch (CoreException coreException) {
			failTest(coreException);
		}

		return project;
	}

	protected abstract String provider();

}