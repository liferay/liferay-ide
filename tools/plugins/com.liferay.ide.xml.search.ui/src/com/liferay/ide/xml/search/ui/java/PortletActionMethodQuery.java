/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.xml.search.ui.java;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xml.search.editor.searchers.javamethod.classnameprovider.IClassNameExtractor;
import org.eclipse.wst.xml.search.editor.searchers.javamethod.classnameprovider.IClassNameExtractorProvider;
import org.eclipse.wst.xml.search.editor.searchers.javamethod.requestor.IJavaMethodRequestor;
import org.eclipse.wst.xml.search.editor.searchers.javamethod.requestor.IJavaMethodRequestorProvider;


/**
 * @author Gregory Amerson
 */
public class PortletActionMethodQuery implements IJavaMethodRequestorProvider, IClassNameExtractorProvider
{
    private static final IClassNameExtractor EXTRACTOR_INSTANCE =
                    new HierarchyTypeClassNameExtractor( "javax.portlet.Portlet" );

    public PortletActionMethodQuery()
    {
        super();
    }

    @Override
    public IClassNameExtractor getClassNameExtractor( Object selectedNode, IFile file )
    {
        return EXTRACTOR_INSTANCE;
    }

    @Override
    public IJavaMethodRequestor getRequestor( Object selectedNode, IFile file )
    {
        return PortletActionMethodRequestor.INSTANCE;
    }

}
