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

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xml.search.core.resource.IURIResolver;

public class CSSResourcesQuerySpecification extends AbstractWebResourcesQuerySpecification
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.wst.xml.search.core.resource.IURIResolverProvider#getURIResolver
     * (org.eclipse.core.resources.IFile, java.lang.Object)
     */
    public IURIResolver getURIResolver( IFile file, Object selectedNode )
    {
        return CSSResourceURIResolver.INSTANCE;
    }
}
