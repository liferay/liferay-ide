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

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.wst.xml.search.core.xpath.NamespaceInfos;
import org.eclipse.wst.xml.search.editor.searchers.javamethod.classnameprovider.AbstractClassNameExtractor;
import org.eclipse.wst.xml.search.editor.searchers.javamethod.classnameprovider.IClassNameExtractor;
import org.w3c.dom.Node;


/**
 * @author Gregory Amerson
 */
public class PortletActionMethodClassNameExtractor extends AbstractClassNameExtractor
{
    public static final IClassNameExtractor INSTANCE = new PortletActionMethodClassNameExtractor();

    private static class PortletClassCollector extends SearchRequestor
    {
        private final List<String> results = new ArrayList<String>();

        public PortletClassCollector()
        {
            super();
        }

        @Override
        public void acceptSearchMatch( SearchMatch match ) throws CoreException
        {
            final Object element = match.getElement();

            if( element instanceof IType )
            {
                final IType type = (IType) element;

                this.results.add( type.getFullyQualifiedName() );
            }
        }

        List<String> getResults()
        {
            return this.results;
        }
    }

    @Override
    protected String[] doExtractClassNames(
        Node node, IFile file, String pathForClass, String findByAttrName, boolean findByParentNode,
        String xpathFactoryProviderId, NamespaceInfos namespaceInfo ) throws XPathExpressionException
    {
        String[] retval = null;

        final IJavaProject project = JavaCore.create( file.getProject() );

        try
        {
            // look for all subclasses of liferayPortlet in this project
            final IType portletType = project.findType( "javax.portlet.Portlet" );

            final IJavaSearchScope scope =
                SearchEngine.createStrictHierarchyScope( project, portletType, true, false, null );

            final PortletClassCollector requestor = new PortletClassCollector();
            final SearchPattern search =
                SearchPattern.createPattern( "*", IJavaSearchConstants.CLASS, IJavaSearchConstants.DECLARATIONS, 0 );

            new SearchEngine().search(
                search, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope, requestor,
                new NullProgressMonitor() );

            retval = requestor.getResults().toArray( new String[0] );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return retval;
    }
}
