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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.PropertyKey;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Joye Luo
 * @author Charles Wu
 */
public class LiferayMavenModuleProjectProvider
	extends LiferayMavenProjectProvider
	implements NewLiferayProjectProvider<NewLiferayModuleProjectOp>, SapphireContentAccessor {

	@Override
	public IStatus createNewProject(NewLiferayModuleProjectOp op, IProgressMonitor monitor) throws CoreException {
		IStatus retval = Status.OK_STATUS;

		String projectName = get(op.getProjectName());

		IPath location = PathBridge.create(get(op.getLocation()));

		String className = get(op.getComponentName());

		String liferayVersion = get(op.getLiferayVersion());

		String serviceName = get(op.getServiceName());

		String packageName = get(op.getPackageName());

		String contributorType = get(op.getContributorType());

		ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

		List<String> properties = new ArrayList<>();

		for (PropertyKey propertyKey : propertyKeys) {
			properties.add(get(propertyKey.getName()) + "=" + get(propertyKey.getValue()));
		}

		File targetDir = location.toFile();

		targetDir.mkdirs();

		String projectTemplateName = get(op.getProjectTemplateName());

		StringBuilder sb = new StringBuilder();

		sb.append("create ");
		sb.append("-d \"");
		sb.append(targetDir.getAbsolutePath());
		sb.append("\" ");
		sb.append("-b ");
		sb.append("maven ");
		sb.append("-v ");
		sb.append(liferayVersion);
		sb.append(" ");
		sb.append("-t ");
		sb.append(projectTemplateName);
		sb.append(" ");

		if (className != null) {
			sb.append("-c ");
			sb.append(className);
			sb.append(" ");
		}

		if (serviceName != null) {
			sb.append("-s ");
			sb.append(serviceName);
			sb.append(" ");
		}

		if (packageName != null) {
			sb.append("-p ");
			sb.append(packageName);
			sb.append(" ");
		}

		if (contributorType != null) {
			sb.append("-C ");
			sb.append(contributorType);
			sb.append(" ");
		}

		sb.append("\"");
		sb.append(projectName);
		sb.append("\" ");

		try {
			BladeCLI.execute(sb.toString());

			ElementList<ProjectName> projectNames = op.getProjectNames();

			ProjectName name = projectNames.insert();

			name.setName(projectName);

			if (projectTemplateName.equals("service-builder")) {
				name = projectNames.insert();

				name.setName(projectName + "-api");

				name = projectNames.insert();

				name.setName(projectName + "-service");
			}

			IPath projectLocation = location;

			String lastSegment = location.lastSegment();

			if ((location != null) && (location.segmentCount() > 0)) {
				if (!lastSegment.equals(projectName)) {
					projectLocation = location.append(projectName);
				}
			}

			CoreUtil.openProject(projectName, projectLocation, monitor);

			MavenUtil.updateProjectConfiguration(projectName, projectLocation.toOSString(), monitor);
		}
		catch (Exception e) {
			ProjectCore.logError(e);

			retval = ProjectCore.createErrorStatus("can not create module project.", e);
		}

		return retval;
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		return Status.OK_STATUS;
	}

}