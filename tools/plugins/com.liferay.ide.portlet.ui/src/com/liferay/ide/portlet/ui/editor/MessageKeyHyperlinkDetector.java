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
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.properties.core.PropertiesFileLookup;
import com.liferay.ide.properties.core.PropertiesFileLookup.KeyInfo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
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
    public static class MessageKey
    {
        public final IFile file;
        public final String key;
        public final int offset;
        public final int length;

        public MessageKey( IFile file, String key, int offset, int length )
        {
            this.file = file;
            this.key = key;
            this.offset = offset;
            this.length = length;
        }
    }

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

            if( isMessageKey( currentNode) )
            {
                final IDOMNode keyNode = (IDOMNode) currentNode.getAttributes().getNamedItem( "key" );
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
                    messageKeys = findMessageKeys( document, key );

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

    private MessageKey[] findMessageKeys( IDocument document, String key )
    {
        MessageKey[] retval = null;

        final IFile file = DOMUtils.getFile( document );

        if( file != null && file.exists() )
        {
            final IJavaProject project = JavaCore.create( file.getProject() );

            if( project != null && project.exists() )
            {
                final List<MessageKey> keys = new ArrayList<MessageKey>();

                for( final IFolder src : CoreUtil.getSrcFolders( project.getProject() ) )
                {
                    final IFile[] props = PropertiesUtil.visitPropertiesFiles( src, ".*" );

                    for( final IFile prop : props )
                    {
                        try
                        {
                            final KeyInfo info = new PropertiesFileLookup( prop.getContents(), key ).getKeyInfo( key );

                            if( info.offset >= 0 )
                            {
                                keys.add( new MessageKey( prop, key, info.offset, info.length ) );
                            }
                        }
                        catch( CoreException e )
                        {
                        }
                    }
                }

                retval = keys.toArray( new MessageKey[0] );
            }
        }

        return retval;
    }

    private boolean isMessageKey( IDOMNode currentNode )
    {
        boolean retval = false;

        final boolean messageNode = currentNode != null &&
                        currentNode.getNodeName() != null &&
                        currentNode.getNodeType() == Node.ELEMENT_NODE &&
                        ( currentNode.getNodeName().endsWith( "message" ) ||
                          currentNode.getNodeName().endsWith( "error" ) );

        if( messageNode )
        {
            final Node key = currentNode.getAttributes().getNamedItem( "key" );

            retval = key != null && ( ! CoreUtil.isNullOrEmpty( key.getNodeValue() ) );
        }

        return retval;
    }

    private boolean shouldDetectHyperlinks( ITextViewer textViewer, IRegion region )
    {
        return region != null && textViewer != null;
    }

}
