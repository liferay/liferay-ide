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
package com.liferay.ide.ui.editor;

import com.liferay.ide.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;


/**
 * @author Gregory Amerson
 */
public class LiferayPropertiesContentAssistProcessor implements IContentAssistProcessor
{
    public static class PropKey
    {
        private final String comment;
        private final String key;

        PropKey( String key, String comment )
        {
            this.key = key;
            this.comment = comment;
        }

        String getComment()
        {
            return this.comment;
        }

        String getKey()
        {
            return this.key;
        }
    }

    private final char[] AUTO_CHARS = new char[] { '.' };
    private final PropKey[] propKeys;


    public LiferayPropertiesContentAssistProcessor( PropKey[] propKeys, String contentType )
    {
        this.propKeys = propKeys;

        if( CoreUtil.isNullOrEmpty( propKeys ) )
        {
            throw new IllegalArgumentException( "propKeys can not be empty" );
        }
    }

    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int offset )
    {
        ICompletionProposal[] retval = null;

        String currentPartitionType = null;

        final IDocument document = viewer.getDocument();
        final IDocumentPartitioner partitioner = getPartitioner( document );

        if( partitioner != null )
        {
            final ITypedRegion p = partitioner.getPartition( offset );

            if( p != null )
            {
                currentPartitionType = p.getType();
            }
        }

        if( currentPartitionType != null && currentPartitionType.equals( IDocument.DEFAULT_CONTENT_TYPE ) )
        {
            // now we need to check to see if we have a partial key
            final int rewindOffset = rewindOffsetToNearestNonDefaultPartition( partitioner, offset );

            final String partialKey = getPartialKey( document, rewindOffset, offset );

            final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

            if( this.propKeys != null )
            {
                for( PropKey key : this.propKeys )
                {
                    if( partialKey != null && key.getKey().startsWith( partialKey ) )
                    {
                        proposals.add( new PropertyCompletionProposal(
                            key.getKey(), key.getComment(), offset, rewindOffset ) );
                    }
                }
            }

            retval = proposals.toArray( new ICompletionProposal[0] );
        }

        return retval;
    }

    public IContextInformation[] computeContextInformation( ITextViewer viewer, int offset )
    {
        return null;
    }

    public char[] getCompletionProposalAutoActivationCharacters()
    {
        return AUTO_CHARS;
    }

    public char[] getContextInformationAutoActivationCharacters()
    {
        return null;
    }

    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }

    public String getErrorMessage()
    {
        return "Unable to get keys from portal.properties";
    }

    private String getPartialKey( IDocument document , int rewindOffset, int offset )
    {
        String retval = null;

        if( rewindOffset < offset );
        {
            try
            {
                if( rewindOffset > 0 )
                {
                    rewindOffset--;// move rewind back to beginning of partition type
                }

                retval = document.get( rewindOffset, offset - rewindOffset ).trim();
            }
            catch( Exception e )
            {
            }
        }

        return retval;
    }

    @SuppressWarnings( "restriction" )
    private IDocumentPartitioner getPartitioner( IDocument document )
    {
        IDocumentPartitioner retval = null;

        if( document instanceof IDocumentExtension3 )
        {
            final IDocumentExtension3 doc3 = (IDocumentExtension3) document;
            retval = doc3.getDocumentPartitioner(
                org.eclipse.jdt.internal.ui.propertiesfileeditor.IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING );
        }

        return retval;
    }

    private int rewindOffsetToNearestNonDefaultPartition( IDocumentPartitioner partitioner, final int initialOffset )
    {
        int offset = initialOffset;

        ITypedRegion partition = partitioner.getPartition( offset );

        while( offset > 0 && partition != null && IDocument.DEFAULT_CONTENT_TYPE.equals( partition.getType() ) )
        {
            offset--;
            partition = partitioner.getPartition( offset );
        }

        if( offset > 0 && offset < initialOffset )
        {
            offset++; // move back up to next partition
        }

        return offset;
    }

}
