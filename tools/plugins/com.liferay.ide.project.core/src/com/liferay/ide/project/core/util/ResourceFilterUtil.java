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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Andy Wu
 */
public class ResourceFilterUtil {

	public static void addResourceFilter(IFolder folder, String filteredSubFolderName, IProgressMonitor monitor) {
		String pre = "1.0-name-matches-true-false-";

		boolean shouldAdd = true;

		IResourceFilterDescription[] resourceFilterDescriptions = null;

		try {
			resourceFilterDescriptions = folder.getFilters();
		}
		catch (CoreException ce) {
		}

		if (resourceFilterDescriptions != null) {
			for (IResourceFilterDescription resourceFilterDescription : resourceFilterDescriptions) {
				FileInfoMatcherDescription description = resourceFilterDescription.getFileInfoMatcherDescription();

				Object object = description.getArguments();

				String argument = object.toString();

				String projectName = argument.substring(argument.indexOf(pre) + pre.length(), argument.length());

				if (projectName.equals(filteredSubFolderName)) {
					shouldAdd = false;

					break;
				}
			}
		}

		if (shouldAdd) {
			try {
				FileInfoMatcherDescription fmd = new FileInfoMatcherDescription(
					"org.eclipse.ui.ide.multiFilter", pre + filteredSubFolderName);

				folder.createFilter(10, fmd, IResource.BACKGROUND_REFRESH, monitor);
			}
			catch (CoreException ce) {
				ProjectCore.logError("add " + filteredSubFolderName + " filter error", ce);
			}
		}
	}

	public static void deleteResourceFilter(IFolder parentFolder, String filteredSubFolderName) {
		try {
			IResourceFilterDescription[] resourceFilterDescriptions = parentFolder.getFilters();

			for (IResourceFilterDescription resourceFilterDescription : resourceFilterDescriptions) {
				FileInfoMatcherDescription description = resourceFilterDescription.getFileInfoMatcherDescription();

				Object argument = description.getArguments();

				if (StringUtil.contains(argument.toString(), filteredSubFolderName)) {

					// need to make deleting in a job to avoid Resource Lock Exception

					Job job = new WorkspaceJob("delete project resource filter") {

						@Override
						public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
							try {
								resourceFilterDescription.delete(IResource.BACKGROUND_REFRESH, monitor);
							}
							catch (Exception e) {
							}

							return Status.OK_STATUS;
						}

					};

					job.schedule();
				}
			}
		}
		catch (CoreException ce) {
			ProjectCore.logError("delete filter error", ce);
		}
	}

}