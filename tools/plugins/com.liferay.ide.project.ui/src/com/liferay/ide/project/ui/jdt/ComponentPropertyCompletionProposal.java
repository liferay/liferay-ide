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

package com.liferay.ide.project.ui.jdt;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class ComponentPropertyCompletionProposal extends JavaCompletionProposal
{

    private int replacementStart;
    private int replacementEnd;
    private String addtionalString;
    private String source;

    public ComponentPropertyCompletionProposal(
        JavaContentAssistInvocationContext jdt, String replacementString, int replacementOffset, int replacementLength,
        Image image, String displayString, int relevance, int replacementStart, int replacementEnd,
        String addtionalString, String source )
    {
        super( replacementString, replacementOffset, replacementLength, image, displayString, relevance );

        this.replacementStart = replacementStart;
        this.replacementEnd = replacementEnd;

        this.addtionalString = addtionalString;
        this.source = source;
    }

    @Override
    public IInformationControlCreator getInformationControlCreator()
    {
        return new AbstractReusableInformationControlCreator()
        {

            @Override
            protected IInformationControl doCreateInformationControl( final Shell parent )
            {
                return new DefaultInformationControl( parent, true );
            }
        };
    }

    @Override
    public Object getAdditionalProposalInfo( IProgressMonitor monitor )
    {
        String info = this.addtionalString;

        return info;
    }

    private boolean fIsValidated = true;

    protected boolean isOffsetValid( int offset )
    {
        return getReplacementOffset() <= offset;
    }

    /**
     * Gets the replacement offset.
     * 
     * @return Returns a int
     */
    @Override
    public final int getReplacementOffset()
    {
        if( !fReplacementOffsetComputed )
            setReplacementOffset( getReplaceStart() );
        return super.getReplacementOffset();
    }

    /**
     * Sets the replacement length.
     * 
     * @param replacementLength
     *            The replacementLength to set
     */
    public void setReplacementLength( int replacementLength )
    {
        Assert.isTrue( replacementLength >= 0 );
        super.setReplacementLength( replacementLength );
    }

    public int getReplaceStart()
    {
        if( source.contains( "\"" ) )
        {
            return replacementStart - 1;
        }
        return replacementStart; // default overridden by concrete implementation
    }

    public int getReplaceEnd()
    {
        return replacementEnd;
    }

    public int setReplaceEnd( final int replacementEnd )
    {
        return this.replacementEnd = replacementEnd;
    }

    public final int getReplacementLength()
    {
        if( !fReplacementLengthComputed )
            setReplacementLength( getReplaceEnd() - getReplaceStart() );
        if( source.contains( "\"" ) && source.endsWith( "\"" ) )
        {
            return super.getReplacementLength() + 1;
        }

        return super.getReplacementLength();
    }

    private boolean fReplacementOffsetComputed;
    private boolean fReplacementLengthComputed;

    @Override
    public boolean validate( IDocument document, int offset, DocumentEvent event )
    {
        if( !isOffsetValid( offset ) )
            return fIsValidated = false;
        String prefix = getPrefix( document, offset );
        String removeQuotPrefix = prefix.replace( "\"", "" );
        fIsValidated = isValidPrefix( removeQuotPrefix );

        if( fIsValidated )
        {
            setReplaceEnd( getReplaceEnd() );
        }

        if( fIsValidated && event != null )
        {
            int delta = ( event.fText == null ? 0 : event.fText.length() ) - event.fLength;
            final int newLength = Math.max( getReplacementLength() + delta, 0 );

            setReplacementLength( newLength );
            setReplaceEnd( getReplaceEnd() + delta );
        }

        return fIsValidated;
    }

    protected boolean isValidPrefix( String prefix )
    {
        String word = TextProcessor.deprocess( getDisplayString() );
        return isPrefix( prefix, word );
    }
}
