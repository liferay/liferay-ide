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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.queryspecifications.container.IMultiResourceProvider;
import org.eclipse.wst.xml.search.core.resource.DefaultResourceRequestor;
import org.eclipse.wst.xml.search.core.resource.IResourceRequestor;
import org.eclipse.wst.xml.search.core.resource.IResourceRequestorProvider;
import org.eclipse.wst.xml.search.core.resource.IURIResolver;
import org.eclipse.wst.xml.search.core.resource.IURIResolverProvider;
import org.eclipse.wst.xml.search.editor.util.JdtUtils;

/**
 * @author Kuo Zhang
 */
public class ResourceBundleQuerySpecification
	implements IMultiResourceProvider, IResourceRequestorProvider, IURIResolverProvider {

	public static final String ID = "liferay.webresources.resourcebundle.querySpecification";

	@Override
	public IResourceRequestor getRequestor() {
		return DefaultResourceRequestor.INSTANCE;
	}

	@Override
	public IResource[] getResources(Object selectedNode, IResource resource) {
		return JdtUtils.getJavaProjectSrcFolders(resource.getProject());
	}

	@Override
	public IURIResolver getURIResolver(IFile file, Object selectedNode) {
		return ResourceBundleURIResolver.INSTANCE;
	}

}