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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.sse.ui.internal.taginfo.AbstractHoverProcessor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.util.DOMUtils;
import org.w3c.dom.Node;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class MessageKeyHover extends AbstractHoverProcessor
{
    private Region foundRegion;
    private MessageKey[] messageKeys;

    public MessageKeyHover()
    {
        super();
    }

    public String getHoverInfo( ITextViewer textViewer, IRegion hoverRegion )
    {
        String retval = null;

        if( hoverRegion != null && hoverRegion.equals( this.foundRegion ) &&
            ( !CoreUtil.isNullOrEmpty( this.messageKeys ) ) )
        {
            retval = this.messageKeys[0].value;
        }

        return retval;
    }

    public IRegion getHoverRegion( ITextViewer textViewer, int offset )
    {
        IRegion retval = null;

        if( shouldHover( textViewer ) )
        {
            final IDocument document = textViewer.getDocument();
            final IDOMNode node = DOMUtils.getNodeByOffset( document, offset );
            final Node keyNode = NodeUtils.getMessageKey( node );

            if( keyNode != null )
            {
                MessageKey[] messageKeys = NodeUtils.findMessageKeys( document, keyNode.getNodeValue(), true );

                if( ! CoreUtil.isNullOrEmpty( messageKeys ) )
                {
                    IDOMNode regionNode = node;
                    // check if this is an attr hover and return attribute region
                    final IDOMNode attr = DOMUtils.getAttrByOffset( node, offset );

                    if( attr != null )
                    {
                        regionNode = attr;
                    }

                    this.foundRegion =
                        new Region( regionNode.getStartOffset(), regionNode.getEndOffset() -
                            regionNode.getStartOffset() );
                    this.messageKeys = messageKeys;
                    retval = this.foundRegion;
                }
            }
        }

        return retval;
    }

    private boolean shouldHover( ITextViewer textViewer )
    {
        return textViewer != null;
    }

}
