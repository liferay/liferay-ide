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

import java.util.HashSet;
import java.util.Set;

/**
 * Extension of WTP/XML Search resources uri resolver for Liferay to manage css, js, icons used in the descriptor of
 * lifreay which starts with "/". Ex : <pre>
 * <header-portlet-css>/html/portlet/directory/css/main.css</header-portlet-css> </pre>
 */
public class CSSResourceURIResolver extends AbstractWebResourceURIResolver
{

    public static final CSSResourceURIResolver INSTANCE = new CSSResourceURIResolver();

    private static final Set<String> EXTENSIONS;

    public CSSResourceURIResolver()
    {
        super( true );
    }

    static
    {
        EXTENSIONS = new HashSet<String>();
        EXTENSIONS.add( "css" );
    }

    @Override
    protected Set<String> getExtensions()
    {
        return EXTENSIONS;
    }
}
