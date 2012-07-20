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

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public abstract class BaseValidator extends AbstractValidator
{

    protected IPreferencesService fPreferencesService = Platform.getPreferencesService();

    public BaseValidator()
    {
        super();
    }

    @Override
    public boolean shouldClearMarkers( ValidationEvent event )
    {
        return true;
    }

    protected Map<String, Object> checkClass(
        IJavaProject javaProject, Node classSpecifier, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, String preferenceKey, String errorMessage )
    {

        String className = NodeUtil.getTextContent( classSpecifier );

        if( className != null && className.length() > 0 )
        {
            IType type = null;

            try
            {
                type = javaProject.findType( className );
            }
            catch( JavaModelException e )
            {
                return null;
            }

            if( type == null || !type.exists() )
            {
                String msg = MessageFormat.format( errorMessage, new Object[] { className } );

                return createMarkerValues(
                    preferenceNodeQualifier, preferenceScopes, preferenceKey, (IDOMNode) classSpecifier, msg );

            }
        }

        return null;
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
            if( classResource.endsWith( ".properties" ) && warnPropertiesSuffix )
            {
                String msg =
                    MessageFormat.format(
                        "The class resource {0} should not end with .properties", new Object[] { classResource } );
                return createMarkerValues(
                    preferenceNodeQualifier, preferenceScopes, preferenceKey, (IDOMNode) classResourceSpecifier, msg );
            }

            try
            {
                IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath( true );

                IResource classResourceValue = null;
                for( IClasspathEntry entry : classpathEntries )
                {
                    if( entry.getEntryKind() == IClasspathEntry.CPE_SOURCE )
                    {
                        IPath entryPath = entry.getPath();
                        IPath classResourcePath = entryPath.append( classResource );

                        classResourceValue =
                            javaProject.getJavaModel().getWorkspace().getRoot().findMember( classResourcePath );

                        if( classResourceValue != null )
                        {
                            break;
                        }

                        IPath qualifiedResourcePath = entryPath.append( classResource.replaceAll( "\\.", "/" ) );

                        classResourceValue =
                            javaProject.getJavaModel().getWorkspace().getRoot().findMember( qualifiedResourcePath );

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

                            IPath propertiesClassResourcePath = parent.append( resourceName + ".properties" );

                            classResourceValue =
                                javaProject.getJavaModel().getWorkspace().getRoot().findMember(
                                    propertiesClassResourcePath );

                            if( classResourceValue != null )
                            {
                                break;
                            }

                            propertiesClassResourcePath =
                                parent.append( resourceName.replaceAll( "\\.", "/" ) + ".properties" );

                            classResourceValue =
                                javaProject.getJavaModel().getWorkspace().getRoot().findMember(
                                    propertiesClassResourcePath );

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
        Node docrootResourceSpecifier, IProject project, String preferenceNodeQualifier,
        IScopeContext[] preferenceScopes, String preferenceKey, String errorMessage )
    {

        String docrootResource = NodeUtil.getTextContent( docrootResourceSpecifier );

        if( docrootResource != null && docrootResource.length() > 0 )
        {
            IFolder docroot = CoreUtil.getDocroot( project );

            IResource docrootResourceValue = docroot.findMember( new Path( docrootResource ) );

            if( docrootResourceValue == null )
            {
                String msg = MessageFormat.format( errorMessage, new Object[] { docrootResource } );

                return createMarkerValues(
                    preferenceNodeQualifier, preferenceScopes, preferenceKey, (IDOMNode) docrootResourceSpecifier, msg );
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

        return markerValues;
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

}
