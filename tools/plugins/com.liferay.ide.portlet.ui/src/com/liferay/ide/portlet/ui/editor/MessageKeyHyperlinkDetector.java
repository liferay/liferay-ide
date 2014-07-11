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
import com.liferay.ide.portlet.ui.util.MessageKey;
import com.liferay.ide.portlet.ui.util.NodeUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
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
public class MessageKeyHyperlinkDetector extends AbstractHyperlinkDetector
{
    private long lastModStamp;
    private IFile lastFile;
    private IRegion lastNodeRegion;
    private MessageKey[] lastMessageKeys;

    public MessageKeyHyperlinkDetector()
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
            final Node keyNode = NodeUtils.getMessageKey( currentNode );

            if( keyNode != null )
            {
                final IRegion nodeRegion =
                    new Region( currentNode.getStartOffset(), currentNode.getEndOffset() - currentNode.getStartOffset() );
                final long modStamp = ( (IDocumentExtension4) document ).getModificationStamp();
                final IFile file = DOMUtils.getFile( document );

                MessageKey[] messageKeys = null;

                if( file.equals( this.lastFile ) && modStamp == this.lastModStamp &&
                    nodeRegion.equals( this.lastNodeRegion ) )
                {
                    messageKeys = this.lastMessageKeys;
                }
                else
                {
                    final String key = keyNode.getNodeValue();

                    // search for message key in content/Langauge.properties
                    messageKeys = NodeUtils.findMessageKeys( document, key, false );

                    this.lastModStamp = modStamp;
                    this.lastFile = file;
                    this.lastNodeRegion = nodeRegion;
                    this.lastMessageKeys = messageKeys;
                }

                if( ! CoreUtil.isNullOrEmpty( messageKeys ) )
                {
                    final List<IHyperlink> links = new ArrayList<IHyperlink>();

                    for( MessageKey messageKey : messageKeys )
                    {
                        links.add( new MessageKeyHyperlink(
                            nodeRegion, messageKey.file, messageKey.key, messageKey.offset, messageKey.length ) );
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

        return retval;
    }

    private boolean shouldDetectHyperlinks( ITextViewer textViewer, IRegion region )
    {
        return region != null && textViewer != null;
    }

}
