/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import org.eclipse.sapphire.modeling.xml.RootElementController;
import org.eclipse.sapphire.modeling.xml.XmlResource;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

/**
 * @author kamesh
 */
public class DtdRootElementController extends RootElementController
{

    private String rootElementName;
    private String publicId;
    private String systemId;

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.xml.RootElementController#init(org.eclipse.sapphire.modeling.xml.XmlResource)
     */
    @Override
    public void init( XmlResource resource )
    {
        super.init( resource );
        Doctype doctypeAnnotation = resource.root().element().type().getAnnotation( Doctype.class );
        if( doctypeAnnotation != null )
        {
            this.rootElementName = doctypeAnnotation.rootElementName();
            this.systemId = doctypeAnnotation.systemId();
            this.publicId = doctypeAnnotation.publicId();

            // System.out.println( String.format(
            // "DtdRootElementController.init() - [Root - %s, Public Id- %s, SystemId - %s ]", this.rootElementName,
            // this.publicId, this.systemId ) );
        }

    }

    protected void createRootElement( Document document )
    {
        final Element root = document.createElementNS( null, this.rootElementName );
        final DocumentType doctype =
            document.getImplementation().createDocumentType( this.rootElementName, this.publicId, this.systemId );

        document.appendChild( doctype );
        document.insertBefore( document.createTextNode( "\n" ), doctype );
        document.appendChild( document.createTextNode( "\n" ) );
        document.appendChild( root );
    }

    @Override
    public void createRootElement()
    {
        Document document = resource().root().getDomDocument();
        createRootElement( document );
    }

    @Override
    public boolean checkRootElement()
    {
        Document document = resource().root().getDomDocument();
        return checkRootElement( document );
    }

    /**
     * @return
     */
    private boolean checkRootElement( Document document )
    {
        final Element root = document.getDocumentElement();
        String localName = root.getLocalName();
        return equal( localName, this.rootElementName );
    }

}
