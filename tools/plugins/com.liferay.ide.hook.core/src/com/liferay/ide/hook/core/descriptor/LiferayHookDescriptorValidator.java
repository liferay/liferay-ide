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

package com.liferay.ide.hook.core.descriptor;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.project.core.BaseValidator;
import com.liferay.ide.project.core.ProjectCore;
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
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
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class LiferayHookDescriptorValidator extends BaseValidator
{

    public static final String CUSTOM_JSP_DIR_ELEMENT = "custom-jsp-dir"; //$NON-NLS-1$

    public static final String LANGUAGE_PROPERTIES_ELEMENT = "language-properties"; //$NON-NLS-1$

    public static final String MARKER_TYPE = "com.liferay.ide.hook.core.liferayHookDescriptorMarker"; //$NON-NLS-1$

    public static final String MESSAGE_CUSTOM_JSP_DIR_NOT_FOUND = Msgs.customJspDirectoryNotFound;

    public static final String MESSAGE_RESOURCE_NOT_FOUND = Msgs.resourceNotFound;

    public static final String MESSAGE_PRORETIES_VALUE_END_WITH_PROPERTIES = Msgs.propertiesValueEndWithProperties;

    public static final String MESSAGE_SERVICE_IMPL_NOT_FOUND = Msgs.serviceImplNotFound;

    private static final String MESSAGE_SERVICE_TYPE_INVALID = Msgs.serviceTypeInvalid;

    private static final String MESSAGE_SERVICE_TYPE_IS_NOT_INTERFACE = Msgs.serviceTypeNotInterface;

    public static final String MESSAGE_SERVICE_TYPE_NOT_FOUND = Msgs.serviceTypeNotFound;

    public static final String PORTAL_PROPERTIES_ELEMENT = "portal-properties"; //$NON-NLS-1$

    public static final String PREFERENCE_NODE_QUALIFIER =
        ProjectCore.getDefault().getBundle().getSymbolicName();

    private static final String SERVICE_ELEMENT = "service"; //$NON-NLS-1$

    public static final String SERVICE_IMPL_ELEMENT = "service-impl"; //$NON-NLS-1$

    public static final String SERVICE_TYPE_ELEMENT = "service-type"; //$NON-NLS-1$

    public LiferayHookDescriptorValidator()
    {
        super();
    }

    protected Map<String, Object> checkClassResource( IJavaProject javaProject, 
                                                      Node classResourceSpecifier,
                                                      String preferenceNodeQualifier,
                                                      IScopeContext[] preferenceScopes )
    {
        String classResource =
            NodeUtil.getTextContent( classResourceSpecifier ).replaceAll( "(^\\s*)|(\\s*$)", StringPool.BLANK );

        if( classResource == null || classResource.length() == 0 )
        {
            return null;
        }

        if( ! classResource.endsWith( ".properties" ) )
        {
            final String elementName = classResourceSpecifier.getLocalName();
            String preferenceKey = null; 

            if( elementName.equals( PORTAL_PROPERTIES_ELEMENT ) )
            {
                preferenceKey = ValidationPreferences.LIFERAY_HOOK_XML_SYNTAX_INVALID;
            }
            else if( elementName.equals( LANGUAGE_PROPERTIES_ELEMENT ) )
            {
                preferenceKey = ValidationPreferences.LIFERAY_HOOK_XML_SYNTAX_INVALID;
            }

            final String msg = MessageFormat.format( MESSAGE_PRORETIES_VALUE_END_WITH_PROPERTIES, new Object[] { classResource } );

            return createMarkerValues( 
                preferenceNodeQualifier, preferenceScopes, preferenceKey, (IDOMNode) classResourceSpecifier, msg );
        }

        final IPath[] entryPaths = getSourceEntries( javaProject );

        IResource classResourceValue = null;

        for( IPath entryPath : entryPaths )
        {
            IResource srcFolder = CoreUtil.getWorkspaceRoot().getFolder( entryPath );

            if( srcFolder != null && srcFolder.exists() )
            {
                String[] namePatterns = PropertiesUtil.
                    generatePropertiesNamePatternsForValidation( classResource, classResourceSpecifier.getNodeName() );

                for( String pattern : namePatterns )
                {
                    if( pattern != null )
                    {
                        IFile[] propertiesFiles = PropertiesUtil.visitPropertiesFiles( srcFolder, pattern );

                        if( propertiesFiles != null && propertiesFiles.length > 0 )
                        {
                            classResourceValue = propertiesFiles[0];
                            break;
                        }
                    }
                }
            }

            if( classResourceValue != null )
            {
                break;
            }
        }

        if( classResourceValue == null )
        {
            final String msg = MessageFormat.format( MESSAGE_RESOURCE_NOT_FOUND, new Object[] { classResource } );

            final String elementName = classResourceSpecifier.getLocalName();
            String preferenceKey = null; 

            if( elementName.equals( PORTAL_PROPERTIES_ELEMENT ) )
            {
                preferenceKey = ValidationPreferences.LIFERAY_HOOK_XML_RESOURCE_NOT_FOUND;
            }
            else if( elementName.equals( LANGUAGE_PROPERTIES_ELEMENT ) )
            {
                preferenceKey = ValidationPreferences.LIFERAY_HOOK_XML_RESOURCE_NOT_FOUND;
            }

            return createMarkerValues(
                preferenceNodeQualifier, preferenceScopes, preferenceKey, (IDOMNode) classResourceSpecifier, msg );
        }

        return null;
    }

    protected void checkClassResourceElement(
        IDOMDocument document, IJavaProject javaProject, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, List<Map<String, Object>> problems, String elementName )
    {
        final NodeList elements = document.getElementsByTagName( elementName );

        for( int i = 0; i < elements.getLength(); i++ )
        {
            final Node item = elements.item( i );

            final Map<String, Object> problem = 
                checkClassResource( javaProject, item, PREFERENCE_NODE_QUALIFIER, preferenceScopes );

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
            document, javaProject, preferenceNodeQualifier, preferenceScopes, problems, PORTAL_PROPERTIES_ELEMENT );

        checkClassResourceElement(
            document, javaProject, preferenceNodeQualifier, preferenceScopes, problems, LANGUAGE_PROPERTIES_ELEMENT );
    }

    protected void checkDocrootElements(
        IDOMDocument document, IProject project, IScopeContext[] preferenceScopes, List<Map<String, Object>> problems )
    {
        checkDocrootElement(
            document, CUSTOM_JSP_DIR_ELEMENT, project, PREFERENCE_NODE_QUALIFIER, preferenceScopes,
            ValidationPreferences.LIFERAY_HOOK_XML_RESOURCE_NOT_FOUND, MESSAGE_CUSTOM_JSP_DIR_NOT_FOUND, problems );
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

            final Node itemServiceType = serviceTypes.item( 0 );

            boolean serviceTypeValid = false;

            String serviceTypeContent = null;

            Map<String, Object> problem = null;

            if( itemServiceType != null )
            {
                serviceTypeContent = NodeUtil.getTextContent( itemServiceType );

                problem =
                    checkClass(
                        javaProject, itemServiceType, preferenceNodeQualifier, preferenceScopes,
                        ValidationPreferences.LIFERAY_HOOK_XML_TYPE_NOT_FOUND,
                        ValidationPreferences.LIFERAY_HOOK_XML_TYPE_HIERARCHY_INCORRECT, null );

                if( problem != null )
                {
                    problems.add( problem );
                }
                else
                {
                    if( serviceTypeContent != null && serviceTypeContent.length() > 0 )
                    {
                        try
                        {
                            final IType typeServiceType = javaProject.findType( serviceTypeContent );

                            if( ! typeServiceType.isInterface() )
                            {
                                String msg =
                                    MessageFormat.format(
                                        MESSAGE_SERVICE_TYPE_IS_NOT_INTERFACE, new Object[] { serviceTypeContent } );

                                problem =
                                    createMarkerValues(
                                        preferenceNodeQualifier, preferenceScopes,
                                        ValidationPreferences.LIFERAY_HOOK_XML_TYPE_HIERARCHY_INCORRECT,
                                        (IDOMNode) itemServiceType, msg );

                                if( problem != null )
                                {
                                    problems.add( problem );
                                }
                            }
                            else if( ! serviceTypeContent.matches( "com.liferay.*Service" ) ) //$NON-NLS-1$
                            {
                                problem =
                                    createMarkerValues(
                                        preferenceNodeQualifier, preferenceScopes,
                                        ValidationPreferences.LIFERAY_HOOK_XML_TYPE_HIERARCHY_INCORRECT,
                                        (IDOMNode) itemServiceType, MESSAGE_SERVICE_TYPE_INVALID );

                                if( problem != null )
                                {
                                    problems.add( problem );
                                }
                            }
                            else
                            {
                                serviceTypeValid = true;
                            }
                        }
                        catch( JavaModelException e )
                        {
                            HookCore.logError( e );
                        }
                    }
                }
            }

            final NodeList serviceImpls = serviceTag.getElementsByTagName( SERVICE_IMPL_ELEMENT );

            final Node itemServiceImpl = serviceImpls.item( 0 );

            if( itemServiceImpl != null )
            {
                if( serviceTypeValid )
                {
                    problem =
                        checkClass(
                            javaProject, itemServiceImpl, preferenceNodeQualifier, preferenceScopes,
                            ValidationPreferences.LIFERAY_HOOK_XML_TYPE_NOT_FOUND,
                            ValidationPreferences.LIFERAY_HOOK_XML_TYPE_HIERARCHY_INCORRECT, serviceTypeContent +
                                "Wrapper" ); //$NON-NLS-1$

                }
                else
                {
                    problem =
                        checkClass(
                            javaProject, itemServiceImpl, preferenceNodeQualifier, preferenceScopes,
                            ValidationPreferences.LIFERAY_HOOK_XML_TYPE_NOT_FOUND,
                            ValidationPreferences.LIFERAY_HOOK_XML_TYPE_HIERARCHY_INCORRECT, null );

                }
                if( problem != null )
                {
                    problems.add( problem );
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

                Map<String, String> map = getAllClasseElements( "LiferayHookClassElements.properties" ); //$NON-NLS-1$

                checkAllClassElements(
                    map, javaProject, liferayHookXml, ValidationPreferences.LIFERAY_HOOK_XML_TYPE_NOT_FOUND,
                    ValidationPreferences.LIFERAY_HOOK_XML_TYPE_HIERARCHY_INCORRECT, preferenceScopes,
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
                    ProjectCore.USE_PROJECT_SETTINGS, false );

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

    private static class Msgs extends NLS
    {

        public static String customJspDirectoryNotFound;
        public static String propertiesValueEndWithProperties;
        public static String resourceNotFound;
        public static String serviceImplNotFound;
        public static String serviceTypeInvalid;
        public static String serviceTypeNotFound;
        public static String serviceTypeNotInterface;

        static
        {
            initializeMessages( LiferayHookDescriptorValidator.class.getName(), Msgs.class );
        }
    }
}
