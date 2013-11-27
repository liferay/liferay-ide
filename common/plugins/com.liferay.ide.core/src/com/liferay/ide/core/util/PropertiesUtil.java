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

package com.liferay.ide.core.util;

import com.liferay.ide.core.LiferayCore;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class PropertiesUtil
{
    // Get the language properties files referenced from portlet.xml and liferay-hook.xml
    public static IFile[] getLanguagePropertiesFiles( IProject proj )
    {
        Set<IFile> retval = new HashSet<IFile>();

        final IFolder[] srcFolders = CoreUtil.getSrcFolders( proj );

        if( srcFolders.length < 1 )
        {
            return null;
        }

        for( IFolder srcFolder : srcFolders )
        {
            if( CoreUtil.isLiferayProject( proj ) )
            {
                IFile portletXml = null;

                IFile liferayHookXml = null;

                if( proj != null )
                {
                    IFolder defaultDocrootFolder = CoreUtil.getDefaultDocrootFolder( proj );

                    if( defaultDocrootFolder != null )
                    {
                        portletXml = defaultDocrootFolder.getFile( new Path( "WEB-INF/portlet.xml" ) );
                        liferayHookXml = defaultDocrootFolder.getFile( new Path( "WEB-INF/liferay-hook.xml" ) );
                    }
                }

                try
                {
                    // Search all resource bundle and supported locale files referenced in portlet.xml.
                    if( portletXml != null )
                    {
                        IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead( portletXml );

                        if( model instanceof IDOMModel )
                        {
                            final IDOMDocument document = ( (IDOMModel) model ).getDocument();

                            final NodeList portlets = document.getElementsByTagName( "portlet" );
                            final NodeList allResourceBundles = document.getElementsByTagName( "resource-bundle" );
                            final NodeList allSupportedLocales = document.getElementsByTagName( "supported-locale" );

                            if( portlets != null && portlets.getLength() > 0 )
                            {
                                for( int i = 0; i < portlets.getLength(); i++ )
                                {
                                    Node portlet = portlets.item( i );
                                    Node resourceBundle = null;
                                    Set<Node> supportedLocales = new HashSet<Node>();

                                    if( allResourceBundles.getLength() > 0 )
                                    {
                                        for( int j = 0; j < allResourceBundles.getLength(); j++ )
                                        {
                                            if( allResourceBundles.item( j ).getParentNode().equals( portlet ) )
                                            {
                                                resourceBundle = allResourceBundles.item( j );
                                            }
                                        }

                                        if( allSupportedLocales.getLength() > 0 )
                                        {
                                            for( int k = 0; k < allSupportedLocales.getLength(); k++ )
                                            {
                                                if( allSupportedLocales.item( k ).getParentNode().equals( portlet ) )
                                                {
                                                    supportedLocales.add( allSupportedLocales.item( k ) );
                                                }
                                            }
                                        }

                                        final String resourceBundleVal = NodeUtil.getTextContent( resourceBundle );

                                        IFile resourceBundleFile =
                                            proj.getWorkspace().getRoot().getFile(
                                                srcFolder.getFullPath().append(
                                                    resourceBundleVal.replaceAll( "\\.", "/" ) + ".properties" ) );

                                        if( resourceBundleFile.exists() )
                                        {
                                            retval.add( resourceBundleFile );
                                        }

                                        if( supportedLocales.size() > 0 )
                                        {
                                            IFile supportedLocaleFile = null;

                                            for( Node supportedLocale : supportedLocales )
                                            {
                                                String supportedLocaleVal = NodeUtil.getTextContent( supportedLocale );

                                                supportedLocaleFile =
                                                    proj.getWorkspace().getRoot().getFile(
                                                        srcFolder.getFullPath().append(
                                                            resourceBundleVal.replaceAll( "\\.", "/" ) + "_" +
                                                                supportedLocaleVal + ".properties" ) );

                                                if( supportedLocaleFile.exists() )
                                                {
                                                    retval.add( supportedLocaleFile );
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Search all language properties files referenced in liferay-hook.xml
                    if( liferayHookXml != null )
                    {
                        IStructuredModel model =
                            StructuredModelManager.getModelManager().getModelForRead( liferayHookXml );

                        if( model instanceof IDOMModel )
                        {
                            final IDOMDocument document = ( (IDOMModel) model ).getDocument();

                            final NodeList languagePropertiesList =
                                document.getElementsByTagName( "language-properties" );

                            if( languagePropertiesList.getLength() > 0 )
                            {
                                Node languageProperties = null;

                                for( int i = 0; i < languagePropertiesList.getLength(); i++ )
                                {
                                    languageProperties = languagePropertiesList.item( i );

                                    String languagePropertiesVal = NodeUtil.getTextContent( languageProperties );

                                    // In liferay-hook.xml, the content of language-properties can use a wildcard
                                    // "*", need to search all the files whose names match the content.
                                    if( languagePropertiesVal.contains( StringPool.ASTERISK ) )
                                    {
                                        String languagePropertiesValRegex =
                                            languagePropertiesVal.replaceAll( "\\*", ".*" );

                                        IResource entryResource =
                                            proj.getWorkspace().getRoot().findMember(
                                                srcFolder.getFullPath().toString() );

                                        if( entryResource != null )
                                        {
                                            IFile[] languagePropertiesFiles = visitPropertiesFiles(
                                                entryResource, languagePropertiesValRegex );

                                            if( languagePropertiesFiles != null && languagePropertiesFiles.length > 0 )
                                            {
                                                for( IFile file : languagePropertiesFiles )
                                                {
                                                    if( file.exists() )
                                                    {
                                                        retval.add( file );
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    else
                                    {
                                        IFile languagePropertiesFile =
                                            proj.getWorkspace().getRoot().getFile(
                                                srcFolder.getFullPath().append( languagePropertiesVal ) );

                                        if( languagePropertiesFile.exists() )
                                        {
                                            retval.add( languagePropertiesFile );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                catch( Exception e )
                {
                    LiferayCore.logError( e );
                }
            }
        }

        return retval.toArray( new IFile[retval.size()] );
    }

    public static IFile[] visitPropertiesFiles( IResource entryResource, String relativePath )
    {
        return new PropertiesVisitor().visitPropertiesFiles( entryResource, relativePath );
    }

    private static class PropertiesVisitor implements IResourceProxyVisitor
    {

        IResource entryResource = null;
        String relativePathToEntry = null;
        Set<IFile> resources = new HashSet<IFile>();

        public boolean visit( IResourceProxy resourceProxy )
        {
            if( resourceProxy.getType() == IResource.FILE && resourceProxy.getName().endsWith( ".properties" ) )
            {
                String relativePath =
                    resourceProxy.requestFullPath().makeRelativeTo( entryResource.getFullPath() ).toString();

                if( relativePath.matches( relativePathToEntry ) )
                {
                    resources.add( (IFile) resourceProxy.requestResource() );
                }
            }

            return true;
        }

        public IFile[] visitPropertiesFiles( IResource entryResource, String relativePath )
        {
            this.entryResource = entryResource;
            this.relativePathToEntry = relativePath;

            try
            {
                entryResource.accept( this, IContainer.EXCLUDE_DERIVED );
            }
            catch( CoreException e )
            {
                LiferayCore.logError( e );
            }

            return resources.toArray( new IFile[resources.size()] );
        }
    }
}
