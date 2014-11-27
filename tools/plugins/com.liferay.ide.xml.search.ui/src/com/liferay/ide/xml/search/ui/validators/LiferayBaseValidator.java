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

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.project.core.ValidationPreferences.ValidationType;
import com.liferay.ide.xml.search.ui.LiferayXMLSearchUI;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IType;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.properties.IPropertiesRequestor;
import org.eclipse.wst.xml.search.core.queryspecifications.IXMLQuerySpecification;
import org.eclipse.wst.xml.search.core.queryspecifications.XMLQuerySpecificationManager;
import org.eclipse.wst.xml.search.core.queryspecifications.requestor.IXMLSearchRequestor;
import org.eclipse.wst.xml.search.core.util.DOMUtils;
import org.eclipse.wst.xml.search.editor.references.IXMLReference;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceTo;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceToJava;
import org.eclipse.wst.xml.search.editor.references.validators.IXMLReferenceValidator;
import org.eclipse.wst.xml.search.editor.validation.IValidationResult;
import org.eclipse.wst.xml.search.editor.validation.LocalizedMessage;
import org.eclipse.wst.xml.search.editor.validation.ValidatorUtils;
import org.w3c.dom.Node;

/**
 * @author Kuo Zhang
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class LiferayBaseValidator implements IXMLReferenceValidator
{
    public static final String MARKER_QUERY_ID = "querySpecificationId";
    public static final String MESSAGE_METHOD_NOT_FOUND = Msgs.methodNotFound;
    public static final String MESSAGE_PROPERTY_NOT_FOUND = Msgs.propertyNotFound;
    public static final String MESSAGE_REFERENCE_NOT_FOUND = Msgs.referenceNotFound;
    public static final String MESSAGE_RESOURCE_NOT_FOUND = Msgs.resourceNotFound;
    public static final String MESSAGE_STATIC_VALUE_NOT_FOUND = Msgs.staticValueNotFound;
    public static final String MESSAGE_SYNTAX_INVALID = Msgs.syntaxInvalid;
    public static final String MESSAGE_TYPE_HIERARCHY_INCORRECT = Msgs.typeHierarchyIncorrect;
    public static final String MESSAGE_TYPE_NOT_FOUND = Msgs.typeNotFound;

    protected static final String PREFERENCE_NODE_QUALIFIER = ProjectCore.getDefault().getBundle().getSymbolicName();

    private class ReferencedFileVisitor implements IResourceProxyVisitor
    {
        IFile retval = null;
        IResource rootResource;
        IXMLSearchRequestor searchRequestor;

        public IFile getReferencedFile( IXMLSearchRequestor requestor, IResource rootResource )
        {
            this.searchRequestor = requestor;
            this.rootResource = rootResource;

            try
            {
                rootResource.accept( this, IContainer.EXCLUDE_DERIVED );
            }
            catch( CoreException e )
            {
                LiferayXMLSearchUI.logError( e );
            }

            return retval;
        }

        public boolean visit( IResourceProxy proxy )
        {
            try
            {
                if( proxy.getType() == IResource.FILE )
                {
                    final IFile file = (IFile) proxy.requestResource();

                    if( searchRequestor.accept( file, rootResource ) )
                    {
                        IStructuredModel model = null;

                        try
                        {
                            model = StructuredModelManager.getModelManager().getModelForRead( file );

                            if( searchRequestor.accept( model ) )
                            {
                                retval = file;
                                return false;
                            }
                        }
                        finally
                        {
                            if( model != null )
                            {
                                model.releaseFromRead();
                            }
                        }

                    }
                }
            }
            catch( Exception e )
            {
                return true;
            }

            return true;
        }
    }

    class ReferencedPropertiesVisitor implements IResourceProxyVisitor
    {

        IPropertiesRequestor propertiesRequestor;
        IFile retval = null;
        IResource rootResource;

        public IFile getReferencedFile( IPropertiesRequestor requestor, IResource rootResource )
        {
            this.propertiesRequestor = requestor;
            this.rootResource = rootResource;

            try
            {
                rootResource.accept( this, IContainer.EXCLUDE_DERIVED );
            }
            catch( CoreException e )
            {
                LiferayXMLSearchUI.logError( e );
            }

            return retval;
        }

        public boolean visit( IResourceProxy proxy )
        {
            try
            {
                if( proxy.getType() == IResource.FILE )
                {
                    final IFile file = (IFile) proxy.requestResource();

                    if( propertiesRequestor.accept( file, rootResource ) )
                    {
                        retval = file;
                        return false;
                    }
                }
            }
            catch( Exception e )
            {
                return true;
            }

            return true;
        }
    }

    protected void addMessage(
        IDOMNode node, IFile file, IValidator validator, IReporter reporter, boolean batchMode, String messageText,
        int severity)
    {
        addMessage( node, file, validator, reporter, batchMode, messageText, severity, null );
    }

    protected void addMessage(
        IDOMNode node, IFile file, IValidator validator, IReporter reporter, boolean batchMode, String messageText,
        int severity, String querySpecificationId )
    {
        final String textContent = DOMUtils.getNodeValue( node );
        int startOffset = getStartOffset( node );
        int length = textContent.trim().length() + 2;

        final LocalizedMessage message =
            createMessage( startOffset, length, messageText, severity, node.getStructuredDocument() );

        if( message != null )
        {
            if( batchMode )
            {
                reporter.removeAllMessages( validator );
                message.setTargetObject( file );
                message.setAttribute( MARKER_QUERY_ID, querySpecificationId );
                reporter.addMessage( validator, message );
            }
        }
    }

    protected LocalizedMessage createMessage(
        int start, int length, String messageText, int severity, IStructuredDocument structuredDocument )
    {
        return ValidatorUtils.createMessage( start, length, messageText, severity, structuredDocument );
    }

    protected void doValidate(
        IXMLReference reference, IDOMNode node, IFile file, IValidator validator, IReporter reporter, boolean batchMode )
    {
        if( reference.isExpression() )
        {
            return; // reference expression is not used in Liferay-IDE
        }

        if( !validateSyntax( reference, node, file, validator, reporter, batchMode ) )
        {
            return; // if the syntax is incorrect, stop validating
        }

        final List<IXMLReferenceTo> refTos = reference.getTo();

        // refTos.size() == 0 means the reference is only used for syntax validation, which is already done in last
        // step, and multiple reference-tos are not used in Liferay-IDE
        if( refTos == null || refTos.size() != 1 )
        {
            return;
        }

        final IXMLReferenceTo referenceTo = refTos.get( 0 );

        switch( referenceTo.getType() )
        {
            case XML:
                validateReferenceToXML( referenceTo, node, file, validator, reporter, batchMode );
                break;
            case JAVA:
                validateReferenceToJava( referenceTo, node, file, validator, reporter, batchMode );
                break;
            case JAVA_METHOD:
                validateReferenceToJavaMethod( referenceTo, node, file, validator, reporter, batchMode );
                break;
            case RESOURCE:
                validateReferenceToResource( referenceTo, node, file, validator, reporter, batchMode );
                break;
            case PROPERTY:
                validateReferenceToProperty( referenceTo, node, file, validator, reporter, batchMode );
                break;
            case STATIC:
                validateReferenceToStatic( referenceTo, node, file, validator, reporter, batchMode );
                break;
            default:
                return;
        }

    }

    protected String getMessageText( ValidationType validationType, IXMLReferenceTo referenceTo, Node node, IFile file )
    {
        final String textContent = DOMUtils.getNodeValue( node );

        if( textContent == null )
        {
            return null;
        }

        switch( validationType )
        {
        case SYNTAX_INVALID:
            return NLS.bind( MESSAGE_SYNTAX_INVALID, textContent );
        case TYPE_NOT_FOUND:
            return NLS.bind( MESSAGE_TYPE_NOT_FOUND, textContent );
        case METHOD_NOT_FOUND:
            return NLS.bind( MESSAGE_METHOD_NOT_FOUND, textContent );
        case TYPE_HIERARCHY_INCORRECT:
        {
            if( referenceTo != null && referenceTo.getType() == IXMLReferenceTo.ToType.JAVA && file != null )
            {
                IType[] superTypes = ( (IXMLReferenceToJava) referenceTo ).getExtends( node, file );

                if( superTypes != null && superTypes.length > 0 )
                {
                    StringBuilder sb = new StringBuilder();
                    for( IType type : superTypes )
                    {
                        sb.append( type.getFullyQualifiedName() );
                        sb.append( ", " );
                    }

                    final String superTypeNames = sb.toString().replaceAll( ", $", "" );
                    return NLS.bind( MESSAGE_TYPE_HIERARCHY_INCORRECT, textContent, superTypeNames );
                }
            }
        }
        case RESOURCE_NOT_FOUND:
            return NLS.bind( MESSAGE_RESOURCE_NOT_FOUND, textContent );
        case REFERENCE_NOT_FOUND:
            final IFile referencedFile = getReferencedFile( referenceTo, node, file );
            return NLS.bind( MESSAGE_REFERENCE_NOT_FOUND, textContent, referencedFile != null ? referencedFile.getName() : "" );
        case PROPERTY_NOT_FOUND:
            final IFile languagePropertiesFile = getReferencedFile( referenceTo, node, file );
            return NLS.bind( MESSAGE_PROPERTY_NOT_FOUND, textContent, languagePropertiesFile != null
                ? languagePropertiesFile.getName() : "any resource files" );
        case STATIC_NOT_FOUND:
            return NLS.bind( MESSAGE_STATIC_VALUE_NOT_FOUND, textContent );
        }

        return null;
    }

    protected String getMessageText( ValidationType validationType, Node node )
    {
        return getMessageText( validationType, null, node, null );
    }

    /**
     * get the exactly file which contains the element referenced by another xml element
     */
    protected IFile getReferencedFile( IXMLReferenceTo referenceTo, Node node, IFile file )
    {
        IXMLQuerySpecification querySpecification =
            XMLQuerySpecificationManager.getDefault().getQuerySpecification( referenceTo.getQuerySpecificationId() );

        if( !querySpecification.isMultiResource() )
        {
            IResource resource = querySpecification.getResource( node, file );

            IXMLSearchRequestor requestor = querySpecification.getRequestor();

            return new ReferencedFileVisitor().getReferencedFile( requestor, resource );
        }

        return null;
    }

    protected IScopeContext[] getScopeContexts( IProject project )
    {
        final ProjectScope projectScope = new ProjectScope( project );

        return projectScope.getNode( PREFERENCE_NODE_QUALIFIER ).getBoolean(
            ProjectCore.USE_PROJECT_SETTINGS, false ) ? new IScopeContext[] { projectScope,
            InstanceScope.INSTANCE, DefaultScope.INSTANCE } : new IScopeContext[] { InstanceScope.INSTANCE,
            DefaultScope.INSTANCE };
    }

    protected int getServerity( ValidationType validationType, IFile file )
    {
        final String validationKey = ValidationPreferences.getValidationPreferenceKey( file.getName(), validationType );

        // get severity from users' settings
        return Platform.getPreferencesService().getInt(
            PREFERENCE_NODE_QUALIFIER, validationKey, IMessage.NORMAL_SEVERITY, getScopeContexts( file.getProject() ) );
    }

    protected int getStartOffset( IDOMNode node )
    {
        int nodeType = node.getNodeType();

        switch( nodeType )
        {
            case Node.ATTRIBUTE_NODE:
                return ( (IDOMAttr) node ).getValueRegionStartOffset();
        }

        return node.getStartOffset();
    }

    protected ValidationType getValidationType( IXMLReferenceTo referenceTo, int nbElements )
    {
        switch( referenceTo.getType() )
        {
            case XML:
                return ValidationType.REFERENCE_NOT_FOUND;
            case JAVA:
                if( nbElements == -1 )
                {
                    return ValidationType.TYPE_HIERARCHY_INCORRECT;
                }
                return ValidationType.TYPE_NOT_FOUND;
            case JAVA_METHOD:
                return ValidationType.METHOD_NOT_FOUND;
            case RESOURCE:
                return ValidationType.RESOURCE_NOT_FOUND;
            case PROPERTY:
                return ValidationType.PROPERTY_NOT_FOUND;
            case STATIC:
                return ValidationType.STATIC_NOT_FOUND;
            default:
                return null;
        }
    }

    protected boolean isMultipleElementsAllowed( IDOMNode node, int nbElements )
    {
        return true;
    }

    /**
     * Subclasses override the method to set their own markers
     */
    protected void setMarker( IValidator validator, IFile file )
    {
    }

    @Override
    public void validate( IXMLReference reference, IDOMNode node, IFile file,
                          IValidator validator, IReporter reporter, boolean batchMode )
    {
        if( reference != null )
        {
            setMarker( validator, file );
            doValidate( reference, node, file, validator, reporter, batchMode );
        }
    }

    /**
     * default implementation of all kinds of validation
     */
    protected void validateReferenceToAllType( IXMLReferenceTo referenceTo, IDOMNode node,IFile file,
                                               IValidator validator, IReporter reporter, boolean batchMode )
    {
        final String nodeValue = DOMUtils.getNodeValue( node );

        final IValidationResult result =
            referenceTo.getSearcher().searchForValidation( node, nodeValue, -1, -1, file, referenceTo );

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
                ValidationType validationType = getValidationType( referenceTo, nbElements );
                int severity = getServerity( validationType, file );

                if( severity != ValidationMessage.IGNORE )
                {
                    final String messageText = getMessageText( validationType, referenceTo, node, file );
                    addMessage( node, file, validator, reporter, batchMode, messageText, severity, referenceTo.getQuerySpecificationId() );
                }
            }
        }
    }

    protected void validateReferenceToJava( IXMLReferenceTo referenceTo, IDOMNode node, IFile file,
                                            IValidator validator, IReporter reporter, boolean batchMode )
    {
        validateReferenceToAllType( referenceTo, node, file, validator, reporter, batchMode );
    }

    protected void validateReferenceToJavaMethod(
        IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
        boolean batchMode )
    {
        validateReferenceToAllType( referenceTo, node, file, validator, reporter, batchMode );
    }

    protected void validateReferenceToProperty(
        IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
        boolean batchMode )
    {
        validateReferenceToAllType( referenceTo, node, file, validator, reporter, batchMode );
    }

    protected void validateReferenceToResource( IXMLReferenceTo referenceTo, IDOMNode node, IFile file,
                                                IValidator validator, IReporter reporter, boolean batchMode )
    {
        validateReferenceToAllType( referenceTo, node, file, validator, reporter, batchMode );
    }

    protected void validateReferenceToStatic(
        IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
        boolean batchMode )
    {
        validateReferenceToAllType( referenceTo, node, file, validator, reporter, batchMode );
    }

    protected void validateReferenceToXML( IXMLReferenceTo referenceTo, IDOMNode node, IFile file,
                                           IValidator validator, IReporter reporter, boolean batchMode )
    {
        validateReferenceToAllType( referenceTo, node, file, validator, reporter, batchMode );
    }

    protected boolean validateSyntax( IXMLReference reference, IDOMNode node, IFile file,
                                      IValidator validator, IReporter reporter, boolean batchMode )
    {
        return true;
    }

    protected static class Msgs extends NLS
    {
        public static String propertyNotFound;
        public static String referenceNotFound;
        public static String resourceNotFound;
        public static String staticValueNotFound;
        public static String syntaxInvalid;
        public static String typeHierarchyIncorrect;
        public static String typeNotFound;
        public static String methodNotFound;

        static
        {
            initializeMessages( LiferayBaseValidator.class.getName(), Msgs.class );
        }
    }
}
