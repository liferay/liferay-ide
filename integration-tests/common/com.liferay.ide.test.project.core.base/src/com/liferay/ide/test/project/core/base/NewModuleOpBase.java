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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.ExecutableElement;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
public abstract class NewModuleOpBase<T extends ExecutableElement> extends NewProjectOpBase<T> {

	protected void verifyProject(IProject project) {
		super.verifyProject(project);

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

		Assert.assertNotNull(bundleProject);

		IPath outputBundle;

		try {
			outputBundle = bundleProject.getOutputBundle(true, npm);

			assertFileExists(outputBundle);

			assertFileSuffix(outputBundle, shape());
		}
		catch (CoreException e) {
			failTest(e);
		}
	}

	protected abstract String shape();

}