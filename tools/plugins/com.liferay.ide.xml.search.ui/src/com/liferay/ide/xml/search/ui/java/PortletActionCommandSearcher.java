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
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.search.editor.contentassist.IContentAssistProposalRecorder;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceTo;
import org.eclipse.wst.xml.search.editor.searchers.java.XMLSearcherForJava;


/**
 * @author Gregory Amerson
 */
public class PortletActionCommandSearcher extends XMLSearcherForJava
{

    private String filterDisplayText( String displayText )
    {
        final String[] parts = displayText.split( " - " );

        if( parts.length == 2 )
        {
            parts[1] = parts[1] + "." + parts[0];
            parts[0] = parts[0].substring( 0, parts[0].indexOf( "ActionCommand" ) );

            displayText = parts[0] + " - " + parts[1];
        }

        return displayText;
    }

    protected String filterReplaceText( final String replaceText )
    {
        String retval = replaceText;

        int lastDot = replaceText.lastIndexOf( '.' );

        if( lastDot == -1 )
        {
            lastDot = 0;
        }

        final String className = replaceText.substring( lastDot );

        if( className.lastIndexOf( "ActionCommand" ) > -1 )
        {
            final String prefixOnly = className.substring( 0, className.lastIndexOf( "ActionCommand" ) );

            if( prefixOnly.charAt( 0 ) == '.' && prefixOnly.length() > 1 )
            {
                retval = prefixOnly.substring( 1 );
            }
        }

        return retval;
    }

    @Override
    public void searchForCompletion(
        Object selectedNode, String mathingString, String forceBeforeText, String forceEndText, IFile file,
        IXMLReferenceTo referenceTo, final IContentAssistProposalRecorder recorder )
    {
        final IContentAssistProposalRecorder filteringRecorder = new IContentAssistProposalRecorder()
        {
            @Override
            public void recordProposal( Image image, int relevance, String displayText, String replaceText )
            {
                if( shouldAddProposal( displayText, replaceText ) )
                {
                    recorder.recordProposal(
                        image, relevance, filterDisplayText( displayText ), filterReplaceText( replaceText ) );
                }
            }

            @Override
            public void recordProposal(
                Image image, int relevance, String displayText, String replaceText, int cursorPosition,
                Object proposedObject )
            {
                if( shouldAddProposal( displayText, replaceText ) )
                {
                    recorder.recordProposal(
                        image, relevance, filterDisplayText( displayText ), filterReplaceText( replaceText ),
                        cursorPosition, proposedObject );
                }
            }

            @Override
            public void recordProposal(
                Image image, int relevance, String displayText, String replaceText, Object proposedObject )
            {
                if( shouldAddProposal( displayText, replaceText ) )
                {
                    recorder.recordProposal(
                        image, relevance, filterDisplayText( displayText ), filterReplaceText( replaceText ),
                        proposedObject );
                }
            }
        };

        super.searchForCompletion(
            selectedNode, mathingString, forceBeforeText, forceEndText, file, referenceTo, filteringRecorder );
    }

    private boolean shouldAddProposal( String displayText, String replaceText )
    {
        final String[] parts = displayText.split( " - " );

        if( parts.length == 2 && parts[0].endsWith( "ActionCommand" ) )
        {
            return true;
        }

        return false;
    }
}
