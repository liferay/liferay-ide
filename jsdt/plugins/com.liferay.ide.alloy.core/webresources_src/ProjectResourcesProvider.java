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

package com.liferay.ide.alloy.core.webresources_src;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesProvider;

/**
 * @author Gregory Amerson
 */
public class ProjectResourcesProvider implements IWebResourcesProvider {

	@Override
	public IResource[] getResources(IWebResourcesContext context, IProgressMonitor monitor) {
		IResource[] retval = null;

		IFile htmlFile = context.getHtmlFile();

		if ((htmlFile != null) && ProjectUtil.isPortletProject(htmlFile.getProject())) {
			retval = new IResource[] {CoreUtil.getDefaultDocrootFolder(htmlFile.getProject())};
		}

		return retval;
	}

}