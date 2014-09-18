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

package com.liferay.ide.xml.search.ui.validators;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.properties.IPropertiesQuerySpecification;
import org.eclipse.wst.xml.search.core.properties.IPropertiesRequestor;
import org.eclipse.wst.xml.search.core.util.DOMUtils;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceTo;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceToProperty;
import org.eclipse.wst.xml.search.editor.util.PropertiesQuerySpecificationUtil;
import org.eclipse.wst.xml.search.editor.validation.LocalizedMessage;
import org.eclipse.wst.xml.search.editor.validation.XMLReferencesBatchValidator;
import org.w3c.dom.Node;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class LiferayJspValidator extends LiferayBaseValidator
{

    public static final String MARKER_TYPE = "com.liferay.ide.xml.search.ui.liferayJspMarker";

    protected void addMessage(
        IDOMNode node, IFile file, IValidator validator, IReporter reporter, boolean batchMode, String messageText,
        int severity )
    {
        final String textContent = DOMUtils.getNodeValue( node );
        int startOffset = getStartOffset( node );

        if( textContent != null )
        {
            int length = textContent.trim().length() + 2;

            final LocalizedMessage message =
                createMessage( startOffset, length, messageText, severity, node.getStructuredDocument() );

            if( message != null )
            {
                message.setTargetObject( file );
                reporter.addMessage( validator, message );
            }
        }
    }

    @Override
    protected IFile getReferencedFile( IXMLReferenceTo referenceTo, Node node, IFile file )
    {
        if( referenceTo instanceof IXMLReferenceToProperty )
        {
            IXMLReferenceToProperty referenceToProperty = (IXMLReferenceToProperty) referenceTo;

            IPropertiesQuerySpecification[] querySpecifications =
                PropertiesQuerySpecificationUtil.getQuerySpecifications( referenceToProperty );

            if( querySpecifications == null || querySpecifications.length < 1 )
            {
                return null;
            }

            IPropertiesQuerySpecification querySpecification = null;

            querySpecification = querySpecifications[0];

            IPropertiesRequestor requestor = querySpecification.getRequestor();

            IResource resource = querySpecification.getResource( node, file );

            return new ReferencedPropertiesVisitor().getReferencedFile( requestor, resource );
        }

        return null;
    }

    @Override
    protected void setMarker( IValidator validator, IFile file )
    {
        if( ( validator instanceof XMLReferencesBatchValidator ) && file.getFileExtension().equals( "jsp" ) )
        {
            ( (XMLReferencesBatchValidator) validator ).getParent().setMarkerId( MARKER_TYPE );
        }
    }

}
