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
package com.liferay.ide.xml.search.ui;

import com.liferay.ide.xml.search.ui.validators.LiferayBaseValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.MarkerAnnotation;


/**
 * @author Gregory Amerson
 */
public class ResourceBundleQuickAssistProcessor implements IQuickAssistProcessor
{

    @Override
    public boolean canAssist( IQuickAssistInvocationContext invocationContext )
    {
        return true;
    }

    @Override
    public boolean canFix( Annotation annotation )
    {
        if( annotation instanceof MarkerAnnotation )
        {
            MarkerAnnotation mark = (MarkerAnnotation) annotation;

            String hint = mark.getMarker().getAttribute( LiferayBaseValidator.MARKER_QUERY_ID, null );

            if( LiferayXMLConstants.RESOURCE_BUNDLE_QUERY_SPECIFICATION_ID.equals( hint ) )
            {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public ICompletionProposal[] computeQuickAssistProposals( IQuickAssistInvocationContext context )
    {
        ICompletionProposal[] retval = null;

        final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        final ISourceViewer sourceViewer = context.getSourceViewer();
        final Iterator<Annotation> annotations = sourceViewer.getAnnotationModel().getAnnotationIterator();

        while( annotations.hasNext() )
        {
            final Annotation annotation = annotations.next();

            if( annotation instanceof MarkerAnnotation )
            {
                final MarkerAnnotation marker = (MarkerAnnotation) annotation;
                final Position position =  sourceViewer.getAnnotationModel().getPosition( annotation );
                try
                {
                    int lineNum = sourceViewer.getDocument().getLineOfOffset( position.getOffset() ) + 1;
                    int currentLineNum = sourceViewer.getDocument().getLineOfOffset( context.getOffset() ) + 1;

                    if( currentLineNum == lineNum )
                    {
                        final String hint =
                            marker.getMarker().getAttribute( LiferayBaseValidator.MARKER_QUERY_ID, null );

                        if( LiferayXMLConstants.RESOURCE_BUNDLE_QUERY_SPECIFICATION_ID.equals( hint ) )
                        {
                            Collections.addAll( proposals, createFromMarkerResolutions( marker.getMarker() ) );
                        }
                    }
                }
                catch( BadLocationException e )
                {
                    LiferayXMLSearchUI.logError( "Error finding quick assists", e );
                }
            }
        }

        if( proposals.size() > 0 )
        {
            retval = proposals.toArray( new ICompletionProposal[0] );
        }

        return retval;
    }

    private ICompletionProposal[] createFromMarkerResolutions( IMarker marker )
    {
        final List<ICompletionProposal> retval = new ArrayList<ICompletionProposal>();

        if( IDE.getMarkerHelpRegistry().hasResolutions( marker ) )
        {
            final IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry().getResolutions( marker );

            for( IMarkerResolution resolution : resolutions )
            {
                if( resolution instanceof AbstractLanguagePropertiesMarkerResolution )
                {
                    retval.add( new MarkerResolutionProposal( resolution, marker ) );
                }
            }
        }

        return retval.toArray( new ICompletionProposal[0] );
    }

    @Override
    public String getErrorMessage()
    {
        return null;
    }

}
