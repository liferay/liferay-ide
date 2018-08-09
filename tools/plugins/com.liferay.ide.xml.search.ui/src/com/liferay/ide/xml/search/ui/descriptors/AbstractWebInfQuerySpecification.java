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

package com.liferay.ide.xml.search.ui.descriptors;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.queryspecifications.container.IResourceProvider;
import org.eclipse.wst.xml.search.core.queryspecifications.requestor.IXMLSearchRequestorProvider;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public abstract class AbstractWebInfQuerySpecification implements IResourceProvider, IXMLSearchRequestorProvider {

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.search.core.queryspecifications.container.
	 * IResourceProvider#getResource(Object,
	 * IResource)
	 */
	public IResource getResource(Object selectedNode, IResource resource) {

		// Search WEB-INF folder.

		IContainer folder = resource.getParent();

		if ("WEB-INF".equals(folder.getName())) {
			return folder;
		}

		IFolder docrootfolder = CoreUtil.getDefaultDocrootFolder(resource.getProject());

		if (docrootfolder != null) {
			IFolder webInf = docrootfolder.getFolder("WEB-INF");

			if (webInf.exists()) {
				return webInf;
			}
		}

		return folder;
	}

}