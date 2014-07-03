/*******************************************************************************
 * Copyright (c) 2011 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.xml.search.core.resources;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.queryspecifications.container.IResourceProvider;
import org.eclipse.wst.xml.search.core.resource.IResourceRequestor;
import org.eclipse.wst.xml.search.core.resource.IResourceRequestorProvider;
import org.eclipse.wst.xml.search.core.resource.IURIResolverProvider;

public abstract class AbstractWebResourcesQuerySpecification
    implements IResourceProvider, IResourceRequestorProvider, IURIResolverProvider
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.wst.xml.search.core.resource.IResourceRequestorProvider# getRequestor()
     */
    public IResourceRequestor getRequestor()
    {
        return WebContentResourcesRequestor.INSTANCE;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.wst.xml.search.core.queryspecifications.container.
     * IResourceProvider#getResource(java.lang.Object, org.eclipse.core.resources.IResource)
     */
    public IResource getResource( Object selectedNode, IResource resource )
    {
        return resource.getParent().getParent();
    }

}
