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

package com.liferay.ide.upgrade.plan.ui.importer;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.jobs.JobUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceProjectProvider;

import java.util.stream.Stream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Terry Jia
 */
public abstract class LiferayWorkspaceImporter extends BaseImporter {

	public LiferayWorkspaceImporter(IPath location) {
		super(location);
	}

	@Override
	public void doImport(IProgressMonitor monitor) {
		ILiferayProjectProvider[] providers = LiferayCore.getProviders("workspace");

		ILiferayProjectProvider gradleWorkspacePovider = Stream.of(
			providers
		).filter(
			provider -> StringUtil.contains(provider.getShortName(), getBuildType())
		).findFirst(
		).get();

		((NewLiferayWorkspaceProjectProvider<?>)gradleWorkspacePovider).importProject(location, monitor);

		JobUtil.waitForLiferayProjectJob();

		IJobManager jobManager = Job.getJobManager();

		try {
			jobManager.join("org.eclipse.buildship.core.jobs", monitor);
		}
		catch (OperationCanceledException oce) {
		}
		catch (InterruptedException ie) {
		}
	}

	public abstract String getBuildType();

}