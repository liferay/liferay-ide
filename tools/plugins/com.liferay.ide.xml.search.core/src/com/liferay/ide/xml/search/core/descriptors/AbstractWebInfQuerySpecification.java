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

package com.liferay.ide.xml.search.core.descriptors;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.queryspecifications.container.IResourceProvider;
import org.eclipse.wst.xml.search.core.queryspecifications.requestor.IXMLSearchRequestorProvider;

public abstract class AbstractWebInfQuerySpecification implements IResourceProvider, IXMLSearchRequestorProvider
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.wst.xml.search.core.queryspecifications.container.
     * IResourceProvider#getResource(java.lang.Object, org.eclipse.core.resources.IResource)
     */
    public IResource getResource( Object selectedNode, IResource resource )
    {
        // Search WEB-INF folder.
        final IContainer folder = resource.getParent();

        if( "WEB-INF".equals( folder.getName() ) )
        {
            return folder;
        }

        final IFolder webInf = CoreUtil.getDefaultDocrootFolder( resource.getProject() ).getFolder( "WEB-INF" );

        if( webInf.exists() )
        {
            return webInf;
        }

        return null;
    }
}
