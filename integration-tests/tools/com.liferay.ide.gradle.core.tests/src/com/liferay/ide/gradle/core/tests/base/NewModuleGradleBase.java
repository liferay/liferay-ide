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

package com.liferay.ide.gradle.core.tests.base;

import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.test.project.core.base.NewModuleOpBase;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public abstract class NewModuleGradleBase extends NewModuleOpBase<NewLiferayModuleProjectOp> {

	@Override
	protected void needJobsToBuild(IJobManager manager) throws InterruptedException, OperationCanceledException {
		manager.join(CorePlugin.GRADLE_JOB_FAMILY, new NullProgressMonitor());
		manager.join(GradleCore.JOB_FAMILY_ID, new NullProgressMonitor());
	}

	@Override
	protected String provider() {
		return "gradle-module";
	}

	protected void verifyProjectFiles(String projectName) {
		assertProjectFileExists(projectName, "build.gradle");
		assertProjectFileNotExists(projectName, "pom.xml");
	}

}