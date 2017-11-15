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

package com.liferay.ide.portlet.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.dd.LiferayDisplayDescriptorHelper;
import com.liferay.ide.portlet.core.dd.LiferayPortletDescriptorHelper;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.project.core.AbstractPortletFramework;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Simon Jiang
 * @author Kuo Zhang
 */
public abstract class BasePortletFramework extends AbstractPortletFramework {

	@Override
	public IStatus postProjectCreated(
		IProject project, String frameworkName, String portletName, IProgressMonitor monitor) {

		IStatus status = Status.OK_STATUS;

		if (!CoreUtil.isNullOrEmpty(portletName)) {
			PortletDescriptorHelper portletDH = new PortletDescriptorHelper(project);

			status = portletDH.configurePortletXml(portletName);

			if (!status.isOK()) {
				return status;
			}

			LiferayPortletDescriptorHelper liferayPortletDH = new LiferayPortletDescriptorHelper(project);

			status = liferayPortletDH.configureLiferayPortletXml(portletName);

			if (!status.isOK()) {
				return status;
			}

			LiferayDisplayDescriptorHelper liferayDisplayDH = new LiferayDisplayDescriptorHelper(project);

			status = liferayDisplayDH.configureLiferayDisplayXml(portletName);
		}

		return status;
	}

}