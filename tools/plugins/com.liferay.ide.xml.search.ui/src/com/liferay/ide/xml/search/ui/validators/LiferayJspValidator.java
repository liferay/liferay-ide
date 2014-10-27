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

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.project.core.ValidationPreferences.ValidationType;
import com.liferay.ide.xml.search.ui.PortalLanguagePropertiesCacheUtil;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.properties.IPropertiesQuerySpecification;
import org.eclipse.wst.xml.search.core.properties.IPropertiesRequestor;
import org.eclipse.wst.xml.search.core.util.DOMUtils;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceTo;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceToProperty;
import org.eclipse.wst.xml.search.editor.util.PropertiesQuerySpecificationUtil;
import org.eclipse.wst.xml.search.editor.validation.IValidationResult;
import org.eclipse.wst.xml.search.editor.validation.LocalizedMessage;
import org.eclipse.wst.xml.search.editor.validation.XMLReferencesBatchValidator;
import org.w3c.dom.Node;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class LiferayJspValidator extends LiferayBaseValidator
{
    private final String JSP_TAG_START = "<%";
    private final String JSP_TAG_END = "%>";

    protected void addMessage(
        IDOMNode node, IFile file, IValidator validator, IReporter reporter, boolean batchMode, String messageText,
        int severity, String querySpecificationId )
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
                message.setAttribute( MARKER_QUERY_ID, querySpecificationId );
                message.setAttribute( XMLSearchConstants.TEXT_CONTENT, textContent );
                message.setAttribute( XMLSearchConstants.FULL_PATH, file.getFullPath().toPortableString() );
                message.setAttribute( XMLSearchConstants.MARKER_TYPE, XMLSearchConstants.LIFERAY_JSP_MARKER_ID  );
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
    protected int getServerity( ValidationType validationType, IFile file )
    {
        int retval = -1;
        String validationKey = null;

        if( ValidationType.PROPERTY_NOT_FOUND.equals( validationType ) )
        {
            validationKey = ValidationPreferences.LIFERAY_JSP_FILES_RESOURCE_PROPERTY_NOT_FOUND;
        }
        else if( ValidationType.METHOD_NOT_FOUND.equals( validationType ) )
        {
            validationKey = ValidationPreferences.LIFERAY_JSP_FILES_JAVA_METHOD_NOT_FOUND;
        }

        if( validationKey != null )
        {
            retval =
                Platform.getPreferencesService().getInt(
                    PREFERENCE_NODE_QUALIFIER, validationKey, IMessage.NORMAL_SEVERITY,
                    getScopeContexts( file.getProject() ) );
        }
        else
        {
            retval = super.getServerity( validationType, file );
        }

        return retval;
    }

    @Override
    protected void setMarker( IValidator validator, IFile file )
    {
        if( ( validator instanceof XMLReferencesBatchValidator ) && file.getFileExtension().equals( "jsp" ) )
        {
            ( (XMLReferencesBatchValidator) validator ).getParent().setMarkerId(
                XMLSearchConstants.LIFERAY_JSP_MARKER_ID );
        }
    }

    @Override
    protected void validateReferenceToProperty(
        IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
        boolean batchMode )
    {
        final String languageKey = DOMUtils.getNodeValue( node );

        if( !languageKey.contains( JSP_TAG_START ) && !languageKey.contains( JSP_TAG_END ) )
        {
            final IValidationResult result =
                referenceTo.getSearcher().searchForValidation( node, languageKey, -1, -1, file, referenceTo );

            if( result != null )
            {
                boolean addMessage = false;

                int nbElements = result.getNbElements();

                if( nbElements > 0 )
                {
                    if( nbElements > 1 && !isMultipleElementsAllowed( node, nbElements ) )
                    {
                        addMessage = true;
                    }
                }
                else
                {
                    addMessage = true;
                }

                if( addMessage )
                {
                    final Properties properties =
                        PortalLanguagePropertiesCacheUtil.getPortalLanguageProperties( LiferayCore.create( file.getProject() ) );

                    if( properties != null )
                    {
                        try
                        {
                            String languageValue = (String) properties.get( languageKey );

                            if( !languageValue.equals( "" ) )
                            {
                                addMessage = false;
                            }
                        }
                        catch( Exception e )
                        {
                        }

                        if( addMessage )
                        {
                            final ValidationType validationType = getValidationType( referenceTo, nbElements );
                            final int severity = getServerity( validationType, file );

                            if( severity != ValidationMessage.IGNORE )
                            {
                                final String querySpecificationId = referenceTo.getQuerySpecificationId();

                                final String messageText = getMessageText( validationType, referenceTo, node, file );

                                addMessage(
                                    node, file, validator, reporter, batchMode, messageText, severity,
                                    querySpecificationId );
                            }
                        }
                    }
                }
            }
        }
    }

}
