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

package com.liferay.ide.xml.search.ui.resources;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.resource.ResourceBaseURIResolver;

/**
 * Extension of WTP/XML Search resources uri resolver for Liferay to manage css, js, icons used in the descriptor of
 * liferay which starts with "/". Ex : <pre>
 * <header-portlet-css>/html/portlet/directory/css/main.css</header-portlet-css> </pre>
 */
public abstract class AbstractWebResourceURIResolver extends ResourceBaseURIResolver
{

    private final boolean canStartsWithoutSlash;

    public AbstractWebResourceURIResolver( boolean canStartsWithoutSlash )
    {
        this.canStartsWithoutSlash = canStartsWithoutSlash;
    }

    @Override
    public boolean accept(
        Object selectedNode, IResource rootContainer, IResource file, String matching, boolean fullMatch )
    {
        final String extension = file.getFileExtension();

        if( extension == null || ( ! getExtensions().contains( extension.toLowerCase() ) ) )
        {
            return false;
        }

        matching = ( matching == null ? matching : matching.toLowerCase() );

        if( fullMatch )
        {
            if( canStartsWithoutSlash )
            {
                final String uri = resolve( selectedNode, rootContainer, file ).toLowerCase();

                return( uri.equals( matching ) || uri.equals( "/" + matching ) );
            }

            return resolve( selectedNode, rootContainer, file ).equals( matching );
        }

        return super.accept( selectedNode, rootContainer, file, matching, fullMatch );
    }

    protected abstract Set<String> getExtensions();

    @Override
    public String resolve( Object selectedNode, IResource rootContainer, IResource file )
    {
        return "/" + super.resolve( selectedNode, rootContainer, file );
    }
}
