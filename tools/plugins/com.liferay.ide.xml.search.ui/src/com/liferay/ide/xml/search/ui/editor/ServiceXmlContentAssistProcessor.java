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
 *******************************************************************************/

package com.liferay.ide.xml.search.ui.editor;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.editor.contentassist.XMLReferencesContentAssistUtils;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings( { "deprecation", "restriction" } )
public class ServiceXmlContentAssistProcessor extends XMLContentAssistProcessor
{
    private ServiceXmlTemplateCompletinoProcessor templateProcessor;
    private boolean templatesAdd = false;

    public ServiceXmlContentAssistProcessor()
    {
        super();
    }

    @Override
    protected void addAttributeValueProposals( ContentAssistRequest contentAssistRequest )
    {
        XMLReferencesContentAssistUtils.addAttributeValueProposals( contentAssistRequest );
        super.addAttributeValueProposals( contentAssistRequest );

    }

    @Override
    protected ContentAssistRequest computeEndTagOpenProposals(
        int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node )
    {
        ContentAssistRequest request =
            super.computeEndTagOpenProposals( documentPosition, matchString, completionRegion, nodeAtOffset, node );
        if( matchString.equals( "" ) )
        {
            doEntityProposalIfNeeded( request );
        }

        return request;
    }

    @Override
    protected ContentAssistRequest computeContentProposals(
        int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node )
    {
        ContentAssistRequest request =
            super.computeContentProposals( documentPosition, matchString, completionRegion, nodeAtOffset, node );
        doEntityProposalIfNeeded( request );
        return request;
    }

    private void doEntityProposalIfNeeded( ContentAssistRequest contentAssistRequest )
    {
        XMLReferencesContentAssistUtils.addEntityProposals( this, contentAssistRequest );
    }

    @Override
    protected void addTagInsertionProposals( ContentAssistRequest contentAssistRequest, int childPosition )
    {
        super.addTagInsertionProposals( contentAssistRequest, childPosition );
        addTemplates( contentAssistRequest );
    }

    private void addTemplates( ContentAssistRequest contentAssistRequest )
    {
        addTemplates( contentAssistRequest, contentAssistRequest.getReplacementBeginPosition() );
    }

    private void addTemplates( ContentAssistRequest contentAssistRequest, int startOffset )
    {
        if( contentAssistRequest == null )
        {
            return;
        }

        if( !templatesAdd )
        {
            templatesAdd = true;

            if( getTemplateCompletionProcessor() != null )
            {
                ICompletionProposal[] proposals =
                    getTemplateCompletionProcessor().computeCompletionProposals( fTextViewer, startOffset );

                for( int i = 0; i < proposals.length; ++i )
                {
                    contentAssistRequest.addProposal( proposals[i] );
                }
            }
        }
    }

    private ServiceXmlTemplateCompletinoProcessor getTemplateCompletionProcessor()
    {
        if( templateProcessor == null )
        {
            templateProcessor = new ServiceXmlTemplateCompletinoProcessor();
        }

        return templateProcessor;
    }

    @Override
    public ICompletionProposal[] computeCompletionProposals( ITextViewer textViewer, int documentPosition )
    {
        templatesAdd = false;
        return super.computeCompletionProposals( textViewer, documentPosition );
    }
}
