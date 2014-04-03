/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.core.descriptor;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.project.core.BaseValidator;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 * @author Cindy Li
 * @author Tao Tao
 */
@SuppressWarnings( "restriction" )
public class LiferayDisplayDescriptorValidator extends BaseValidator
{

    public static final String CATEGORY_ELEMENT = "category";

    public static final String MARKER_TYPE = "com.liferay.ide.portlet.core.liferayDisplayDescriptorMarker"; //$NON-NLS-1$

    public static final String MESSAGE_CATEGORY_NAME_IS_EMPTY = Msgs.categoryNameCannotBeEmpty;

    public static final String MESSAGE_PORTLET_NAME_NOT_FOUND = Msgs.portletIdNotMatchPortletName;

    public static final String PORTLET_ELEMENT = "portlet"; //$NON-NLS-1$

    public static final String PORTLET_NAME_ELEMENT = "portlet-name"; //$NON-NLS-1$

    public static final String PREFERENCE_NODE_QUALIFIER = LiferayProjectCore.getDefault().getBundle().getSymbolicName();

    public LiferayDisplayDescriptorValidator()
    {
        super();
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public ValidationResult validate( IResource resource, int kind, ValidationState state, IProgressMonitor monitor )
    {
        if( resource.getType() != IResource.FILE )
        {
            return null;
        }

        ValidationResult result = new ValidationResult();

        IFile liferayDisplayXml = (IFile) resource;

        IFile portletXml =
            CoreUtil.getDescriptorFile( liferayDisplayXml.getProject(), ILiferayConstants.PORTLET_XML_FILE );

        if( liferayDisplayXml.isAccessible() && portletXml != null && portletXml.isAccessible() &&
            ProjectUtil.isPortletProject( resource.getProject() ) )
        {

            IScopeContext[] scopes = new IScopeContext[] { new InstanceScope(), new DefaultScope() };

            ProjectScope projectScope = new ProjectScope( liferayDisplayXml.getProject() );

            boolean useProjectSettings =
                projectScope.getNode( PREFERENCE_NODE_QUALIFIER ).getBoolean(
                    LiferayProjectCore.USE_PROJECT_SETTINGS, false );

            if( useProjectSettings )
            {
                scopes = new IScopeContext[] { projectScope, new InstanceScope(), new DefaultScope() };
            }

            try
            {
                Map<String, Object>[] problems = detectProblems( liferayDisplayXml, portletXml, scopes );

                for( int i = 0; i < problems.length; i++ )
                {
                    ValidatorMessage message =
                        ValidatorMessage.create( problems[i].get( IMarker.MESSAGE ).toString(), resource );
                    message.setType( MARKER_TYPE );
                    message.setAttributes( problems[i] );
                    result.add( message );
                }

                if( problems.length > 0 )
                {
                    if( portletXml != null && portletXml.exists() )
                    {
                        result.setDependsOn( new IResource[] { portletXml } );
                    }
                }
            }
            catch( Exception e )
            {
                PortletCore.logError( e );
            }
        }

        return result;
    }

    protected void checkCategoryName(
        IDOMDocument liferayDisplayDocument, Node categoryNameNode, IScopeContext[] preferenceScopes,
        String validationKey, String errorMessage, List<Map<String, Object>> problems )
    {
        Node categoryName = categoryNameNode.getAttributes().getNamedItem( "name" );

        if( categoryName != null )
        {
            String categoryNameValue = categoryName.getNodeValue();

            if( categoryNameValue.matches( "\\s*" ) )
            {
                String msg = MessageFormat.format( errorMessage, new Object[] { categoryNameValue } );

                Map<String, Object> problem =
                    createMarkerValues(
                        PREFERENCE_NODE_QUALIFIER, preferenceScopes, validationKey, (IDOMNode) categoryName, msg );

                if( problem != null )
                {
                    problems.add( problem );
                }
            }
        }
    }

    protected void checkCategoryNames(
        IDOMDocument liferayDisplayDocument, IScopeContext[] preferenceScopes, List<Map<String, Object>> problems )
    {
        NodeList elements = liferayDisplayDocument.getElementsByTagName( CATEGORY_ELEMENT );

        for( int i = 0; i < elements.getLength(); i++ )
        {
            Node categoryElement = elements.item( i );

            checkCategoryName(
                liferayDisplayDocument, categoryElement, preferenceScopes,
                ValidationPreferences.LIFERAY_DISPLAY_XML_CATEGORY_NAME_INVALID, MESSAGE_CATEGORY_NAME_IS_EMPTY,
                problems );
        }
    }

    protected void checkPortletId(
        IDOMDocument portletXmlDocument, Node portletIdNode, IScopeContext[] preferenceScopes, String validationKey,
        String errorMessage, List<Map<String, Object>> problems )
    {

        NodeList elements = portletXmlDocument.getElementsByTagName( PORTLET_NAME_ELEMENT );

        Node portletId = portletIdNode.getAttributes().getNamedItem( "id" ); //$NON-NLS-1$

        if( portletId != null )
        {
            String portletIdValue = portletId.getNodeValue();

            boolean portletNameFound = false;

            for( int i = 0; i < elements.getLength(); i++ )
            {
                Node item = elements.item( i );
                String portletName = NodeUtil.getTextContent( item );

                if( CoreUtil.isEqual( portletName, portletIdValue ) )
                {
                    portletNameFound = true;

                    break;
                }
            }

            if( !portletNameFound )
            {
                String msg = MessageFormat.format( errorMessage, new Object[] { portletIdValue } );

                Map<String, Object> problem =
                    createMarkerValues(
                        PREFERENCE_NODE_QUALIFIER, preferenceScopes, validationKey, (IDOMNode) portletIdNode, msg );

                if( problem != null )
                {
                    problems.add( problem );
                }
            }
        }

    }

    protected void checkPortletIds(
        IDOMDocument liferayDisplayDocument, IDOMDocument portletXmlDocument, IScopeContext[] preferenceScopes,
        List<Map<String, Object>> problems )
    {

        NodeList elements = liferayDisplayDocument.getElementsByTagName( PORTLET_ELEMENT );

        for( int i = 0; i < elements.getLength(); i++ )
        {
            Node portletElement = elements.item( i );

            checkPortletId(
                portletXmlDocument, portletElement, preferenceScopes,
                ValidationPreferences.LIFERAY_DISPLAY_XML_PORTLET_ID_NOT_FOUND, MESSAGE_PORTLET_NAME_NOT_FOUND,
                problems );
        }
    }

    @SuppressWarnings( "unchecked" )
    protected Map<String, Object>[] detectProblems(
        IFile liferayDisplayXml, IFile portletXml, IScopeContext[] preferenceScopes ) throws CoreException
    {

        List<Map<String, Object>> problems = new ArrayList<Map<String, Object>>();

        IStructuredModel liferayDisplayModel = null;
        IStructuredModel portletXmlModel = null;
        IDOMDocument liferayDisplayDocument = null;

        try
        {
            liferayDisplayModel = StructuredModelManager.getModelManager().getModelForRead( liferayDisplayXml );

            if( liferayDisplayModel != null && liferayDisplayModel instanceof IDOMModel )
            {
                liferayDisplayDocument = ( (IDOMModel) liferayDisplayModel ).getDocument();

                if( portletXml != null && portletXml.exists() )
                {
                    portletXmlModel = StructuredModelManager.getModelManager().getModelForRead( portletXml );

                    if( portletXmlModel instanceof IDOMModel )
                    {
                        IDOMDocument portletXmlDocument = ( (IDOMModel) portletXmlModel ).getDocument();

                        checkPortletIds( liferayDisplayDocument, portletXmlDocument, preferenceScopes, problems );
                        checkCategoryNames( liferayDisplayDocument, preferenceScopes, problems );
                    }
                }
            }
        }
        catch( IOException e )
        {

        }
        finally
        {
            if( liferayDisplayModel != null )
            {
                liferayDisplayModel.releaseFromRead();
            }

            if( portletXmlModel != null )
            {
                portletXmlModel.releaseFromRead();
            }
        }

        Map<String, Object>[] retval = new Map[problems.size()];

        return (Map<String, Object>[]) problems.toArray( retval );
    }

    private static class Msgs extends NLS
    {
        public static String categoryNameCannotBeEmpty;
        public static String portletIdNotMatchPortletName;

        static
        {
            initializeMessages( LiferayDisplayDescriptorValidator.class.getName(), Msgs.class );
        }
    }
}
