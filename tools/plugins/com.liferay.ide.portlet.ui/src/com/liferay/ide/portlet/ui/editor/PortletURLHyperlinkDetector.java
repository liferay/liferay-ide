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
package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.util.DOMUtils;
import org.w3c.dom.Node;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class PortletURLHyperlinkDetector extends AbstractHyperlinkDetector
{
    private IFile lastFile;
    private long lastModStamp;
    private IRegion lastNodeRegion;
    private IMethod[] lastActionUrlMethods;

    private static class ActionMethodCollector extends SearchRequestor
    {
        private final List<IMethod> results;

        public ActionMethodCollector( List<IMethod> results )
        {
            super();
            this.results = results;
        }

        @Override
        public void acceptSearchMatch( SearchMatch match ) throws CoreException
        {
            final Object element = match.getElement();

            if( element instanceof IMethod && isActionMethod( (IMethod) element ) )
            {
                this.results.add( (IMethod) element );
            }
        }
    }

    private static boolean isActionMethod( IMethod method )
    {
        final String[] paramTypes = method.getParameterTypes();

        return paramTypes.length == 2 && paramTypes[0].toLowerCase().contains( "request" ) &&
            paramTypes[1].toLowerCase().contains( "response" );
    }

    public PortletURLHyperlinkDetector()
    {
        super();
    }

    public IHyperlink[] detectHyperlinks( ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks )
    {
        IHyperlink[] retval = null;

        if( shouldDetectHyperlinks( textViewer, region ) )
        {
            final IDocument document = textViewer.getDocument();
            final int offset = region.getOffset();
            final IDOMNode currentNode = DOMUtils.getNodeByOffset( document, offset );
            final IRegion nodeRegion =
                new Region( currentNode.getStartOffset(), currentNode.getEndOffset() - currentNode.getStartOffset() );

            if( isActionURL( currentNode ) )
            {
                final Node name = currentNode.getAttributes().getNamedItem( "name" );

                if( name != null )
                {
                    final long modStamp = ( (IDocumentExtension4) document ).getModificationStamp();
                    final IFile file = DOMUtils.getFile( document );

                    IMethod[] actionUrlMethods = null;

                    if( file.equals( this.lastFile ) && modStamp == this.lastModStamp &&
                        nodeRegion.equals( this.lastNodeRegion ) )
                    {
                        actionUrlMethods = this.lastActionUrlMethods;
                    }
                    else
                    {
                        final String nameValue = name.getNodeValue();

                        // search for this method in any portlet classes
                        actionUrlMethods = findPortletMethods( document, nameValue );

                        this.lastModStamp = modStamp;
                        this.lastFile = file;
                        this.lastNodeRegion = nodeRegion;
                        this.lastActionUrlMethods = actionUrlMethods;
                    }

                    if( ! CoreUtil.isNullOrEmpty( actionUrlMethods ) )
                    {
                        final List<IHyperlink> links = new ArrayList<IHyperlink>();

                        for( IMethod method : actionUrlMethods )
                        {
                            if( method.exists() )
                            {
                                links.add( new BasicJavaElementHyperlink( nodeRegion, method ) );
                            }
                        }

                        if( links.size() != 0 )
                        {
                            if( canShowMultipleHyperlinks )
                            {
                                retval = links.toArray( new IHyperlink[0] );
                            }
                            else
                            {
                                retval = new IHyperlink[] { links.get( 0 ) };
                            }
                        }
                    }
                }
            }
        }

        return retval;
    }

    private IMethod[] findPortletMethods( IDocument document, String nameValue )
    {
        IMethod[] retval = null;

        final IFile file = DOMUtils.getFile( document );

        if( file != null && file.exists() )
        {
            final IJavaProject project = JavaCore.create( file.getProject() );

            if( project != null && project.exists() )
            {
                try
                {
                    final IType portlet = project.findType( "javax.portlet.Portlet" );

                    if( portlet != null )
                    {
                        final List<IMethod> methods = new ArrayList<IMethod>();
                        final SearchRequestor requestor = new ActionMethodCollector( methods );

                        final IJavaSearchScope scope =
                            SearchEngine.createStrictHierarchyScope( project, portlet, true, false, null );

                        final SearchPattern search =
                            SearchPattern.createPattern(
                                nameValue, IJavaSearchConstants.METHOD, IJavaSearchConstants.DECLARATIONS,
                                SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE );

                        new SearchEngine().search(
                            search, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope,
                            requestor, new NullProgressMonitor() );

                        retval = methods.toArray( new IMethod[0] );
                    }
                }
                catch( JavaModelException e )
                {
                }
                catch( CoreException e )
                {
                }
            }
        }

        return retval;
    }

    private boolean isActionURL( Node currentNode )
    {
        return currentNode != null && currentNode.getNodeName() != null &&
            currentNode.getNodeType() == Node.ELEMENT_NODE &&
            currentNode.getNodeName().endsWith( "actionURL" );
    }

    private boolean shouldDetectHyperlinks( final ITextViewer textViewer, final IRegion region )
    {
        return region != null && textViewer != null;
    }

}
