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
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
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

    private final String ACTION_REQUEST_ACTION_NAME = "ActionRequest.ACTION_NAME";
    private final String AUI_PREFIX = "aui";
    private final String JAVAX_PORTLET_ACTION = "javax.portlet.action";
    private final String JSP_TAG_START = "<%";
    private final String JSP_TAG_END = "%>";
    private final String[] SUPPORTED_TAGS = { "liferay-portlet:param", "portlet:param" };

    public static final String MESSAGE_CLASS_ATTRIBUTE_NOT_WORK = Msgs.classAttributeNotWork;

    protected void addMessage(
        IDOMNode node, IFile file, IValidator validator, IReporter reporter, boolean batchMode,
        String messageText, int severity, String liferayPluginValidationType, String querySpecificationId )
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
                message.setAttribute( XMLSearchConstants.MARKER_TYPE, XMLSearchConstants.LIFERAY_JSP_MARKER_ID );
                message.setAttribute(
                    XMLSearchConstants.LIFERAY_PLUGIN_VALIDATION_TYPE, liferayPluginValidationType );
                message.setTargetObject( file );
                reporter.addMessage( validator, message );
            }
        }
    }

    protected String getMessageText(
        ValidationType validationType, IXMLReferenceTo referenceTo, Node node, IFile file )
    {
        if( node.toString().equals( "class" ) && validationType.equals( ValidationType.STATIC_VALUE_UNDEFINED ) )
        {
            return NLS.bind( MESSAGE_CLASS_ATTRIBUTE_NOT_WORK, null );
        }
        else
        {
            return super.getMessageText( validationType, referenceTo, node, file );
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
        String liferayPluginValidationType = getLiferayPluginValidationType( validationType, file );

        if( liferayPluginValidationType != null )
        {
            retval =
                Platform.getPreferencesService().getInt(
                    PREFERENCE_NODE_QUALIFIER, liferayPluginValidationType, IMessage.NORMAL_SEVERITY,
                    getScopeContexts( file.getProject() ) );
        }
        else
        {
            retval = super.getServerity( validationType, file );
        }

        return retval;
    }


    @Override
    protected String getLiferayPluginValidationType( ValidationType validationType, IFile file )
    {
        String retval = null;

        if( ValidationType.PROPERTY_NOT_FOUND.equals( validationType ) )
        {
            retval = ValidationPreferences.LIFERAY_JSP_PROPERTY_NOT_FOUND;
        }
        else if( ValidationType.METHOD_NOT_FOUND.equals( validationType ) )
        {
            retval = ValidationPreferences.LIFERAY_JSP_METHOD_NOT_FOUND;
        }
        else if( ValidationType.REFERENCE_NOT_FOUND.equals( validationType ) )
        {
            retval = ValidationPreferences.LIFERAY_JSP_REFERENCE_NOT_FOUND;
        }
        else if( ValidationType.RESOURCE_NOT_FOUND.equals( validationType ) )
        {
            retval = ValidationPreferences.LIFERAY_JSP_RESOURCE_NOT_FOUND;
        }
        else if( ValidationType.STATIC_VALUE_UNDEFINED.equals( validationType ) )
        {
            retval = ValidationPreferences.LIFERAY_JSP_STATIC_VALUE_UNDEFINED;
        }
        else if( ValidationType.TYPE_HIERARCHY_INCORRECT.equals( validationType ) )
        {
            retval = ValidationPreferences.LIFERAY_JSP_TYPE_HIERARCHY_INCORRECT;
        }
        else if( ValidationType.TYPE_NOT_FOUND.equals( validationType ) )
        {
            retval = ValidationPreferences.LIFERAY_JSP_TYPE_NOT_FOUND;
        }

        return retval;
    }

    private boolean isSupportedTag( String tagName )
    {
        for( String supportTag : SUPPORTED_TAGS )
        {
            if( supportTag.equals( tagName ) )
            {
                return true;
            }
        }

        return false;
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
    protected void validateReferenceToJava(
        IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
        boolean batchMode )
    {
        if( node instanceof AttrImpl )
        {
            final AttrImpl attrNode = (AttrImpl) node;

            Node parentNode = attrNode.getOwnerElement();

            if( isSupportedTag( parentNode.getNodeName() ) )
            {
                IDOMAttr nameAttr = DOMUtils.getAttr( (IDOMElement) parentNode, "name" );

                if( nameAttr != null &&
                    ( nameAttr.getNodeValue().contains( ACTION_REQUEST_ACTION_NAME ) ||
                      nameAttr.getNodeValue().contains( JAVAX_PORTLET_ACTION ) ) )
                {
                    super.validateReferenceToJava( referenceTo, attrNode, file, validator, reporter, batchMode );
                }
            }
        }
    }

    @Override
    protected void validateReferenceToProperty(
        IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
        boolean batchMode )
    {
        final String languageKey = DOMUtils.getNodeValue( node );

        if( file.exists() && !languageKey.contains( JSP_TAG_START ) && !languageKey.contains( JSP_TAG_END ) )
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
                    }

                    if( addMessage )
                    {
                        final ValidationType validationType = getValidationType( referenceTo, nbElements );
                        final int severity = getServerity( validationType, file );

                        if( severity != ValidationMessage.IGNORE )
                        {
                            final String liferayPluginValidationType =
                                getLiferayPluginValidationType( validationType, file );
                            final String querySpecificationId = referenceTo.getQuerySpecificationId();
                            final String messageText =
                                getMessageText( validationType, referenceTo, node, file );

                            addMessage(
                                node, file, validator, reporter, batchMode, messageText, severity,
                                liferayPluginValidationType, querySpecificationId );
                        }
                    }
                }
            }
        }
    }

    protected void validateReferenceToStatic(
        IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
        boolean batchMode )
    {
        if( node instanceof AttrImpl )
        {
            final AttrImpl attrNode = (AttrImpl) node;

            if( attrNode.getOwnerElement().getNodeName().startsWith( AUI_PREFIX ) )
            {
                final String nodeValue = node.toString();

                boolean addMessage = false;

                if( nodeValue.equals( "class" ) )
                {
                    addMessage = true;
                }

                if( addMessage )
                {
                    final ValidationType validationType = getValidationType( referenceTo, 0 );
                    final int severity = getServerity( validationType, file );

                    if( severity != ValidationMessage.IGNORE )
                    {
                        final String liferayPluginValidationType = getLiferayPluginValidationType( validationType, file );
                        final String querySpecificationId = referenceTo.getQuerySpecificationId();
                        final String messageText = getMessageText( validationType, referenceTo, node, file );

                        addMessage(
                            node, file, validator, reporter, batchMode, messageText, severity, liferayPluginValidationType,
                            querySpecificationId );
                    }
                }
            }
            else
            {
                super.validateReferenceToStatic( referenceTo, attrNode, file, validator, reporter, batchMode );
            }
        }
    }

    protected static class Msgs extends NLS
    {
        public static String classAttributeNotWork;

        static
        {
            initializeMessages( LiferayJspValidator.class.getName(), Msgs.class );
        }
    }

}