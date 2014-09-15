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

import com.liferay.ide.ui.LiferayUIPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * @author Gregory Amerson
 */
public class PropertyCompletionProposal
    implements ICompletionProposal, ICompletionProposalExtension3, ICompletionProposalExtension5
{
    private final String info;
    private final String key;
    private final int offset;
    private final int rewindOffset;

    public PropertyCompletionProposal( String key, String info, int offset, int rewindOffset )
    {
        this.key = key;
        this.info = info;
        this.offset = offset;
        this.rewindOffset = rewindOffset;
    }

    public void apply( IDocument document )
    {
        try
        {
            document.replace( this.rewindOffset, this.offset - this.rewindOffset, this.key );
        }
        catch( BadLocationException e )
        {
            LiferayUIPlugin.logError( "Unable to apply proposal", e );
        }
    }

    public String getAdditionalProposalInfo()
    {
        return this.info;
    }

    public Object getAdditionalProposalInfo( IProgressMonitor monitor )
    {
        return this.info;
    }

    public IContextInformation getContextInformation()
    {
        return null;
    }

    public String getDisplayString()
    {
        return this.key;
    }

    public Image getImage()
    {
        return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_ELEMENT );
    }

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

    public int getPrefixCompletionStart( IDocument document, int completionOffset )
    {
        return this.rewindOffset;
    }

    public CharSequence getPrefixCompletionText( IDocument document, int completionOffset )
    {
        try
        {
            return document.get( this.rewindOffset, completionOffset );
        }
        catch( BadLocationException e )
        {
        }

        return null;
    }

    public Point getSelection( IDocument document )
    {
        final int point = this.rewindOffset + this.key.length();

        return new Point( point, 0 );
    }

}
