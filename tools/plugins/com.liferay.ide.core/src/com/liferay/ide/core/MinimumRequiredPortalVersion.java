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

package com.liferay.ide.core;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import org.osgi.framework.Version;

/**
 * @author Cindy Li
 */
public class MinimumRequiredPortalVersion extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		IProject project = null;

		if (receiver instanceof IProject) {
			project = (IProject)receiver;
		}
		else if (receiver instanceof IFile) {
			IFile fileReceiver = (IFile)receiver;

			project = fileReceiver.getProject();
		}

		try {
			ILiferayProject lProject = LiferayCore.create(ILiferayProject.class, project);

			if ((lProject == null) || (args[0] == null)) {
				return false;
			}

			ILiferayPortal portal = lProject.adapt(ILiferayPortal.class);

			if (portal == null) {
				return false;
			}

			Version version = Version.parseVersion(portal.getVersion());
			Version minimumRequiredPortalVersion = Version.parseVersion((String)args[0]);

			if (CoreUtil.compareVersions(version, minimumRequiredPortalVersion) >= 0) {
				return true;
			}
		}
		catch (Exception e) {
			LiferayCore.logError("Could not determine liferay portal version.", e);
		}

		return false;
	}

}