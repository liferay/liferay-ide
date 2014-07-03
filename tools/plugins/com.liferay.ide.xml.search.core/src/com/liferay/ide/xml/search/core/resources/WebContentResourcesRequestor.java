/*******************************************************************************
 * Copyright (c) 2013-2014 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.xml.search.core.resources;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.resource.DefaultResourceRequestor;
import org.eclipse.wst.xml.search.core.resource.IResourceRequestor;
import org.eclipse.wst.xml.search.core.resource.IURIResolver;

public class WebContentResourcesRequestor extends DefaultResourceRequestor
{

    public static final IResourceRequestor INSTANCE = new WebContentResourcesRequestor();

    @Override
    public boolean accept(
        Object selectedNode, IResource rootContainer, IFolder folder, IURIResolver resolver, String matching,
        boolean fullMatch )
    {
        if( "WEB-INF".equals( folder.getName() ) )
        {
            return false;
        }

        if( "META-INF".equals( folder.getName() ) )
        {
            return false;
        }

        return super.accept( selectedNode, rootContainer, folder, resolver, matching, fullMatch );
    }
}
