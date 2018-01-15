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

package com.liferay.ide.portlet.core.spring;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.portlet.core.BasePortletFramework;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;

/**
 * @author Terry Jia
 */
public class SpringPortletFramework extends BasePortletFramework {

	public IStatus configureNewProject(IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject) {
		return Status.OK_STATUS;
	}

	public boolean supports(ILiferayProjectProvider provider) {
		if ((provider != null) && ("ant".equals(provider.getShortName()) || "maven".equals(provider.getShortName()))) {
			return true;
		}

		return false;
	}

}