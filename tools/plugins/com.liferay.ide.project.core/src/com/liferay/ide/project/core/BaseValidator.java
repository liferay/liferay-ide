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

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.core.util.StringPool;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public abstract class BaseValidator extends AbstractValidator
{

    public static final String MESSAGE_CLASS_INCORRECT_HIERARCHY = Msgs.typeHierarchyIncorrect;

    public static final String MESSAGE_CLASS_NOT_FOUND = Msgs.classNotFound;

    protected IPreferencesService fPreferencesService = Platform.getPreferencesService();

    public BaseValidator()
    {
        super();
    }

    @SuppressWarnings( "unchecked" )
    protected Map<String, Object>[] checkAllClassElements(
        Map<String, String> map, IJavaProject javaProject, IFile liferayDescriptorXml, String classExistPreferenceKey,
        String classHierarchyPreferenceKey, IScopeContext[] preferenceScopes, String preferenceNodeQualifier,
        List<Map<String, Object>> problems ) throws CoreException
    {
        IStructuredModel liferayDescriptorXmlModel = null;
        IDOMDocument liferayDescriptorXmlDocument = null;

        try
        {
            liferayDescriptorXmlModel = StructuredModelManager.getModelManager().getModelForRead( liferayDescriptorXml );

            if( liferayDescriptorXmlModel != null && liferayDescriptorXmlModel instanceof IDOMModel && map != null )
            {
                liferayDescriptorXmlDocument = ( (IDOMModel) liferayDescriptorXmlModel ).getDocument();

                for( String elementName : map.keySet() )
                {
                    checkClassElements(
                        liferayDescriptorXmlDocument, javaProject, elementName, preferenceNodeQualifier,
                        preferenceScopes, classExistPreferenceKey, classHierarchyPreferenceKey, problems,
                        map.get( elementName ) );
                }
            }
        }
        catch( IOException e )
        {
            LiferayProjectCore.logError( e );
        }
        finally
        {
            if( liferayDescriptorXmlModel != null )
            {
                liferayDescriptorXmlModel.releaseFromRead();
            }
        }

        Map<String, Object>[] retval = new Map[problems.size()];

        return (Map<String, Object>[]) problems.toArray( retval );
    }

    protected Map<String, Object> checkClass(
        IJavaProject javaProject, Node classSpecifier, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, String classExistPreferenceKey, String classHierarchyPreferenceKey,
        String superTypeNames )
    {
        String className = NodeUtil.getTextContent( classSpecifier );

        if( className != null && className.length() > 0 )
        {
            IType type = null;

            try
            {
                type = javaProject.findType( className );

                if( type == null || !type.exists() || className.startsWith( "." ) ) //$NON-NLS-1$
                {
                    final String msg = MessageFormat.format( MESSAGE_CLASS_NOT_FOUND, new Object[] { className } );

                    return createMarkerValues(
                        preferenceNodeQualifier, preferenceScopes, classExistPreferenceKey, (IDOMNode) classSpecifier,
                        msg );
                }

                else if( superTypeNames != null )
                {
                    boolean typeFound = false;
                    final String[] superTypes = superTypeNames.split( StringPool.COMMA );

                    for( String superType : superTypes )
                    {
                        try
                        {
                            IType checkType = javaProject.findType( superType.trim() );

                            if( checkType != null )
                            {
                                ITypeHierarchy supertypeHierarchy = type.newSupertypeHierarchy( null );

                                if( supertypeHierarchy.contains( checkType ) )
                                {
                                    typeFound = true;
                                    break;
                                }
                            }
                        }
                        catch( JavaModelException e )
                        {
                            LiferayProjectCore.logError( e );
                        }
                    }

                    if( typeFound == false )
                    {
                        String msg =
                            MessageFormat.format( MESSAGE_CLASS_INCORRECT_HIERARCHY, className, superTypeNames );

                        if( superTypeNames.contains( StringPool.COMMA ) )
                        {
                            msg = msg.replaceAll( Msgs.typeLabel, Msgs.possibleTypes );
                        }

                        return createMarkerValues(
                            preferenceNodeQualifier, preferenceScopes, classHierarchyPreferenceKey,
                            (IDOMNode) classSpecifier, msg );
                    }
                }
            }
            catch( JavaModelException e )
            {
                return null;
            }
        }

        return null;
    }

    protected void checkClassElements(
        IDOMDocument document, IJavaProject javaProject, String classElement, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, String classExistPreferenceKey, String classHierarchyPreferenceKey,
        List<Map<String, Object>> problems, String superTypeNames )
    {
        final NodeList classes = document.getElementsByTagName( classElement );

        for( int i = 0; i < classes.getLength(); i++ )
        {
            final Node item = classes.item( i );

            final Map<String, Object> problem =
                checkClass(
                    javaProject, item, preferenceNodeQualifier, preferenceScopes, classExistPreferenceKey,
                    classHierarchyPreferenceKey, superTypeNames );

            if( problem != null )
            {
                problems.add( problem );
            }
        }
    }

    protected Map<String, Object> checkClassResource(
        IJavaProject javaProject, Node classResourceSpecifier, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, String preferenceKey, String errorMessage )
    {
        return checkClassResource(
            javaProject, classResourceSpecifier, preferenceNodeQualifier, preferenceScopes, preferenceKey,
            errorMessage, false );
    }

    protected Map<String, Object> checkClassResource(
        IJavaProject javaProject, Node classResourceSpecifier, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, String preferenceKey, String errorMessage, boolean warnPropertiesSuffix )
    {
        String classResource = NodeUtil.getTextContent( classResourceSpecifier );

        if( classResource != null && classResource.length() > 0 )
        {
            if( classResource.endsWith( ".properties" ) && warnPropertiesSuffix ) //$NON-NLS-1$
            {
                String msg =
                    MessageFormat.format( Msgs.classResourceNotEndWithProperties, new Object[] { classResource } );
                return createMarkerValues(
                    preferenceNodeQualifier, preferenceScopes, preferenceKey, (IDOMNode) classResourceSpecifier, msg );
            }

            try
            {
                IResource classResourceValue = null;
                final IWorkspaceRoot workspaceRoot = javaProject.getJavaModel().getWorkspace().getRoot();
                IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath( true );

                for( IClasspathEntry entry : classpathEntries )
                {
                    if( entry.getEntryKind() == IClasspathEntry.CPE_SOURCE )
                    {
                        IPath entryPath = entry.getPath();
                        IPath classResourcePath = entryPath.append( classResource );

                        classResourceValue = workspaceRoot.findMember( classResourcePath );

                        if( classResourceValue != null )
                        {
                            break;
                        }

                        IPath qualifiedResourcePath = entryPath.append( classResource.replaceAll( "\\.", "/" ) ); //$NON-NLS-1$ //$NON-NLS-2$

                        classResourceValue = workspaceRoot.findMember( qualifiedResourcePath );

                        if( classResourceValue != null )
                        {
                            break;
                        }

                        String resourceName = classResourcePath.lastSegment();

                        if( classResourceValue == null && classResourcePath.segmentCount() > 0 )
                        {
                            // check for a .properties of the same resource path in case of a resource bundle element
                            // that doesn't append the .properties
                            IPath parent = classResourcePath.removeLastSegments( 1 );

                            IPath propertiesClassResourcePath = parent.append( resourceName + ".properties" ); //$NON-NLS-1$

                            classResourceValue = workspaceRoot.findMember( propertiesClassResourcePath );

                            if( classResourceValue != null )
                            {
                                break;
                            }

                            propertiesClassResourcePath =
                                parent.append( resourceName.replaceAll( "\\.", "/" ) + ".properties" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                            classResourceValue = workspaceRoot.findMember( propertiesClassResourcePath );

                            if( classResourceValue != null )
                            {
                                break;
                            }
                        }

                        // IDE-1105
                        if( classResource.contains( StringPool.ASTERISK ) )
                        {
                            String classResourceRegex = classResource.replaceAll( "\\*", ".*" ); //$NON-NLS-1$ //$NON-NLS-2$

                            IResource entryResource = workspaceRoot.findMember( entryPath );

                            if( entryResource != null )
                            {
                                IFile[] propertiesFiles =  PropertiesUtil.visitPropertiesFiles( entryResource, classResourceRegex );

                                if( propertiesFiles != null && propertiesFiles.length > 0 )
                                {
                                    classResourceValue = propertiesFiles[0];
                                }
                            }

                            if( classResourceValue != null )
                            {
                                break;
                            }
                        }
                    }
                }

                if( classResourceValue == null )
                {
                    String msg = MessageFormat.format( errorMessage, new Object[] { classResource } );

                    return createMarkerValues(
                        preferenceNodeQualifier, preferenceScopes, preferenceKey, (IDOMNode) classResourceSpecifier,
                        msg );
                }
            }
            catch( JavaModelException e1 )
            {
                // no error msg
            }
        }

        return null;
    }

    protected void checkDocrootElement(
        IDOMDocument document, String element, IProject project, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, String validationKey, String messageKey, List<Map<String, Object>> problems )
    {
        NodeList elements = document.getElementsByTagName( element );

        for( int i = 0; i < elements.getLength(); i++ )
        {
            Node item = elements.item( i );

            Map<String, Object> problem =
                checkDocrootResource(
                    item, project, preferenceNodeQualifier, preferenceScopes, validationKey, messageKey );

            if( problem != null )
            {
                problems.add( problem );
            }
        }
    }

    protected Map<String, Object> checkDocrootResource(
        Node resourceSpecifier, IProject project, String preferenceNodeQualifier, IScopeContext[] preferenceScopes,
        String preferenceKey, String errorMessage )
    {
        String resourceValue = NodeUtil.getTextContent( resourceSpecifier );

        if( resourceValue != null && resourceValue.length() > 0 )
        {
            // IDE-110 IDE-648
            final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

            if( webappRoot != null )
            {
                // check for file first
                IVirtualFile resourceFile = webappRoot.getFile( resourceValue );

                if( resourceFile == null || !resourceFile.exists() )
                {
                    IVirtualFolder resourceFolder = webappRoot.getFolder( resourceValue );

                    if( resourceFolder == null || !resourceFolder.exists() )
                    {
                        String msg = MessageFormat.format( errorMessage, new Object[] { resourceValue } );

                        return createMarkerValues(
                            preferenceNodeQualifier, preferenceScopes, preferenceKey, (IDOMNode) resourceSpecifier, msg );
                    }
                }
            }
        }

        return null;
    }

    protected Map<String, Object> createMarkerValues(
        String qualifier, IScopeContext[] preferenceScopes, String preferenceKey, IDOMNode domNode, String message )
    {
        Object severity = getMessageSeverity( qualifier, preferenceScopes, preferenceKey );

        if( severity == null )
        {
            return null;
        }

        Map<String, Object> markerValues = new HashMap<String, Object>();

        setMarkerValues( markerValues, severity, domNode, message );

        return markerValues;
    }

    protected Map<String, String> getAllClasseElements( String liferayDescriptorClassElementsProperties )
    {
        Map<String, String> map = new HashMap<String, String>();
        Properties p = new Properties();
        InputStream resource = null;

        try
        {
            resource = this.getClass().getClassLoader().getResourceAsStream( liferayDescriptorClassElementsProperties );
            p.load( resource );

            for( Object key : p.keySet() )
            {
                String elementName = key.toString();
                String typeNames = p.get( key ).toString();

                map.put( elementName, typeNames );
            }
        }
        catch( IOException e )
        {
            LiferayProjectCore.logError( e );
        }
        finally
        {
            if( resource != null )
            {
                try
                {
                    resource.close();
                }
                catch( IOException e )
                {
                }
            }
        }

        return map;
    }

    protected Integer getMessageSeverity( String qualifier, IScopeContext[] preferenceScopes, String key )
    {
        int sev = fPreferencesService.getInt( qualifier, key, IMessage.NORMAL_SEVERITY, preferenceScopes );

        switch( sev )
        {
            case ValidationMessage.ERROR:
                return new Integer( IMarker.SEVERITY_ERROR );

            case ValidationMessage.WARNING:
                return new Integer( IMarker.SEVERITY_WARNING );

            case ValidationMessage.INFORMATION:
                return new Integer( IMarker.SEVERITY_INFO );

            case ValidationMessage.IGNORE:
                return null;
        }

        return new Integer( IMarker.SEVERITY_WARNING );
    }

    protected void setMarkerValues( Map<String, Object> markerValues, Object severity, IDOMNode domNode, String message )
    {
        markerValues.put( IMarker.SEVERITY, severity );

        int start = domNode.getStartOffset();

        if( domNode.getStartStructuredDocumentRegion() != null && domNode.getEndStructuredDocumentRegion() != null )
        {
            start = domNode.getStartStructuredDocumentRegion().getEndOffset();
        }

        int end = domNode.getEndOffset();

        if( domNode.getStartStructuredDocumentRegion() != null && domNode.getEndStructuredDocumentRegion() != null )
        {
            end = domNode.getEndStructuredDocumentRegion().getStartOffset();
        }

        int line = domNode.getStructuredDocument().getLineOfOffset( start );

        markerValues.put( IMarker.CHAR_START, new Integer( start ) );
        markerValues.put( IMarker.CHAR_END, new Integer( end ) );
        markerValues.put( IMarker.LINE_NUMBER, new Integer( line + 1 ) );
        markerValues.put( IMarker.MESSAGE, message );
    }

    @Override
    public boolean shouldClearMarkers( ValidationEvent event )
    {
        return true;
    }

    private static class Msgs extends NLS
    {

        public static String classNotFound;
        public static String classResourceNotEndWithProperties;
        public static String possibleTypes;
        public static String typeLabel;
        public static String typeHierarchyIncorrect;

        static
        {
            initializeMessages( BaseValidator.class.getName(), Msgs.class );
        }
    }
}
