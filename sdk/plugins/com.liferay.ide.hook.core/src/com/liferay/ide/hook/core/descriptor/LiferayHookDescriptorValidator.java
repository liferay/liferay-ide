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

package com.liferay.ide.hook.core.descriptor;

import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.project.core.BaseValidator;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.IOException;
import java.text.MessageFormat;
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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class LiferayHookDescriptorValidator extends BaseValidator
{

    public static final String CUSTOM_JSP_DIR_ELEMENT = "custom-jsp-dir";

    public static final String LANGUAGE_PROPERTIES_ELEMENT = "language-properties";

    public static final String MARKER_TYPE = "com.liferay.ide.portlet.core.liferayHookDescriptorMarker";

    public static final String MESSAGE_CUSTOM_JSP_DIR_NOT_FOUND =
        "The custom jsp directory {0} was not found in the web app.";

    public static final String MESSAGE_LANGUAGE_PROPERTIES_NOT_FOUND =
        "The resource {0} was not found on the Java Build Path";

    public static final String MESSAGE_PORTAL_PROPERTIES_NOT_FOUND =
        "The resource {0} was not found on the Java Build Path";

    public static final String MESSAGE_SERVICE_IMPL_NOT_FOUND =
        "The service impl {0} was not found on the Java Build Path";

    private static final String MESSAGE_SERVICE_TYPE_INVALID = "Service type is invalid.";

    private static final String MESSAGE_SERVICE_TYPE_IS_NOT_INTERFACE = "The service type {0} is not an interface.";

    public static final String MESSAGE_SERVICE_TYPE_NOT_FOUND =
        "The service type {0} was not found on the Java Build Path";

    public static final String PORTAL_PROPERTIES_ELEMENT = "portal-properties";

    public static final String PREFERENCE_NODE_QUALIFIER = ProjectCorePlugin.getDefault().getBundle().getSymbolicName();

    private static final String SERVICE_ELEMENT = "service";

    public static final String SERVICE_IMPL_ELEMENT = "service-impl";

    public static final String SERVICE_TYPE_ELEMENT = "service-type";

    public LiferayHookDescriptorValidator()
    {
        super();
    }

    protected void checkClassResourceElement(
        IDOMDocument document, IJavaProject javaProject, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, List<Map<String, Object>> problems, String elementName, String preferenceKey,
        String message )
    {
        final NodeList elements = document.getElementsByTagName( elementName );

        for( int i = 0; i < elements.getLength(); i++ )
        {
            final Node item = elements.item( i );

            final Map<String, Object> problem =
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

    protected void checkServiceElements(
        IDOMDocument document, IJavaProject javaProject, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, List<Map<String, Object>> problems )
    {
        final NodeList elements = document.getElementsByTagName( SERVICE_ELEMENT );

        for( int i = 0; i < elements.getLength(); i++ )
        {
            final Element serviceTag = (Element) elements.item( i );

            final NodeList serviceTypes = serviceTag.getElementsByTagName( SERVICE_TYPE_ELEMENT );
            final NodeList serviceImpls = serviceTag.getElementsByTagName( SERVICE_IMPL_ELEMENT );

            final Node itemServiceType = serviceTypes.item( 0 );

            Map<String, Object> problem = checkClass(
                    javaProject, itemServiceType, preferenceNodeQualifier, preferenceScopes,
                    ValidationPreferences.LIFERAY_HOOK_XML_CLASS_NOT_FOUND,
                    ValidationPreferences.LIFERAY_HOOK_XML_INCORRECT_CLASS_HIERARCHY, null );

            if( problem != null )
            {
                problems.add( problem );
            }
            else
            {
                final String serviceTypeContent = NodeUtil.getTextContent( itemServiceType );

                if( serviceTypeContent != null && serviceTypeContent.length() > 0 )
                {
                    try
                    {
                        final IType typeServiceType = javaProject.findType( serviceTypeContent );

                        if( !typeServiceType.isInterface() )
                        {
                            String msg =
                                MessageFormat.format(
                                    MESSAGE_SERVICE_TYPE_IS_NOT_INTERFACE, new Object[] { serviceTypeContent } );

                            problem =
                                createMarkerValues(
                                    preferenceNodeQualifier, preferenceScopes,
                                    ValidationPreferences.LIFERAY_HOOK_XML_INCORRECT_CLASS_HIERARCHY,
                                    (IDOMNode) itemServiceType, msg );

                            if( problem != null )
                            {
                                problems.add( problem );
                            }
                        }
                        else if( !serviceTypeContent.matches( "com.liferay.*Service" ) )
                        {
                            problem =
                                createMarkerValues(
                                    preferenceNodeQualifier, preferenceScopes,
                                    ValidationPreferences.LIFERAY_HOOK_XML_INCORRECT_CLASS_HIERARCHY,
                                    (IDOMNode) itemServiceType, MESSAGE_SERVICE_TYPE_INVALID );

                            if( problem != null )
                            {
                                problems.add( problem );
                            }
                        }
                        else
                        {
                            final Node itemServiceImpl = serviceImpls.item( 0 );

                            problem =
                                checkClass(
                                    javaProject, itemServiceImpl, preferenceNodeQualifier, preferenceScopes,
                                    ValidationPreferences.LIFERAY_HOOK_XML_CLASS_NOT_FOUND,
                                    ValidationPreferences.LIFERAY_HOOK_XML_INCORRECT_CLASS_HIERARCHY,
                                    serviceTypeContent + "Wrapper" );

                            if( problem != null )
                            {
                                problems.add( problem );
                            }
                        }
                    }
                    catch( JavaModelException e )
                    {
                        HookCore.logError( e );
                    }
                }
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    protected Map<String, Object>[] detectProblems( IFile liferayHookXml, IScopeContext[] preferenceScopes )
        throws CoreException
    {
        final List<Map<String, Object>> problems = new ArrayList<Map<String, Object>>();

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

                Map<String, String> map = getAllClasseElements( "LiferayHookClassElements.properties" );

                checkAllClassElements(
                    map, javaProject, liferayHookXml, ValidationPreferences.LIFERAY_HOOK_XML_CLASS_NOT_FOUND,
                    ValidationPreferences.LIFERAY_HOOK_XML_INCORRECT_CLASS_HIERARCHY, preferenceScopes,
                    PREFERENCE_NODE_QUALIFIER, problems );
            }

        }
        catch( IOException e )
        {
            HookCore.logError( e );
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
                HookCore.logError( e );
            }
        }

        return result;
    }

}
