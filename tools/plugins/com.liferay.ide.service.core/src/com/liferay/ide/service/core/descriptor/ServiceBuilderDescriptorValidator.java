/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.service.core.descriptor;

import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.project.core.BaseValidator;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.util.ServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jst.j2ee.internal.common.J2EECommonMessages;
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
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class ServiceBuilderDescriptorValidator extends BaseValidator
{
    public static final String MARKER_TYPE = "com.liferay.ide.service.core.serviceBuilderDescriptorMarker"; //$NON-NLS-1$

    public static final String MESSAGE_NAMESPACE_INVALID = Msgs.namespaceInvalid;

    public static final String NAMESPACE_ELEMENT = "namespace"; //$NON-NLS-1$

    public static final String PACKAGE_PATH_ATTRIBUTE = "package-path"; //$NON-NLS-1$

    public static final String PREFERENCE_NODE_QUALIFIER = LiferayProjectCore.getDefault().getBundle().getSymbolicName();

    public ServiceBuilderDescriptorValidator()
    {
        super();
    }

    protected void checkNamespaceElements(
        IDOMDocument document, IScopeContext[] preferenceScopes, String validationKey,
        List<Map<String, Object>> problems )
    {
        final NodeList namespaces = document.getElementsByTagName( NAMESPACE_ELEMENT );

        if( namespaces.getLength() > 0)
        {
            final Node item = namespaces.item( 0 );

            String namespace = NodeUtil.getTextContent( item );

            if( ! ServiceUtil.isValidNamespace( namespace ) )
            {
                Map<String, Object> problem =
                    createMarkerValues(
                        PREFERENCE_NODE_QUALIFIER, preferenceScopes, validationKey, (IDOMNode) item,
                        MESSAGE_NAMESPACE_INVALID );

                if( problem != null )
                {
                    problems.add( problem );
                }
            }
        }
    }

    protected void checkPackagePath(
        Node serviceBuilderNode, IScopeContext[] preferenceScopes, String validationKey,
        List<Map<String, Object>> problems )
    {
        Node packagePath = serviceBuilderNode.getAttributes().getNamedItem( PACKAGE_PATH_ATTRIBUTE );

        if( packagePath != null )
        {
            String packPathVal = packagePath.getNodeValue();

            if( packPathVal != null && packPathVal.trim().length() > 0 )
            {
                // Use standard java conventions to validate the package name
                IStatus javaStatus =
                    JavaConventions.validatePackageName(
                        packPathVal, CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5 );

                Map<String, Object> problem = null;

                if( javaStatus.getSeverity() == IStatus.ERROR )
                {
                    String msg = J2EECommonMessages.ERR_JAVA_PACAKGE_NAME_INVALID + javaStatus.getMessage();

                    problem =
                        createMarkerValues(
                            PREFERENCE_NODE_QUALIFIER, preferenceScopes, validationKey, (IDOMNode) packagePath, msg );
                }
                else if( javaStatus.getSeverity() == IStatus.WARNING )
                {
                    Object severity = getMessageSeverity( PREFERENCE_NODE_QUALIFIER, preferenceScopes, validationKey );

                    if( severity == null )
                    {
                        problem = null;
                    }
                    else
                    {
                        String msg = J2EECommonMessages.ERR_JAVA_PACKAGE_NAME_WARNING + javaStatus.getMessage();

                        Map<String, Object> markerValues = new HashMap<String, Object>();

                        setMarkerValues(
                            markerValues, new Integer( IMarker.SEVERITY_WARNING ), (IDOMNode) packagePath, msg );

                        problem = markerValues;
                    }
                }

                if( problem != null )
                {
                    problems.add( problem );
                }
            }
        }
    }

    protected void checkPackagePaths(
        IDOMDocument document, IScopeContext[] preferenceScopes, String validationKey, List<Map<String, Object>> problems )
    {
        final Node serviceBuilderElement = document.getDocumentElement();

        if( serviceBuilderElement != null )
        {
            checkPackagePath( serviceBuilderElement, preferenceScopes, validationKey, problems );
        }
    }

    @SuppressWarnings( { "unchecked" } )
    protected Map<String, Object>[] detectProblems(
        IJavaProject javaProject, IFile serviceXml, IScopeContext[] preferenceScopes ) throws CoreException
    {
        final List<Map<String, Object>> problems = new ArrayList<Map<String, Object>>();

        IStructuredModel model = null;

        try
        {
            model = StructuredModelManager.getModelManager().getModelForRead( serviceXml );

            if( model instanceof IDOMModel )
            {
                IDOMDocument document = ( (IDOMModel) model ).getDocument();

                checkNamespaceElements(
                    document, preferenceScopes, ValidationPreferences.SERVICE_XML_NAMESPACE_INVALID, problems );

                checkPackagePaths(
                    document, preferenceScopes, ValidationPreferences.SERVICE_XML_PACKAGE_PATH_INVALID, problems );
            }

        }
        catch( IOException e )
        {
            ServiceCore.logError( e );
        }
        finally
        {
            if( model != null )
            {
                model.releaseFromRead();
            }
        }

        Map<String, Object>[] retval = new Map[problems.size()];

        return (Map<String, Object>[]) problems.toArray( retval );
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

        IFile serviceXml = (IFile) resource;

        if( serviceXml.isAccessible() && ProjectUtil.isPortletProject( resource.getProject() ) )
        {
            final IJavaProject javaProject = JavaCore.create( serviceXml.getProject() );

            if( javaProject.exists() )
            {
                IScopeContext[] scopes = new IScopeContext[] { new InstanceScope(), new DefaultScope() };

                ProjectScope projectScope = new ProjectScope( serviceXml.getProject() );

                boolean useProjectSettings =
                    projectScope.getNode( PREFERENCE_NODE_QUALIFIER ).getBoolean(
                        LiferayProjectCore.USE_PROJECT_SETTINGS, false );

                if( useProjectSettings )
                {
                    scopes = new IScopeContext[] { projectScope, new InstanceScope(), new DefaultScope() };
                }

                try
                {
                    Map<String, Object>[] problems = detectProblems( javaProject, serviceXml, scopes );

                    for( int i = 0; i < problems.length; i++ )
                    {
                        ValidatorMessage message =
                            ValidatorMessage.create( problems[i].get( IMarker.MESSAGE ).toString(), resource );
                        message.setType( MARKER_TYPE );
                        message.setAttributes( problems[i] );
                        result.add( message );
                    }
                }
                catch( Exception e )
                {
                    ServiceCore.logError( e );
                }
            }
        }

        return result;
    }

    private static class Msgs extends NLS
    {
        public static String namespaceInvalid;

        static
        {
            initializeMessages( ServiceBuilderDescriptorValidator.class.getName(), Msgs.class );
        }
    }
}
