/*******************************************************************************
 * Copyright (c) 2011 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.xml.search.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.search.editor.contentassist.ElementContentAssistAdditionalProposalInfoProvider;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Custom content assist proposal info for portlet.
 */
@SuppressWarnings( "restriction" )
public class PortletContentAssistInfoProvider extends ElementContentAssistAdditionalProposalInfoProvider
{

    @Override
    protected String doGetTextInfo( IDOMElement portletNameElt )
    {
        final IDOMElement portletElt = (IDOMElement) portletNameElt.getParentNode();

        final StringBuilder buf = new StringBuilder();

        buf.append( "<b>------------------------ Portlet ------------------------</b> " );
        // description
        buf.append( "<br><b>Portlet name:</b> " );

        final String portletName = getTextContent( portletElt, "portlet-name" );

        if( portletName != null )
        {
            buf.append( portletName );
        }

        // display-name
        buf.append( "<br><b>Display name:</b> " );

        final String displayName = getTextContent( portletElt, "display-name" );

        if( displayName != null )
        {
            buf.append( displayName );
        }

        // portlet-class

        buf.append( "<br><b>Portlet class:</b> " );

        final String portletClass = getTextContent( portletElt, "portlet-class" );

        if( portletClass != null )
        {
            buf.append( portletClass );
        }

        buf.append( "<br><b>File:</b> " );
        buf.append( portletElt.getModel().getBaseLocation() );

        return buf.toString();

    }

    public Image getImage( Node node )
    {
        return LiferayXMLSearchUI.getDefault().getImageRegistry().get( LiferayXMLSearchUI.PORTLET_IMG );
    }

    private String getTextContent( IDOMElement element, String elementName )
    {
        NodeList nodes = element.getElementsByTagName( elementName );
        if( nodes.getLength() < 1 )
        {
            return "";
        }
        Element childElement = (Element) nodes.item( 0 );
        Text text = (Text) childElement.getFirstChild();
        if( text == null )
        {
            return "";
        }
        return text.getData();
    }

}
