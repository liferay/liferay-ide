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

package com.liferay.ide.xml.search.ui.resources;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.queryspecifications.container.IResourceProvider;
import org.eclipse.wst.xml.search.core.resource.IResourceRequestor;
import org.eclipse.wst.xml.search.core.resource.IResourceRequestorProvider;
import org.eclipse.wst.xml.search.core.resource.IURIResolver;
import org.eclipse.wst.xml.search.core.resource.IURIResolverProvider;

/**
 * @author Kuo Zhang
 */
public class JSPDirectoryQuerySpecification
	implements IResourceProvider, IResourceRequestorProvider, IURIResolverProvider {

	@Override
	public IResourceRequestor getRequestor() {
		return JSPDirectoryResourcesRequestor.INSTANCE;
	}

	@Override
	public IResource getResource(Object selectedNode, IResource resource) {
		return CoreUtil.getDefaultDocrootFolder(resource.getProject());
	}

	@Override
	public IURIResolver getURIResolver(IFile file, Object selectedNode) {
		return JSPDirectoryURIResolver.INSTANCE;
	}

}