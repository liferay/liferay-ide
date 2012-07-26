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
 *******************************************************************************/

package com.liferay.ide.portlet.core.descriptor;

import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.project.core.BaseValidator;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayHookDescriptorValidator extends BaseValidator
{

    public static final String CUSTOM_JSP_DIR_ELEMENT = "custom-jsp-dir";

    public static final String LANGUAGE_PROPERTIES_ELEMENT = "language-properties";

    public static final String MARKER_TYPE = "com.liferay.ide.portlet.core.liferayHookDescriptorMarker";

    public static final String MESSAGE_CUSTOM_JSP_DIR_NOT_FOUND =
        "The custom jsp directory {0} was not found in the docroot.";

    public static final String MESSAGE_LANGUAGE_PROPERTIES_NOT_FOUND =
        "The resource {0} was not found on the Java Build Path";

    public static final String MESSAGE_PORTAL_PROPERTIES_NOT_FOUND =
        "The resource {0} was not found on the Java Build Path";

    public static final String MESSAGE_SERVICE_IMPL_NOT_FOUND =
        "The service impl {0} was not found on the Java Build Path";

    public static final String MESSAGE_SERVICE_TYPE_NOT_FOUND =
        "The service type {0} was not found on the Java Build Path";

    public static final String PORTAL_PROPERTIES_ELEMENT = "portal-properties";

    public static final String PREFERENCE_NODE_QUALIFIER = ProjectCorePlugin.getDefault().getBundle().getSymbolicName();

    public static final String SERVICE_IMPL_ELEMENT = "service-impl";

    public static final String SERVICE_TYPE_ELEMENT = "service-type";

    public LiferayHookDescriptorValidator()
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

        IFile liferayHookXml = (IFile) resource;

        IProject project = resource.getProject();

        if( liferayHookXml.isAccessible() &&
            ( ProjectUtil.isHookProject( project ) || ProjectUtil.isPortletProject( project ) ) )
        {
            IScopeContext[] scopes = new IScopeContext[] { new InstanceScope(), new DefaultScope() };

            ProjectScope projectScope = new ProjectScope( project );

            boolean useProjectSettings =
                projectScope.getNode( PREFERENCE_NODE_QUALIFIER ).getBoolean(
                    ProjectCorePlugin.USE_PROJECT_SETTINGS, false );

            if( useProjectSettings )
            {
                scopes = new IScopeContext[] { projectScope, new InstanceScope(), new DefaultScope() };
            }

            try
            {
                Map<String, Object>[] problems = detectProblems( liferayHookXml, scopes );

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
                PortletCore.logError( e );
            }
        }

        return result;
    }

    protected void checkClassResourceElement(
        IDOMDocument document, IJavaProject javaProject, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, List<Map<String, Object>> problems, String elementName, String preferenceKey,
        String message )
    {

        NodeList elements = document.getElementsByTagName( elementName );

        for( int i = 0; i < elements.getLength(); i++ )
        {
            Node item = elements.item( i );

            Map<String, Object> problem =
                checkClassResource(
                    javaProject, item, PREFERENCE_NODE_QUALIFIER, preferenceScopes, preferenceKey, message );

            if( problem != null )
            {
                problems.add( problem );
            }
        }
    }

    protected void checkClassResourceElements(
        IDOMDocument document, IJavaProject javaProject, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, List<Map<String, Object>> problems )
    {

        checkClassResourceElement(
            document, javaProject, preferenceNodeQualifier, preferenceScopes, problems, PORTAL_PROPERTIES_ELEMENT,
            ValidationPreferences.LIFERAY_HOOK_XML_PORTAL_PROPERTIES_NOT_FOUND, MESSAGE_PORTAL_PROPERTIES_NOT_FOUND );

        checkClassResourceElement(
            document, javaProject, preferenceNodeQualifier, preferenceScopes, problems, LANGUAGE_PROPERTIES_ELEMENT,
            ValidationPreferences.LIFERAY_HOOK_XML_LANGUAGE_PROPERTIES_NOT_FOUND, MESSAGE_LANGUAGE_PROPERTIES_NOT_FOUND );
    }

    protected void checkDocrootElements(
        IDOMDocument document, IProject project, IScopeContext[] preferenceScopes, List<Map<String, Object>> problems )
    {

        checkDocrootElement(
            document, CUSTOM_JSP_DIR_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_HOOK_XML_CUSTOM_JSP_DIR_NOT_FOUND, MESSAGE_CUSTOM_JSP_DIR_NOT_FOUND, problems );
    }

    protected void checkJavaElements(
        IDOMDocument document, IJavaProject javaProject, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, List<Map<String, Object>> problems, String elementName, String preferenceKey,
        String message )
    {

        NodeList elements = document.getElementsByTagName( elementName );

        for( int i = 0; i < elements.getLength(); i++ )
        {
            Node item = elements.item( i );

            Map<String, Object> problem =
                checkClass( javaProject, item, preferenceNodeQualifier, preferenceScopes, preferenceKey, message );

            if( problem != null )
            {
                problems.add( problem );
            }
        }
    }

    protected void checkServiceElements(
        IDOMDocument document, IJavaProject javaProject, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, List<Map<String, Object>> problems )
    {

        checkJavaElements(
            document, javaProject, preferenceNodeQualifier, preferenceScopes, problems, SERVICE_TYPE_ELEMENT,
            ValidationPreferences.LIFERAY_HOOK_XML_SERVICE_TYPE_NOT_FOUND, MESSAGE_SERVICE_TYPE_NOT_FOUND );

        checkJavaElements(
            document, javaProject, preferenceNodeQualifier, preferenceScopes, problems, SERVICE_IMPL_ELEMENT,
            ValidationPreferences.LIFERAY_HOOK_XML_SERVICE_IMPL_NOT_FOUND, MESSAGE_SERVICE_IMPL_NOT_FOUND );
    }

    @SuppressWarnings( "unchecked" )
    protected Map<String, Object>[] detectProblems( IFile liferayHookXml, IScopeContext[] preferenceScopes )
        throws CoreException
    {

        List<Map<String, Object>> problems = new ArrayList<Map<String, Object>>();

        IStructuredModel liferayHookXmlModel = null;
        IDOMDocument liferayHookXmlDocument = null;

        try
        {
            liferayHookXmlModel = StructuredModelManager.getModelManager().getModelForRead( liferayHookXml );

            if( liferayHookXmlModel != null && liferayHookXmlModel instanceof IDOMModel )
            {
                liferayHookXmlDocument = ( (IDOMModel) liferayHookXmlModel ).getDocument();

                checkDocrootElements( liferayHookXmlDocument, liferayHookXml.getProject(), preferenceScopes, problems );

                final IJavaProject javaProject = JavaCore.create( liferayHookXml.getProject() );

                checkServiceElements(
                    liferayHookXmlDocument, javaProject, PREFERENCE_NODE_QUALIFIER, preferenceScopes, problems );

                checkClassResourceElements(
                    liferayHookXmlDocument, javaProject, PREFERENCE_NODE_QUALIFIER, preferenceScopes, problems );
            }

        }
        catch( IOException e )
        {

        }
        finally
        {
            if( liferayHookXmlModel != null )
            {
                liferayHookXmlModel.releaseFromRead();
            }
        }

        Map<String, Object>[] retval = new Map[problems.size()];

        return (Map<String, Object>[]) problems.toArray( retval );
    }

}
